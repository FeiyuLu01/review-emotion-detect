# server.py
import os
import re
from typing import List

import torch
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from transformers import AutoModelForSeq2SeqLM, AutoTokenizer

# ------------------------- Model & device -------------------------
# 本地模型目录（前面你已把 flan-t5-base 下载到 models/flan-t5-base）
MODEL_PATH = os.getenv("MODEL_PATH", "models/flan-t5-base")

def pick_device() -> str:
    if torch.cuda.is_available():
        return "cuda"
    if getattr(torch.backends, "mps", None) and torch.backends.mps.is_available():
        return "mps"
    return "cpu"

DEVICE = pick_device()

print(f"Loading model from {MODEL_PATH} on {DEVICE} …")
tokenizer = AutoTokenizer.from_pretrained(MODEL_PATH, local_files_only=True)
model = AutoModelForSeq2SeqLM.from_pretrained(MODEL_PATH, local_files_only=True)
model.to(DEVICE).eval()

# ------------------------- FastAPI app -------------------------
app = FastAPI(title="Local Rewrite API (FLAN-T5)")

# 开发阶段允许本地前端跨域；上线请收紧域名白名单
app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        "http://localhost:5173",
        "http://127.0.0.1:5173",
        "*",  # 生产环境请移除
    ],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# ------------------------- Schemas -------------------------
class RewriteReq(BaseModel):
    text: str
    tone: str = "neutral"  # neutral | positive | polite

class RewriteResp(BaseModel):
    text: str

# ------------------------- Prompt -------------------------
TONE_MAP = {
    "neutral": "neutral and objective",
    "positive": "positive, constructive, encouraging",
    "polite":   "polite, respectful, non-confrontational",
}

PROMPT_TEMPLATE = """You are a careful editor.
Rewrite the following review in a {tone_text} tone.

Hard rules (must follow):
- Keep the original factual meaning and **do NOT change sentiment polarity**.
  - WRONG: "useless" -> "useful"
  - RIGHT : "useless" -> "not very useful"
  - WRONG: "awful" -> "great"
  - RIGHT : "awful" -> "poor"
- Remove profanity/insults by replacing with mild words (e.g., "fucking" -> "very").
- Soften strong subjective words (e.g., useless/awful/hate/really/very) without flipping polarity.
- Do NOT add apologies, sympathy, or new facts.
- Keep roughly similar length and structure.
- Output only the rewritten sentence.

Input: "{src}"
Output:
""".rstrip()

def build_prompt(tone: str, src: str) -> str:
    tone_text = TONE_MAP.get(tone, TONE_MAP["neutral"])
    return PROMPT_TEMPLATE.format(tone_text=tone_text, src=src.replace('"', '\\"'))

# ------------------------- Post-process helpers -------------------------
def normalize_space(s: str) -> str:
    return re.sub(r"\s+", " ", s).strip()

def tokens(s: str) -> List[str]:
    return re.findall(r"[a-z']+", s.lower())

def jaccard(a: List[str], b: List[str]) -> float:
    sa, sb = set(a), set(b)
    if not sa or not sb:
        return 0.0
    return len(sa & sb) / len(sa | sb)

def content_overlap_ok(src: str, out: str, min_jaccard: float = 0.35) -> bool:
    return jaccard(tokens(src), tokens(out)) >= min_jaccard

def must_change_guard(src: str, out: str) -> bool:
    """避免几乎没改动（同样的标点/大小写改动不算）。"""
    return normalize_space(src).lower() != normalize_space(out).lower()

# 轻量化“软化词典”（只减弱强烈程度，不改变极性）
SOFTEN_LEXICON = [
    (r"\bfuck(ing)?\b", "very"),
    (r"\bshit(ty)?\b", "bad"),
    (r"\buseless\b", "not very useful"),
    (r"\bawful\b", "poor"),
    (r"\bterrible\b", "poor"),
    (r"\bhate\b", "dislike"),
    (r"\bgarbage\b", "poor quality"),
    (r"\bsucks?\b", "is not good"),
    (r"\breally\b", ""),
    (r"\bvery\b", ""),
]

def soften_emotion_words(s: str) -> str:
    out = s
    for pat, repl in SOFTEN_LEXICON:
        out = re.sub(pat, repl, out, flags=re.IGNORECASE)
    # 清理多余空格与标点空格
    out = normalize_space(re.sub(r"\s+([,.!?;:])", r"\1", out))
    # 句尾加句号（如需要）
    if out and out[-1].isalnum():
        out += "."
    return out

# ---- Polarity guard: very simple heuristic to avoid sentiment flip ----
POS_WORDS = {
    "useful","great","good","excellent","awesome","amazing","love","like",
    "satisfied","fantastic","positive","pleased","wonderful","nice",
}
NEG_WORDS = {
    "useless","bad","awful","terrible","horrible","hate","dislike","poor",
    "worst","disgusting","garbage","broken","issue","problem","negative",
}

def has_pos(s: str) -> bool:
    t = set(tokens(s))
    return any(w in t for w in POS_WORDS)

def has_neg(s: str) -> bool:
    t = set(tokens(s))
    return any(w in t for w in NEG_WORDS)

def polarity_flip(src: str, out: str) -> bool:
    """Detect clear neg→pos or pos→neg flips."""
    src_pos, src_neg = has_pos(src), has_neg(src)
    out_pos, out_neg = has_pos(out), has_neg(out)
    if src_neg and not src_pos:
        # 源句负向：输出不应变成明显正向
        return out_pos and not out_neg
    if src_pos and not src_neg:
        # 源句正向：输出不应变成明显负向
        return out_neg and not out_pos
    return False

# ------------------------- Inference -------------------------
def generate_once(prompt: str) -> str:
    inputs = tokenizer(prompt, return_tensors="pt").to(DEVICE)
    with torch.no_grad():
        out_ids = model.generate(
            **inputs,
            max_new_tokens=96,
            temperature=0.4,
            top_p=0.9,
            do_sample=True,
            repetition_penalty=1.1,
        )
    text = tokenizer.decode(out_ids[0], skip_special_tokens=True).strip()
    # 如果模型把模板复述出来，截到 Output: 之后
    if "Output:" in text:
        text = text.split("Output:", 1)[1].strip()
    return text.strip(' "\'')

# ------------------------- API -------------------------
@app.post("/rewrite", response_model=RewriteResp)
def rewrite(req: RewriteReq):
    src = req.text.strip()
    if not src:
        return RewriteResp(text="")

    prompt = build_prompt(req.tone, src)
    out_text = generate_once(prompt)

    # 极性守卫：发现翻转就回退到规则化软化
    if polarity_flip(src, out_text):
        out_text = soften_emotion_words(src)

    # 兜底：若几乎没改动/内容重叠过低/模型输出为空 → 规则化软化
    if (not out_text) or (not must_change_guard(src, out_text)) or (not content_overlap_ok(src, out_text)):
        out_text = soften_emotion_words(src)

    return RewriteResp(text=out_text)
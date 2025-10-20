# server.py
import os
import re
from typing import List

import torch
import torch.nn.functional as F
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from transformers import (
    AutoTokenizer,
    AutoModelForSeq2SeqLM,
    AutoModelForSequenceClassification,
)

# =========================
# Device & small runtime opts
# =========================
def pick_device() -> str:
    """Pick the best available device: CUDA -> MPS (Apple Silicon) -> CPU."""
    if torch.cuda.is_available():
        return "cuda"
    if getattr(torch.backends, "mps", None) and torch.backends.mps.is_available():
        return "mps"
    return "cpu"


DEVICE = pick_device()
torch.set_num_threads(max(1, int(os.getenv("TORCH_THREADS", "1"))))

# =========================
# Model paths (local-first)
# - You can put models under ./models/...
# - Set local_files_only via env LOCAL_ONLY ("1" by default)
# =========================
MODEL_PATH_T5 = os.getenv("MODEL_PATH_T5", "models/flan-t5-base")
MODEL_PATH_CLS = os.getenv(
    "MODEL_PATH_CLS", "models/SamLowe/roberta-base-go_emotions"
)
LOCAL_ONLY = os.getenv("LOCAL_ONLY", "1") == "1"

print(f"[Boot] DEVICE = {DEVICE}")
print(f"[Boot] MODEL_PATH_T5  = {MODEL_PATH_T5}")
print(f"[Boot] MODEL_PATH_CLS = {MODEL_PATH_CLS}")
print(f"[Boot] LOCAL_ONLY     = {LOCAL_ONLY}")

# =========================
# Load models
# =========================
def load_t5():
    print(f"[Load] T5 from {MODEL_PATH_T5}")
    tok = AutoTokenizer.from_pretrained(MODEL_PATH_T5, local_files_only=LOCAL_ONLY)
    mod = AutoModelForSeq2SeqLM.from_pretrained(
        MODEL_PATH_T5, local_files_only=LOCAL_ONLY
    ).to(DEVICE).eval()
    return tok, mod


def load_cls():
    print(f"[Load] Classifier from {MODEL_PATH_CLS}")
    tok = AutoTokenizer.from_pretrained(MODEL_PATH_CLS, local_files_only=LOCAL_ONLY)
    mod = AutoModelForSequenceClassification.from_pretrained(
        MODEL_PATH_CLS, local_files_only=LOCAL_ONLY
    ).to(DEVICE).eval()
    return tok, mod


t5_tok, t5_mod = load_t5()
cls_tok, cls_mod = load_cls()

# =========================
# FastAPI app + CORS
# =========================
app = FastAPI(title="MoodLens API (Rewrite + Classify)")

app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        "http://localhost:5173",
        "http://127.0.0.1:5173",
        "*",  # For demo; tighten in production
    ],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# =========================
# Schemas
# =========================
class RewriteReq(BaseModel):
    text: str
    tone: str = "neutral"  # neutral | positive | polite


class RewriteResp(BaseModel):
    text: str


class ClassifyReq(BaseModel):
    text: str


class ClassifyResp(BaseModel):
    results: list  # [{label, score}, ...]


# =========================
# Prompt & guards (from your logic)
# =========================
TONE_MAP = {
    "neutral": "neutral and objective",
    "positive": "positive, constructive, encouraging",
    "polite": "polite, respectful, non-confrontational",
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
    return normalize_space(src).lower() != normalize_space(out).lower()


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
    out = re.sub(r"\bhave bad ([a-z]+)\b", r"have a bad \1", out, flags=re.IGNORECASE)
    out = normalize_space(re.sub(r"\s+([,.!?;:])", r"\1", out))
    if out and out[-1].isalnum():
        out += "."
    return out


POS_WORDS = {
    "useful",
    "great",
    "good",
    "excellent",
    "awesome",
    "amazing",
    "love",
    "like",
    "satisfied",
    "fantastic",
    "positive",
    "pleased",
    "wonderful",
    "nice",
}
NEG_WORDS = {
    "useless",
    "bad",
    "awful",
    "terrible",
    "horrible",
    "hate",
    "dislike",
    "poor",
    "worst",
    "disgusting",
    "garbage",
    "broken",
    "issue",
    "problem",
    "negative",
    "shit",
    "shitty",
    "sucks",
}


def has_pos(s: str) -> bool:
    t = set(tokens(s))
    return any(w in t for w in POS_WORDS)


def has_neg(s: str) -> bool:
    t = set(tokens(s))
    return any(w in t for w in NEG_WORDS)


def polarity_flip(src: str, out: str) -> bool:
    src_pos, src_neg = has_pos(src), has_neg(src)
    out_pos, out_neg = has_pos(out), has_neg(out)
    if src_neg and not src_pos:
        return out_pos and not out_neg
    if src_pos and not src_neg:
        return out_neg and not out_pos
    return False


SOFTEN_PATTERNS = [re.compile(pat, re.IGNORECASE) for pat, _ in SOFTEN_LEXICON]


def needs_soften(s: str) -> bool:
    return any(p.search(s) for p in SOFTEN_PATTERNS)


# =========================
# Inference helpers
# =========================
def t5_generate_once(prompt: str) -> str:
    inputs = t5_tok(prompt, return_tensors="pt").to(DEVICE)
    with torch.no_grad():
        out_ids = t5_mod.generate(
            **inputs,
            max_new_tokens=96,
            temperature=0.4,
            top_p=0.9,
            do_sample=True,
            repetition_penalty=1.1,
        )
    text = t5_tok.decode(out_ids[0], skip_special_tokens=True).strip()
    if "Output:" in text:
        text = text.split("Output:", 1)[1].strip()
    return text.strip(' "\'')


# =========================
# Endpoints
# =========================
@app.get("/health")
def health():
    return {"ok": True}


@app.post("/classify", response_model=ClassifyResp)
def classify(req: ClassifyReq):
    """
    Return scores for all labels (sorted desc).
    Response: {"results": [{"label": "...", "score": 0.xx}, ...]}
    """
    text = (req.text or "").strip()
    if not text:
        return {"results": []}
    inputs = cls_tok(text, return_tensors="pt").to(DEVICE)
    with torch.no_grad():
        logits = cls_mod(**inputs).logits
        probs = F.softmax(logits, dim=-1)[0].tolist()

    id2label = cls_mod.config.id2label
    items = [{"label": id2label[i], "score": float(p)} for i, p in enumerate(probs)]
    items.sort(key=lambda x: x["score"], reverse=True)
    return {"results": items}


@app.post("/rewrite", response_model=RewriteResp)
def rewrite(req: RewriteReq):
    """
    Generate a rewrite with guards:
    - build guarded prompt
    - generate once
    - if polarity flip -> fallback to rule-based softening on SOURCE
    - if trivial/no-overlap/empty -> fallback to rule-based softening on SOURCE
    - final pass: ensure no strong/profane words remain
    """
    src = (req.text or "").strip()
    if not src:
        return RewriteResp(text="")

    prompt = build_prompt(req.tone, src)
    out_text = t5_generate_once(prompt)

    if polarity_flip(src, out_text):
        out_text = soften_emotion_words(src)

    if (not out_text) or (not must_change_guard(src, out_text)) or (
        not content_overlap_ok(src, out_text)
    ):
        out_text = soften_emotion_words(src)

    if needs_soften(out_text):
        out_text = soften_emotion_words(out_text)

    return RewriteResp(text=out_text)
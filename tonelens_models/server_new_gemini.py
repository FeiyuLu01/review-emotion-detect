# server.py (concise inline comments in English only; code logic unchanged)

# standard library and third-party imports
import os
from dotenv import load_dotenv
from typing import List, Optional
import torch
import torch.nn.functional as F
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from transformers import (
    AutoTokenizer,
    AutoModelForSequenceClassification,
)
from google import genai


# -----------------------
# Device & runtime
# -----------------------
def pick_device() -> str:
    # prefer CUDA, then Apple MPS, otherwise fallback to CPU
    if torch.cuda.is_available():
        return "cuda"
    if getattr(torch.backends, "mps", None) and torch.backends.mps.is_available():
        return "mps"
    return "cpu"


# runtime device choice and thread config
DEVICE = os.getenv("DEVICE", pick_device())
torch.set_num_threads(max(1, int(os.getenv("TORCH_THREADS", "1"))))

# -----------------------
# Model paths & flags
# -----------------------
# local classifier path and local-only flag (default: use local files only)
MODEL_PATH_CLS = os.getenv("MODEL_PATH_CLS", "/home/ubuntu/models/roberta-base_go_emotions")
LOCAL_ONLY = os.getenv("LOCAL_ONLY", "1") == "1"  # set 0 to allow hub downloads

# load .env before reading GEMINI_API_KEY
load_dotenv(dotenv_path="/home/ubuntu/new_gemini/.env")

# Gemini / generative API configuration (API key must be provided via env)
GEMINI_MODEL = os.getenv("GEMINI_MODEL", "gemini-2.5-flash")
API_KEY = os.getenv("GEMINI_API_KEY")  # Set in environment variable
if not API_KEY:
    raise RuntimeError("Missing environment variable GEMINI_API_KEY")

# initialize genai client for Gemini calls
client = genai.Client(api_key=API_KEY)


# -----------------------
# Load classifier
# -----------------------
def load_cls():
    # load tokenizer and sequence-classification model (local or cached)
    print(f"[Load] Classifier from {MODEL_PATH_CLS}")
    tok = AutoTokenizer.from_pretrained(MODEL_PATH_CLS, local_files_only=LOCAL_ONLY)
    mod = AutoModelForSequenceClassification.from_pretrained(
        MODEL_PATH_CLS, local_files_only=LOCAL_ONLY
    ).to(DEVICE).eval()
    return tok, mod


# load classifier at module import (may be slow)
cls_tok, cls_mod = load_cls()

# -----------------------
# FastAPI app
# -----------------------
# create FastAPI app and enable CORS (open for development; tighten in production)
app = FastAPI(title="MoodLens API (Rewrite + Classify)")
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # tighten in production
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# -----------------------
# Schemas
# -----------------------
# request/response data models (Pydantic)
class RewriteReq(BaseModel):
    text: str
    tone: str = "neutral"  # neutral | positive | polite


class RewriteResp(BaseModel):
    text: str


class ClassifyReq(BaseModel):
    text: str


class LabelScore(BaseModel):
    label: str
    score: float


class ClassifyResp(BaseModel):
    results: List[LabelScore]


# -----------------------
# Prompt & guards (same prompt template)
# -----------------------
# tone mapping: short key -> descriptive text used in the prompt
TONE_MAP = {
    "neutral": "neutral and objective",
    "positive": "positive, constructive, encouraging",
    "polite": "polite, respectful, non-confrontational",
}

# prompt template: explicit instructions for the generation model
PROMPT_TEMPLATE = """You are a careful editor.

Rewrite the following review in a {tone_text} tone by strictly following the steps and rules below.

Steps
1) Replace any rude/offensive words or any phrase containing them
   (e.g., shit, fuck, trash, dumb, and their variants such as "shitty", "fucking", "trash product", "dumb idea")
   with normal, non-offensive synonyms while keeping the original meaning unchanged and without exaggeration.
2) After the replacements, proofread the entire sentence for grammar and fluency.
3) Adjust the sentence to the requested tone ({tone_text}: neutral | positive | polite) without changing facts
   or flipping the overall sentiment polarity. Proofread again to ensure the final sentence is smooth and correct.

Hard rules
- Keep the factual meaning and do NOT change sentiment polarity.
- Do NOT add apologies, sympathy, new facts, or subjective judgments.
- Make minimal edits; keep length and structure roughly the same.
- Output only the final rewritten review (no explanations).

Input: "{src}"
Output:
""".rstrip()


def build_prompt(tone: str, src: str) -> str:
    # map tone to descriptive text and inject the source (escape quotes)
    tone_text = TONE_MAP.get(tone, TONE_MAP["neutral"])
    return PROMPT_TEMPLATE.format(tone_text=tone_text, src=src.replace('"', '\\"'))


# -----------------------
# Gemini / external API helpers
# -----------------------
def gemini_generate(prompt: str) -> Optional[str]:
    """
    Call google.genai models.generate_content.
    - parameters passed as contents (compatible with SDK variants)
    - supports parsing resp.text and candidates[*].content.parts[*].text
    - on error returns None and upper layer decides fallback
    """
    try:
        # use SDK's models.generate_content (preferred)
        resp = client.models.generate_content(
            model=GEMINI_MODEL,
            contents=prompt  # passing a plain string is acceptable for many SDK versions
        )
    except Exception as e:
        # log primary call failure and return None (do not raise)
        print("[Gemini] primary call failed:", str(e))
        return None

    # ---- parse response ----
    try:
        # 1) Newer SDKs may expose resp.text directly
        if hasattr(resp, "text") and resp.text:
            return str(resp.text).strip()

        # 2) SDK objects may contain candidates -> content -> parts -> text
        cand = getattr(resp, "candidates", None)
        if cand and len(cand) > 0:
            c0 = cand[0]
            content = getattr(c0, "content", None)
            parts = getattr(content, "parts", None) if content else None
            if parts:
                texts = []
                for p in parts:
                    # support object attribute or dict-like part
                    t = getattr(p, "text", None) or (p.get("text") if isinstance(p, dict) else None)
                    if t:
                        texts.append(t)
                if texts:
                    # join all parts and return
                    return " ".join(texts).strip()

        # 3) fallback: convert resp to dict and extract common keys
        j = None
        if hasattr(resp, "to_dict"):
            try:
                j = resp.to_dict()
            except Exception:
                j = None
        if isinstance(resp, dict):
            j = resp

        if j:
            # handle candidates list form (dict)
            if isinstance(j.get("candidates"), list) and j["candidates"]:
                c0 = j["candidates"][0]
                if isinstance(c0, dict):
                    content = c0.get("content")
                    if isinstance(content, dict):
                        parts = content.get("parts")
                        if isinstance(parts, list) and parts:
                            # collect text from each part
                            texts = [p.get("text") for p in parts if isinstance(p, dict) and p.get("text")]
                            if texts:
                                return " ".join(texts).strip()
                # also try common keys directly on candidate
                for k in ("content", "text", "output", "response"):
                    v = c0.get(k)
                    if isinstance(v, str) and v.strip():
                        return v.strip()

            # top-level fallback keys
            for k in ("text", "content", "output", "response"):
                v = j.get(k)
                if isinstance(v, str) and v.strip():
                    return v.strip()

        # 4) final fallback: stringify the response object
        s = str(resp)
        if s and s.strip():
            return s.strip()
    except Exception as e:
        # parsing error: log and return None
        print("[Gemini] parse error:", str(e))
        return None

    return None


def rewrite_once(text: str, tone: str) -> str:
    # generate rewritten text; if generation fails, return original input (no extra post-processing)
    src = (text or "").strip()
    if not src:
        return ""
    prompt = build_prompt(tone, src)
    out = gemini_generate(prompt)
    if not out:
        # fail-open: return original text on generation failure
        return src
    # trim whitespace and surrounding quotes, then return
    return out.strip().strip('"\'')


# -----------------------
# Endpoints (unchanged)
# -----------------------
@app.get("/health")
def health():
    # simple health check endpoint
    return {"ok": True}


@app.post("/classify", response_model=ClassifyResp)
def classify(req: ClassifyReq):
    # text classification: return labels sorted by probability
    text = (req.text or "").strip()
    if not text:
        return ClassifyResp(results=[])
    inputs = cls_tok(text, return_tensors="pt").to(DEVICE)
    with torch.no_grad():
        logits = cls_mod(**inputs).logits
        probs = F.softmax(logits, dim=-1)[0].tolist()
    id2label = cls_mod.config.id2label
    items = [LabelScore(label=id2label[i], score=float(p)) for i, p in enumerate(probs)]
    items.sort(key=lambda x: x.score, reverse=True)
    return ClassifyResp(results=items)


@app.post("/rewrite", response_model=RewriteResp)
def rewrite(req: RewriteReq):
    # call rewrite_once and return rewritten text
    src = (req.text or "").strip()
    if not src:
        return RewriteResp(text="")
    tone = req.tone or "neutral"
    out_text = rewrite_once(src, tone)
    return RewriteResp(text=out_text)

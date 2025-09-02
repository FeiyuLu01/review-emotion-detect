# server.py
import os
import re
from typing import List

import torch
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from transformers import AutoModelForSeq2SeqLM, AutoTokenizer

# ------------------------- Model & Device -------------------------
# Local model directory (you've downloaded flan-t5-base to models/flan-t5-base)
MODEL_PATH = os.getenv("MODEL_PATH", "models/flan-t5-base")


def pick_device() -> str:
    """Pick the best available device: CUDA -> MPS (Apple Silicon) -> CPU."""
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

# ------------------------- FastAPI App -------------------------
app = FastAPI(title="Local Rewrite API (FLAN-T5)")

# During development we allow local front-ends via CORS; tighten this for production
app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        "http://localhost:5173",
        "http://127.0.0.1:5173",
        "*",  # Remove in production
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
    """Fill the prompt with the tone text and user source (escape quotes)."""
    tone_text = TONE_MAP.get(tone, TONE_MAP["neutral"])
    return PROMPT_TEMPLATE.format(tone_text=tone_text, src=src.replace('"', '\\"'))


# ------------------------- Post-process Helpers -------------------------
def normalize_space(s: str) -> str:
    """Collapse multiple whitespaces and trim."""
    return re.sub(r"\s+", " ", s).strip()


def tokens(s: str) -> List[str]:
    """Lowercase tokenization for simple lexical checks."""
    return re.findall(r"[a-z']+", s.lower())


def jaccard(a: List[str], b: List[str]) -> float:
    """Token-level Jaccard similarity."""
    sa, sb = set(a), set(b)
    if not sa or not sb:
        return 0.0
    return len(sa & sb) / len(sa | sb)


def content_overlap_ok(src: str, out: str, min_jaccard: float = 0.35) -> bool:
    """Ensure the output still overlaps sufficiently with the source content."""
    return jaccard(tokens(src), tokens(out)) >= min_jaccard


def must_change_guard(src: str, out: str) -> bool:
    """Reject trivial changes (punctuation/casing only)."""
    return normalize_space(src).lower() != normalize_space(out).lower()


# Lightweight “softening lexicon” (reduce intensity without flipping polarity)
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
    """
    Rule-based softening:
    - Replace strong/profane words with milder alternatives.
    - Clean extra whitespaces and spaces before punctuation.
    - Ensure a trailing period if the text ends with an alphanumeric.
    """
    out = s
    for pat, repl in SOFTEN_LEXICON:
        out = re.sub(pat, repl, out, flags=re.IGNORECASE)

    # Optional micro-fix: minimal grammar tweak for "have bad X" -> "have a bad X"
    out = re.sub(
        r"\bhave bad ([a-z]+)\b", r"have a bad \1", out, flags=re.IGNORECASE
    )

    # Normalize spaces and remove spaces before punctuation
    out = normalize_space(re.sub(r"\s+([,.!?;:])", r"\1", out))

    # Append period if needed
    if out and out[-1].isalnum():
        out += "."
    return out


# ---- Polarity guard: very simple heuristic to avoid sentiment flip ----
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
    # Make the guard more sensitive to common profanities/colloquialisms
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
    """
    Detect clear neg→pos or pos→neg flips.
    - If the source is clearly negative (no positive words), output must not become clearly positive (without negatives).
    - If the source is clearly positive (no negatives), output must not become clearly negative (without positives).
    """
    src_pos, src_neg = has_pos(src), has_neg(src)
    out_pos, out_neg = has_pos(out), has_neg(out)
    if src_neg and not src_pos:
        return out_pos and not out_neg
    if src_pos and not src_neg:
        return out_neg and not out_pos
    return False


# --------- Softening end-check: always sanitize remaining strong words ----------
SOFTEN_PATTERNS = [re.compile(pat, re.IGNORECASE) for pat, _ in SOFTEN_LEXICON]


def needs_soften(s: str) -> bool:
    """Return True if the string still contains any word that should be softened."""
    return any(p.search(s) for p in SOFTEN_PATTERNS)


# ------------------------- Inference -------------------------
def generate_once(prompt: str) -> str:
    """Single-shot generation with conservative decoding settings."""
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
    # If the model echoed the template, keep only the part after 'Output:'
    if "Output:" in text:
        text = text.split("Output:", 1)[1].strip()
    return text.strip(' "\'')


# ------------------------- API -------------------------
@app.post("/rewrite", response_model=RewriteResp)
def rewrite(req: RewriteReq):
    """
    Rewrite endpoint:
    - Build a guarded prompt.
    - Generate once.
    - If polarity flips -> fall back to rule-based softening of the *source*.
    - If output is too similar/too dissimilar/empty -> fall back to rule-based softening of the *source*.
    - End-check: if output still contains strong/profane words -> soften the *output*.
    """
    src = req.text.strip()
    if not src:
        return RewriteResp(text="")

    prompt = build_prompt(req.tone, src)
    out_text = generate_once(prompt)

    # Polarity guard: if a flip is detected, fall back to safe rule-based softening
    if polarity_flip(src, out_text):
        out_text = soften_emotion_words(src)

    # Fallbacks: trivial change / low overlap / empty model output
    if (not out_text) or (not must_change_guard(src, out_text)) or (
        not content_overlap_ok(src, out_text)
    ):
        out_text = soften_emotion_words(src)

    # Final sanitization: always ensure profanity/strong words are softened if still present
    if needs_soften(out_text):
        out_text = soften_emotion_words(out_text)

    return RewriteResp(text=out_text)
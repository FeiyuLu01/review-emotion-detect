import os
import re
from pathlib import Path
from dotenv import load_dotenv
from google import genai

BASE_DIR = Path(__file__).resolve().parent
load_dotenv(BASE_DIR / ".env")

GEMINI_MODEL = os.getenv("GEMINI_MODEL", "gemini-2.5-flash")
API_KEY = os.getenv("GEMINI_API_KEY") or os.getenv("GOOGLE_API_KEY")
if not API_KEY:
    raise RuntimeError("Missing GEMINI_API_KEY/GOOGLE_API_KEY in .env")

client = genai.Client(api_key=API_KEY)

def moderate_content(text: str) -> dict:
    """
    Content moderation: ALLOW / BLOCK
    Checks: hate, harassment, politics, sexual, violence, PII.
    """
    prompt = f"""
You are a content moderation system for an anonymous mood sharing platform.
Decide if the INPUT contains ANY of the following:

- discrimination or hate speech
- insults/abuse/harassment
- sensitive political content
- sexual or pornographic content
- graphic violence or gore
- personal identifiable information (full names, phone numbers, email addresses, home addresses, social media handles)

Reply exactly one word: ALLOW or BLOCK.

INPUT:
{text}
""".strip()

    try:
        resp = client.models.generate_content(
            model=GEMINI_MODEL,
            contents=prompt
        )
        raw = (getattr(resp, "text", "") or "").strip().upper()
        print("[Gemini Moderation]", raw)

        allowed = bool(re.search(r"\bALLOW\b", raw))
        return {
            "allowed": allowed,
            "reason": "Content approved" if allowed
                      else "Content blocked: contains hate/harassment/politics/sexual/violence/PII info."
        }

    except Exception as e:
        print(f"[Moderation Error] {e}")
        return {"allowed": False, "reason": "Error during moderation"}

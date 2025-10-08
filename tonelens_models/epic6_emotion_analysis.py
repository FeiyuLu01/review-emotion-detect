import os
import json
import hashlib
import re
from pathlib import Path
from google import genai
from dotenv import load_dotenv
from db_config import get_db_connection

BASE_DIR = Path(__file__).resolve().parent
load_dotenv(BASE_DIR / ".env")

GEMINI_MODEL = os.getenv("GEMINI_MODEL", "gemini-2.5-flash")
API_KEY = os.getenv("GEMINI_API_KEY") or os.getenv("GOOGLE_API_KEY")
if not API_KEY:
    raise RuntimeError("Missing GEMINI_API_KEY/GOOGLE_API_KEY in .env")

client = genai.Client(api_key=API_KEY)

class EmotionAnalyzer:
    
    @staticmethod
    def extract_emojis(text: str) -> str:
        """Extract all emojis from text"""
        emoji_pattern = re.compile(
            "["
            "\U0001F600-\U0001F64F"# Emoticons
            "\U0001F300-\U0001F5FF"# Misc Symbols and Pictographs
            "\U0001F680-\U0001F6FF"# Transport and Map Symbols      
            "\U0001F1E0-\U0001F1FF"# Regional Indicator Symbols
            "\U00002702-\U000027B0"# Dingbats
            "\U000024C2-\U0001F251"# Enclosed Alphanumeric Supplement
            "]+", 
            flags=re.UNICODE
        )
        emojis = emoji_pattern.findall(text)
        return ''.join(emojis)
    
    @staticmethod
    def analyze_emotion(post_id: int, content: str) -> dict:
        """Analyze emotion of a single post"""
        content_hash = hashlib.md5(content.encode()).hexdigest()
        
        cached = EmotionAnalyzer._check_cache(content_hash)
        if cached:
            return cached
        
        extracted_emojis = EmotionAnalyzer.extract_emojis(content)
        
        prompt = f"""
Analyze the emotional tone of this text (including any emojis). 
Reply ONLY in JSON format:
{{
    "emotion": "positive" or "neutral" or "negative",
    "keywords": ["keyword1", "keyword2", "keyword3"]
}}

TEXT:
{content}
        """
        
        try:
            response = client.models.generate_content(
                                model=GEMINI_MODEL,
                                contents=prompt
                                )
            raw = response.text.strip()
            
            if raw.startswith("```json"):
                raw = raw.replace("```json", "").replace("```", "").strip()
            if raw.endswith("```"):
                raw = raw[:-3].strip()
            
            result = json.loads(raw)
            result["emojis"] = extracted_emojis
            
            EmotionAnalyzer._save_to_cache(post_id, content_hash, result)
            
            return result
            
        except Exception as e:
            print(f"[Emotion Analysis Error] {e}")
            return {
                "emotion": "neutral", 
                "keywords": [], 
                "emojis": extracted_emojis
            }
    
    @staticmethod
    def _check_cache(content_hash: str):
        try:
            conn = get_db_connection()
            cursor = conn.cursor(dictionary=True)
            cursor.execute(
                """SELECT emotion_type, keywords, extracted_emojis 
                   FROM emotion_analysis 
                   WHERE content_hash = %s""",
                (content_hash,)
            )
            result = cursor.fetchone()
            cursor.close()
            conn.close()
            
            if result:
                return {
                    "emotion": result["emotion_type"],
                    "keywords": json.loads(result["keywords"]),
                    "emojis": result["extracted_emojis"] or ""
                }
            return None
        except Exception as e:
            print(f"[Cache Check Error] {e}")
            return None
    
    @staticmethod
    def _save_to_cache(post_id: int, content_hash: str, result: dict):
        try:
            conn = get_db_connection()
            cursor = conn.cursor()
            cursor.execute(
                """INSERT INTO emotion_analysis 
                   (post_id, content_hash, emotion_type, keywords, extracted_emojis)
                   VALUES (%s, %s, %s, %s, %s)
                   ON DUPLICATE KEY UPDATE 
                   emotion_type = VALUES(emotion_type),
                   keywords = VALUES(keywords),
                   extracted_emojis = VALUES(extracted_emojis)""",
                (
                    post_id, 
                    content_hash, 
                    result["emotion"], 
                    json.dumps(result["keywords"], ensure_ascii=False),
                    result.get("emojis", "")
                )
            )
            conn.commit()
            cursor.close()
            conn.close()
        except Exception as e:
            print(f"[Cache Save Error] {e}")
    
    @staticmethod
    def get_dashboard_data():
        """Get Epic 6 dashboard data"""
        conn = get_db_connection()
        cursor = conn.cursor(dictionary=True)
        
        # Word cloud
        cursor.execute("""
            SELECT keyword, COUNT(*) as frequency
            FROM (
                SELECT JSON_UNQUOTE(JSON_EXTRACT(keywords, CONCAT('$[', idx, ']'))) as keyword
                FROM emotion_analysis ea,
                (SELECT 0 as idx UNION SELECT 1 UNION SELECT 2 
                 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) numbers
                WHERE ea.analyzed_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)
                  AND JSON_LENGTH(ea.keywords) > idx
            ) keywords_table
            WHERE keyword IS NOT NULL AND keyword != ''
            GROUP BY keyword
            ORDER BY frequency DESC
            LIMIT 50
        """)
        word_cloud = [{"text": row["keyword"], "size": row["frequency"], "count": row["frequency"]} 
                      for row in cursor.fetchall()]
        
        # Top 10
        cursor.execute("""
            SELECT keyword, COUNT(*) as count
            FROM (
                SELECT JSON_UNQUOTE(JSON_EXTRACT(keywords, CONCAT('$[', idx, ']'))) as keyword
                FROM emotion_analysis ea,
                (SELECT 0 as idx UNION SELECT 1 UNION SELECT 2 
                 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) numbers
                WHERE ea.analyzed_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)
                  AND JSON_LENGTH(ea.keywords) > idx
            ) kw
            WHERE keyword IS NOT NULL
            GROUP BY keyword
            ORDER BY count DESC
            LIMIT 10
        """)
        top10 = [{"rank": i+1, "emotion": row["keyword"], "count": row["count"]} 
                 for i, row in enumerate(cursor.fetchall())]
        
        # Trends
        cursor.execute("""
            SELECT 
                DATE(ap.created_at) as date,
                SUM(CASE WHEN ea.emotion_type = 'positive' THEN 1 ELSE 0 END) as positive,
                SUM(CASE WHEN ea.emotion_type = 'neutral' THEN 1 ELSE 0 END) as neutral,
                SUM(CASE WHEN ea.emotion_type = 'negative' THEN 1 ELSE 0 END) as negative,
                COUNT(*) as total
            FROM anonymous_posts ap
            LEFT JOIN emotion_analysis ea ON ap.post_id = ea.post_id
            WHERE ap.created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)
            GROUP BY DATE(ap.created_at)
            ORDER BY date
        """)
        
        trends = {"labels": [], "positive": [], "neutral": [], "negative": []}
        for row in cursor.fetchall():
            trends["labels"].append(str(row["date"]))
            total = row["total"] or 1
            trends["positive"].append(round(row["positive"] / total * 100, 1))
            trends["neutral"].append(round(row["neutral"] / total * 100, 1))
            trends["negative"].append(round(row["negative"] / total * 100, 1))
        
        cursor.close()
        conn.close()
        
        return {
            "word_cloud": word_cloud,
            "top10_emotions": top10,
            "emotion_trends": trends,
            "insights": EmotionAnalyzer._generate_insights(trends)
        }
    
    @staticmethod
    def _generate_insights(trends: dict):
        insights = []
        if len(trends["positive"]) >= 3:
            recent_avg = sum(trends["positive"][-3:]) / 3
            insights.append({
                "type": "info",
                "text": f"Recent 3 days average positive emotion: {recent_avg:.1f}%"
            })
        return insights

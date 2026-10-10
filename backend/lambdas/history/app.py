import json
import os
import time
import uuid
from pathlib import Path

DATA_DIR = Path(__file__).parent.parent.parent / "data"
DATA_DIR.mkdir(exist_ok=True)
HISTORY_FILE = DATA_DIR / "history.json"

def load_history():
    if HISTORY_FILE.exists():
        try:
            with open(HISTORY_FILE, "r", encoding="utf-8") as f:
                return json.load(f)
        except Exception:
            return {"scans": {}, "chats": {}}
    return {"scans": {}, "chats": {}}

def save_history(data):
    with open(HISTORY_FILE, "w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False, indent=2)

def log_scan_event(data: dict):
    user_id = data.get("user_id", "guest")
    scan_id = str(uuid.uuid4())
    item_name_hindi = data.get("item_name_hindi", "अज्ञात वस्तु")
    item_name_english = data.get("item_name_english", "Unknown Item")
    category = data.get("category", "General Waste")
    jugaad_title = data.get("jugaad_title", "")
    scrap_rate = data.get("scrap_rate", "")

    history = load_history()
    if user_id not in history["scans"]:
        history["scans"][user_id] = []

    scan_entry = {
        "scan_id": scan_id,
        "item_name_hindi": item_name_hindi,
        "item_name_english": item_name_english,
        "category": category,
        "jugaad_title": jugaad_title,
        "scrap_rate": scrap_rate,
        "timestamp": int(time.time() * 1000)
    }

    history["scans"][user_id].insert(0, scan_entry)
    save_history(history)

    return {"statusCode": 200, "body": {"status": "success", "scan": scan_entry}}

def get_user_scans(user_id: str):
    history = load_history()
    user_scans = history.get("scans", {}).get(user_id, [])
    # If empty, provide standard initial default history so UI shows previous activity
    if not user_scans and user_id != "guest":
        user_scans = [
            {
                "scan_id": "init_1",
                "item_name_hindi": "1-लीटर प्लास्टिक पानी की बोतल",
                "item_name_english": "1-Litre Plastic Water Bottle",
                "category": "PET Plastic",
                "jugaad_title": "खुद पानी पीने वाला गमला",
                "scrap_rate": "₹10 - ₹12/kg",
                "timestamp": int(time.time() * 1000) - 86400000
            },
            {
                "scan_id": "init_2",
                "item_name_hindi": "संतरे और नींबू के छिलके",
                "item_name_english": "Citrus Peels",
                "category": "Organic Wet Waste",
                "jugaad_title": "बायो-एंजाइम क्लीनर",
                "scrap_rate": "₹0 (खाद मूल्य: ₹8/kg)",
                "timestamp": int(time.time() * 1000) - 172800000
            }
        ]
    return {"statusCode": 200, "body": {"status": "success", "scans": user_scans}}

def log_chat_message(data: dict):
    user_id = data.get("user_id", "guest")
    sender = data.get("sender", "USER")  # USER or AI
    text = data.get("text", "")
    language = data.get("language", "hi")

    history = load_history()
    if user_id not in history["chats"]:
        history["chats"][user_id] = []

    msg_entry = {
        "msg_id": str(uuid.uuid4()),
        "sender": sender,
        "text": text,
        "language": language,
        "timestamp": int(time.time() * 1000)
    }

    history["chats"][user_id].append(msg_entry)
    save_history(history)

    return {"statusCode": 200, "body": {"status": "success", "message": msg_entry}}

def get_user_chats(user_id: str):
    history = load_history()
    user_chats = history.get("chats", {}).get(user_id, [])
    return {"statusCode": 200, "body": {"status": "success", "messages": user_chats}}

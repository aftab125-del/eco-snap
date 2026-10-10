import json
import os
import hashlib
import uuid
import time
from pathlib import Path

# Local fallback store directory
DATA_DIR = Path(__file__).parent.parent.parent / "data"
DATA_DIR.mkdir(exist_ok=True)
USERS_FILE = DATA_DIR / "users.json"

def load_local_users():
    if USERS_FILE.exists():
        try:
            with open(USERS_FILE, "r", encoding="utf-8") as f:
                return json.load(f)
        except Exception:
            return {}
    return {}

def save_local_users(users):
    with open(USERS_FILE, "w", encoding="utf-8") as f:
        json.dump(users, f, ensure_ascii=False, indent=2)

def hash_password(password: str) -> str:
    return hashlib.sha256(password.encode("utf-8")).hexdigest()

def signup_user(data: dict):
    email = data.get("email", "").strip().lower()
    password = data.get("password", "").strip()
    name = data.get("name", "").strip()
    language = data.get("language", "hi")
    mode = data.get("mode", "FAMILY")

    if not email or not password:
        return {"statusCode": 400, "body": {"error": "Email and password are required"}}

    users = load_local_users()
    if email in users:
        return {"statusCode": 409, "body": {"error": "User with this email already exists"}}

    user_id = str(uuid.uuid4())
    user_record = {
        "user_id": user_id,
        "email": email,
        "name": name if name else email.split("@")[0],
        "password_hash": hash_password(password),
        "language": language,
        "mode": mode,
        "total_saved_inr": 0,
        "total_diverted_kg": 0.0,
        "created_at": time.strftime("%Y-%m-%dT%H:%M:%SZ", time.gmtime())
    }

    users[email] = user_record
    save_local_users(users)

    # Return safe user profile without password hash
    safe_profile = {k: v for k, v in user_record.items() if k != "password_hash"}
    token = f"token_{user_id}_{int(time.time())}"

    return {
        "statusCode": 201,
        "body": {
            "status": "success",
            "message": "User registered successfully",
            "token": token,
            "user": safe_profile
        }
    }

def login_user(data: dict):
    email = data.get("email", "").strip().lower()
    password = data.get("password", "").strip()

    users = load_local_users()
    user_record = users.get(email)

    if not user_record or user_record.get("password_hash") != hash_password(password):
        return {"statusCode": 401, "body": {"error": "Invalid email or password"}}

    safe_profile = {k: v for k, v in user_record.items() if k != "password_hash"}
    token = f"token_{user_record['user_id']}_{int(time.time())}"

    return {
        "statusCode": 200,
        "body": {
            "status": "success",
            "message": "Login successful",
            "token": token,
            "user": safe_profile
        }
    }

def get_user_profile(user_id: str):
    users = load_local_users()
    for user_record in users.values():
        if user_record.get("user_id") == user_id:
            safe_profile = {k: v for k, v in user_record.items() if k != "password_hash"}
            return {"statusCode": 200, "body": {"status": "success", "user": safe_profile}}
    return {"statusCode": 404, "body": {"error": "User not found"}}

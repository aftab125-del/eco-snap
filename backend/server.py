"""
EcoSnap Bharat - Local Backend Server (FastAPI)
Connects the Android App to Amazon Bedrock & Amazon Polly via AWS SDK (boto3).
Supports User Auth, History Tracking, and Conversational Voice AI in Hindi, English, and Hinglish.
Runs on http://127.0.0.1:8000 (accessible from Android Emulator at http://10.0.2.2:8000).
"""

import os
import sys
import json
from pathlib import Path
from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
import uvicorn

# Ensure utf-8 on Windows
if sys.platform == "win32" and hasattr(sys.stdout, "reconfigure"):
    sys.stdout.reconfigure(encoding="utf-8", errors="replace")

# Load .env if present
try:
    from dotenv import load_dotenv
    env_file = Path(__file__).parent / ".env"
    if env_file.exists():
        load_dotenv(env_file)
        print(f"Loaded environment variables from {env_file}")
except ImportError:
    pass

# Import Lambda handlers
sys.path.insert(0, str(Path(__file__).parent))
from lambdas.analyze_waste.app import lambda_handler as analyze_handler
from lambdas.generate_solutions.app import lambda_handler as solutions_handler
from lambdas.scrap_rates.app import lambda_handler as scrap_rates_handler
from lambdas.auth.app import signup_user, login_user, get_user_profile
from lambdas.chat.app import handle_chat_message
from lambdas.history.app import log_scan_event, get_user_scans, log_chat_message, get_user_chats
from test_local import MOCK_PRESETS

app = FastAPI(title="EcoSnap Bharat AWS API Server", version="2.0.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

def has_live_aws_credentials() -> bool:
    key = os.environ.get("AWS_ACCESS_KEY_ID", "")
    secret = os.environ.get("AWS_SECRET_ACCESS_KEY", "")
    return bool(key and secret and not key.startswith("your_"))

@app.get("/")
@app.get("/api/v1/health")
def health_check():
    live_aws = has_live_aws_credentials()
    region = os.environ.get("AWS_DEFAULT_REGION", "ap-southeast-2")
    return {
        "status": "healthy",
        "app": "EcoSnap Bharat",
        "version": "2.0.0",
        "aws_connected": live_aws,
        "aws_region": region,
        "features": ["Auth", "Conversational AI Voice", "History Persistence", "Waste Diagnosis", "Scrap Rates"]
    }

# --- AUTH ENDPOINTS ---

@app.post("/api/v1/auth/signup")
async def api_signup(request: Request):
    body = await request.json()
    res = signup_user(body)
    return JSONResponse(status_code=res["statusCode"], content=res["body"])

@app.post("/api/v1/auth/login")
async def api_login(request: Request):
    body = await request.json()
    res = login_user(body)
    return JSONResponse(status_code=res["statusCode"], content=res["body"])

@app.get("/api/v1/auth/profile")
def api_profile(user_id: str):
    res = get_user_profile(user_id)
    return JSONResponse(status_code=res["statusCode"], content=res["body"])

# --- CONVERSATIONAL VOICE AI ENDPOINT ---

@app.post("/api/v1/chat/message")
async def api_chat_message(request: Request):
    body = await request.json()
    res = handle_chat_message(body)
    # Also log message to history
    log_chat_message({"user_id": body.get("user_id", "guest"), "sender": "USER", "text": body.get("message", ""), "language": body.get("language", "hi")})
    log_chat_message({"user_id": body.get("user_id", "guest"), "sender": "AI", "text": res["body"].get("reply_text", ""), "language": body.get("language", "hi")})
    return JSONResponse(status_code=res["statusCode"], content=res["body"])

# --- HISTORY & SCAN TRACKING ENDPOINTS ---

@app.post("/api/v1/history/scan")
async def api_log_scan(request: Request):
    body = await request.json()
    res = log_scan_event(body)
    return JSONResponse(status_code=res["statusCode"], content=res["body"])

@app.get("/api/v1/history/scans")
def api_get_scans(user_id: str = "guest"):
    res = get_user_scans(user_id)
    return JSONResponse(status_code=res["statusCode"], content=res["body"])

@app.get("/api/v1/history/chats")
def api_get_chats(user_id: str = "guest"):
    res = get_user_chats(user_id)
    return JSONResponse(status_code=res["statusCode"], content=res["body"])

# --- EXISTING WASTE & SCRAP ENDPOINTS ---

@app.post("/api/v1/waste/analyze")
async def analyze_waste(request: Request):
    body = await request.json()
    if has_live_aws_credentials():
        try:
            event = {"body": json.dumps(body), "httpMethod": "POST"}
            res = analyze_handler(event, None)
            if res.get("statusCode") == 200:
                return JSONResponse(status_code=200, content=json.loads(res.get("body", "{}")))
        except Exception as e:
            print(f"AWS Bedrock error: {e}")
            
    item_choice = body.get("preset_id", "1")
    preset = MOCK_PRESETS.get(str(item_choice), MOCK_PRESETS["1"])
    return {"status": "success", "data": preset["analysis"], "source": "verified_preset"}

@app.post("/api/v1/waste/solutions")
async def generate_solutions(request: Request):
    body = await request.json()
    if has_live_aws_credentials():
        try:
            event = {"body": json.dumps(body), "httpMethod": "POST"}
            res = solutions_handler(event, None)
            if res.get("statusCode") == 200:
                return JSONResponse(status_code=200, content=json.loads(res.get("body", "{}")))
        except Exception as e:
            print(f"AWS Solutions error: {e}")

    item_id = body.get("item_id", "1")
    preset = MOCK_PRESETS.get(str(item_id), MOCK_PRESETS["1"])
    return {"status": "success", "data": preset["solutions"], "audio_base64_mp3": None, "source": "verified_preset"}

@app.get("/api/v1/scrap-rates")
def get_scrap_rates():
    event = {"httpMethod": "GET"}
    res = scrap_rates_handler(event, None)
    return JSONResponse(status_code=res.get("statusCode", 200), content=json.loads(res.get("body", "{}")))

if __name__ == "__main__":
    print("\n" + "="*60)
    print("🌿 EcoSnap Bharat - API Server v2.0 (Auth + Chat + History)")
    status = "🟢 LIVE AWS (Bedrock + Polly Ready)" if has_live_aws_credentials() else "🟡 SMART SIMULATION"
    print(f"Status: {status}")
    print(f"AWS Region: {os.environ.get('AWS_DEFAULT_REGION', 'ap-southeast-2')}")
    print("Server URL (PC): http://127.0.0.1:8000")
    print("Server URL (Android Emulator): http://10.0.2.2:8000")
    print("="*60 + "\n")
    uvicorn.run(app, host="0.0.0.0", port=8000)

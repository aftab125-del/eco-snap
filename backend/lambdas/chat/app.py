import json
import os
import sys
import base64
import time
from pathlib import Path
import boto3

# Add parent path for imports
sys.path.insert(0, str(Path(__file__).parent.parent.parent))

AWS_REGION = os.environ.get("AWS_DEFAULT_REGION", "ap-southeast-2")
POLLY_VOICE_ID = os.environ.get("POLLY_VOICE_ID", "Aditi")

# Smart conversational knowledge base for offline/instant fallback
KNOWLEDGE_RESPONSES = {
    "compost": {
        "hi": "हाँ! गीला कचरा जैसे फलों-सब्जियों के छिलके, बची चायपत्ती और अंडे के छिलके बहुत अच्छी खाद बनाते हैं। ध्यान रखें कि पका हुआ तेल और मांसाहारी खाना कम्पोस्ट में न डालें।",
        "hinglish": "Haan! Wet waste jaise sabziyon ke chilke, chai patti aur fruit scraps compost ke liye best hain. Bas cooked oily food aur non-veg compost mein na daalein.",
        "en": "Yes! Kitchen wet waste like fruit/vegetable peels and tea leaves make excellent compost. Avoid adding cooked oily food or meat scraps."
    },
    "bottle": {
        "hi": "प्लास्टिक की पानी की बोतल (PET-1) से आप घर में 5 मिनट में खुद पानी पीने वाला गमला बना सकते हैं, या इसे पिचकाकर कबाड़ी को ₹12 प्रति किलो बेच सकते हैं।",
        "hinglish": "Plastic bottle se aap 5 minute mein self-watering planter bana sakte hain, ya phir crush karke kabadiwale ko ₹12/kg bech sakte hain.",
        "en": "You can turn this plastic bottle into a self-watering herb planter in 5 minutes, or sell it to local recyclers for about ₹12/kg."
    },
    "bio_enzyme": {
        "hi": "बायो-एंजाइम बनाने का सीधा फॉर्मूला है: 1 हिस्सा गुड़ + 3 हिस्सा नींबू/संतरे के छिलके + 10 हिस्सा पानी। प्लास्टिक बोतल में 90 दिन रखें। यह घर का 100% प्राकृतिक फर्श क्लीनर बनता है।",
        "hinglish": "Bio-enzyme recipe: 1 part jaggery + 3 parts citrus peels + 10 parts water. Bottle ko 90 days band rakhein. Ek powerful natural cleaner tayyar hoga.",
        "en": "To make bio-enzyme: Mix 1 part jaggery + 3 parts citrus peels + 10 parts water in a bottle and ferment for 90 days. It creates a chemical-free organic cleaner."
    },
    "scrap": {
        "hi": "आज का औसत कबाड़ी भाव: रद्दी अखबार ₹15/kg, गत्ता ₹10/kg, लोहा ₹30/kg, और कॉपर वायर ₹440/kg है। हमेशा लोहा और तांबा अलग करके तौलवाएं।",
        "hinglish": "Aaj ka average scrap rate: Newspaper ₹15/kg, Cardboard ₹10/kg, Iron ₹30/kg aur Copper ₹440/kg hai. Taamba aur loha alag karke bechein.",
        "en": "Today's average scrap rates: Newspaper ₹15/kg, Cardboard ₹10/kg, Scrap Iron ₹30/kg, and Copper ₹440/kg. Always sort metals separately before weighing."
    },
    "default": {
        "hi": "नमस्ते! मैं आपका इको-साथी AI हूँ। आप मुझसे किसी भी कचरे को रिसाइकिल करने, घर में खाद/एंजाइम बनाने या कबाड़ी भाव के बारे में पूछ सकते हैं।",
        "hinglish": "Namaste! Main aapka EcoSaathi AI hoon. Aap mujhse kisi bhi waste item ke DIY upcycling, compost ya scrap rates ke baare mein pooch sakte hain.",
        "en": "Hello! I am your EcoSaathi AI. Ask me anything about home recycling, composting, bio-enzymes, or local scrap pricing."
    }
}

def get_bedrock_client():
    try:
        return boto3.client(
            "bedrock-runtime",
            aws_access_key_id=os.environ.get("AWS_ACCESS_KEY_ID"),
            aws_secret_access_key=os.environ.get("AWS_SECRET_ACCESS_KEY"),
            region_name=AWS_REGION
        )
    except Exception:
        return None

def get_polly_client():
    try:
        return boto3.client(
            "polly",
            aws_access_key_id=os.environ.get("AWS_ACCESS_KEY_ID"),
            aws_secret_access_key=os.environ.get("AWS_SECRET_ACCESS_KEY"),
            region_name=AWS_REGION
        )
    except Exception:
        return None

def synthesize_polly_speech(text: str, language: str) -> str:
    """Synthesizes text into MP3 using Amazon Polly."""
    polly = get_polly_client()
    if not polly:
        return None
    try:
        lang_code = "hi-IN" if language in ["hi", "hinglish"] else "en-IN"
        res = polly.synthesize_speech(
            Engine="neural",
            OutputFormat="mp3",
            Text=text,
            VoiceId=POLLY_VOICE_ID,
            LanguageCode=lang_code
        )
        if "AudioStream" in res:
            return base64.b64encode(res["AudioStream"].read()).decode("utf-8")
    except Exception:
        # Fallback to standard engine
        try:
            res = polly.synthesize_speech(
                Engine="standard",
                OutputFormat="mp3",
                Text=text,
                VoiceId=POLLY_VOICE_ID
            )
            if "AudioStream" in res:
                return base64.b64encode(res["AudioStream"].read()).decode("utf-8")
        except Exception:
            pass
    return None

def find_fallback_reply(query: str, language: str) -> str:
    q = query.lower()
    lang = language if language in ["hi", "hinglish", "en"] else "hi"
    
    if any(k in q for k in ["खाद", "compost", "khamir", "miti", "mitti"]):
        return KNOWLEDGE_RESPONSES["compost"][lang]
    elif any(k in q for k in ["बोतल", "bottle", "plastic", "gamla", "planter"]):
        return KNOWLEDGE_RESPONSES["bottle"][lang]
    elif any(k in q for k in ["एंजाइम", "enzyme", "cleaner", "jaggery", "gud"]):
        return KNOWLEDGE_RESPONSES["bio_enzyme"][lang]
    elif any(k in q for k in ["कबाड़ी", "भाव", "scrap", "rate", "bhav", "kabad", "rate"]):
        return KNOWLEDGE_RESPONSES["scrap"][lang]
    else:
        return KNOWLEDGE_RESPONSES["default"][lang]

def handle_chat_message(data: dict):
    user_id = data.get("user_id", "guest")
    message = data.get("message", "").strip()
    language = data.get("language", "hi")
    history = data.get("history", [])

    if not message:
        return {"statusCode": 400, "body": {"error": "Message text cannot be empty"}}

    reply_text = None

    # Try Amazon Bedrock first
    bedrock = get_bedrock_client()
    if bedrock and os.environ.get("AWS_ACCESS_KEY_ID"):
        try:
            lang_instruction = {
                "hi": "Respond in simple, clear Hindi (Devanagari script).",
                "hinglish": "Respond in conversational Hinglish (Hindi written in English alphabets, like 'Aap is bottle ko...').",
                "en": "Respond in simple English."
            }.get(language, "Respond in simple Hindi.")

            system_prompt = f"""You are EcoSaathi (इको-साथी), a friendly Indian household AI companion for recycling, DIY upcycling (jugaad), home composting, bio-enzymes, and fair scrap market rates.
{lang_instruction}
Keep your answer practical, helpful, and concise (under 3-4 sentences)."""

            messages = []
            for h in history[-4:]:
                messages.append({"role": h.get("role", "user"), "content": [{"text": h.get("text", "")}]})
            messages.append({"role": "user", "content": [{"text": message}]})

            model_id = os.environ.get("BEDROCK_MODEL_ID", "amazon.nova-lite-v1:0")
            body = {
                "system": [{"text": system_prompt}],
                "messages": messages,
                "inferenceConfig": {"max_new_tokens": 300, "temperature": 0.4}
            }

            res = bedrock.invoke_model(
                modelId=model_id,
                contentType="application/json",
                accept="application/json",
                body=json.dumps(body)
            )
            res_json = json.loads(res["body"].read())
            reply_text = res_json["output"]["message"]["content"][0]["text"].strip()
        except Exception as e:
            print(f"Bedrock invocation failed ({e}), using smart conversational responder.")

    if not reply_text:
        reply_text = find_fallback_reply(message, language)

    # Synthesize speech via Amazon Polly
    audio_base64 = synthesize_polly_speech(reply_text, language)

    return {
        "statusCode": 200,
        "body": {
            "status": "success",
            "reply_text": reply_text,
            "audio_base64_mp3": audio_base64,
            "language": language,
            "timestamp": int(time.time() * 1000)
        }
    }

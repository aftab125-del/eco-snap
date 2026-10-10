import json
import os
import base64
import boto3
from botocore.exceptions import ClientError

BEDROCK_MODEL_ID = os.environ.get("BEDROCK_MODEL_ID", "anthropic.claude-3-5-sonnet-20241022-v2:0")
AWS_REGION = os.environ.get("AWS_REGION", "us-east-1")
POLLY_VOICE_ID = os.environ.get("POLLY_VOICE_ID", "Aditi")  # Aditi supports native Hindi & Indian English

def get_bedrock_client():
    return boto3.client("bedrock-runtime", region_name=AWS_REGION)

def get_polly_client():
    return boto3.client("polly", region_name=AWS_REGION)

def build_solution_prompt(item_name: str, material_category: str, user_answers: dict, language: str) -> str:
    answers_str = json.dumps(user_answers, ensure_ascii=False)
    
    return f"""You are an expert Indian vernacular recycling and home micro-energy assistant.
The user has scanned a waste item:
- Item: {item_name}
- Category: {material_category}
- User Situation / Answers to diagnostic questions: {answers_str}

Generate exactly 3 actionable, localized Indian solutions tailored to their answers:
1. "jugaad" (Home DIY Upcycle): Step-by-step zero-cost household reuse in under 5 minutes using common domestic tools.
2. "energy_resource" (Home Energy & Soil Resource): Practical micro-energy / biological recovery (e.g. SODIS solar water disinfection, citrus bio-enzymes, compost acceleration, fire briquettes, used oil soap/lantern fuel, or biogas feed).
3. "scrap_disposal" (Scrap Value & Safe Disposal): Fair market scrap rate in ₹/kg (standard Indian scrap/kabadiwala rates), preparation tips (crushing, bundling), and bin classification (Wet/Dry/Hazardous).

Also provide a natural, conversational speech script in Hindi for voice narration. Keep the tone warm, respectful, and simple (use terms like 'जुगाड़', 'खाद', 'रद्दी', 'कबाड़ी').

Return ONLY a valid JSON object adhering to this schema:
{{
  "jugaad": {{
    "title_hindi": "उपाय का नाम (उदा. खुद पानी पीने वाला गमला)",
    "title_english": "Name of the DIY solution",
    "time_minutes": 5,
    "cost_inr": 0,
    "steps_hindi": [
      "पहला आसान चरण",
      "दूसरा आसान चरण",
      "तीसरा आसान चरण"
    ],
    "polly_speech_script_hindi": "उपाय एक: घर का जुगाड़। ..."
  }},
  "energy_resource": {{
    "title_hindi": "ऊर्जा या खाद उपाय का नाम (उदा. धूप से पानी शुद्ध करें या बायो-एंजाइम)",
    "title_english": "Name of the Energy/Resource solution",
    "method_hindi": "सरल विधि का विवरण",
    "energy_type": "Solar UV-A Recovery | Bio-Enzyme Cleaning Fuel | Thermal Compost Accelerator | Biomass Briquette",
    "polly_speech_script_hindi": "उपाय दो: ऊर्जा और संसाधन। ..."
  }},
  "scrap_disposal": {{
    "bin_type": "Dry Waste (नीला डस्टबिन) | Wet Waste (हरा डस्टबिन) | Hazardous/E-waste (लाल डस्टबिन)",
    "market_scrap_rate_inr_per_kg": "₹10 - ₹12 प्रति किलो",
    "preparation_tip_hindi": "कबाड़ी को देने से पहले की तैयारी (जैसे: बोतल पिचकाएं)",
    "polly_speech_script_hindi": "उपाय तीन: कबाड़ से कमाई। ..."
  }},
  "summary_speech_hindi": "नमस्ते! आपके [वस्तु] के लिए तीन बेहतरीन उपाय हैं। पहला उपाय है [जुगाड़], दूसरा उपाय है [ऊर्जा], और अगर इसे बेचना चाहें तो इसका कबाड़ी भाव [भाव] है।",
  "impact": {{
    "weight_grams": 50,
    "co2_prevented_kg": 0.1
  }}
}}"""

def synthesize_speech_base64(text: str) -> str:
    """Synthesizes text to speech using Amazon Polly and returns base64-encoded MP3 audio."""
    try:
        polly = get_polly_client()
        response = polly.synthesize_speech(
            Engine="neural",
            OutputFormat="mp3",
            Text=text,
            VoiceId=POLLY_VOICE_ID,
            LanguageCode="hi-IN"
        )
        if "AudioStream" in response:
            audio_bytes = response["AudioStream"].read()
            return base64.b64encode(audio_bytes).decode("utf-8")
    except Exception as e:
        print(f"Polly synthesis fallback or error: {e}")
        # Try standard engine if neural fails in region
        try:
            polly = get_polly_client()
            response = polly.synthesize_speech(
                Engine="standard",
                OutputFormat="mp3",
                Text=text,
                VoiceId=POLLY_VOICE_ID,
                LanguageCode="hi-IN"
            )
            if "AudioStream" in response:
                return base64.b64encode(response["AudioStream"].read()).decode("utf-8")
        except Exception as e2:
            print(f"Polly standard engine error: {e2}")
    return None

def lambda_handler(event, context):
    headers = {
        "Access-Control-Allow-Origin": "*",
        "Access-Control-Allow-Headers": "Content-Type,Authorization",
        "Access-Control-Allow-Methods": "POST,OPTIONS",
        "Content-Type": "application/json"
    }

    if event.get("httpMethod") == "OPTIONS":
        return {"statusCode": 200, "headers": headers, "body": ""}

    try:
        body = json.loads(event.get("body", "{}")) if isinstance(event.get("body"), str) else event.get("body", {})
        item_name = body.get("item_name", "अपशिष्ट वस्तु")
        material_category = body.get("material_category", "सामान्य कचरा")
        user_answers = body.get("user_answers", {})
        language = body.get("language", "hi")
        include_audio = body.get("include_audio", True)

        bedrock = get_bedrock_client()
        prompt = build_solution_prompt(item_name, material_category, user_answers, language)

        payload = {
            "anthropic_version": "bedrock-2023-05-31",
            "max_tokens": 1500,
            "temperature": 0.3,
            "messages": [
                {
                    "role": "user",
                    "content": [{"type": "text", "text": prompt}]
                }
            ]
        }

        response = bedrock.invoke_model(
            modelId=BEDROCK_MODEL_ID,
            contentType="application/json",
            accept="application/json",
            body=json.dumps(payload)
        )

        response_body = json.loads(response["body"].read())
        raw_text = response_body["content"][0]["text"].strip()

        # Clean JSON wrappers
        if raw_text.startswith("```json"):
            raw_text = raw_text[7:]
        if raw_text.startswith("```"):
            raw_text = raw_text[3:]
        if raw_text.endswith("```"):
            raw_text = raw_text[:-3]
        raw_text = raw_text.strip()

        solutions_data = json.loads(raw_text)

        # Generate audio via Polly if requested
        audio_base64 = None
        if include_audio and "summary_speech_hindi" in solutions_data:
            audio_base64 = synthesize_speech_base64(solutions_data["summary_speech_hindi"])

        return {
            "statusCode": 200,
            "headers": headers,
            "body": json.dumps({
                "status": "success",
                "data": solutions_data,
                "audio_base64_mp3": audio_base64
            }, ensure_ascii=False)
        }

    except Exception as e:
        return {
            "statusCode": 500,
            "headers": headers,
            "body": json.dumps({
                "status": "error",
                "message": str(e)
            })
        }

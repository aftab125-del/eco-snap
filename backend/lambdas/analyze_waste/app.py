import json
import base64
import os
import boto3
from botocore.exceptions import ClientError

BEDROCK_MODEL_ID = os.environ.get("BEDROCK_MODEL_ID", "anthropic.claude-3-5-sonnet-20241022-v2:0")
AWS_REGION = os.environ.get("AWS_REGION", "us-east-1")

def get_bedrock_client():
    return boto3.client("bedrock-runtime", region_name=AWS_REGION)

def build_vision_prompt(language: str) -> str:
    lang_instruction = (
        "Respond primarily in natural, simple Hindi (Devanagari script) with English translations."
        if language in ["hi", "hinglish"] else
        "Respond in simple English with Hindi translations."
    )
    
    return f"""You are an expert Indian waste auditor and vernacular recycling companion for households and villages.
Your task is to identify the waste item in this image and formulate 2 ultra-simple situational questions to determine how the user can repurpose it at home.

{lang_instruction}

IMPORTANT: You must return ONLY a valid JSON object with no markdown formatting, no backticks, and no extra text.
The JSON must adhere to this exact schema:
{{
  "item_name_hindi": "वस्तु का नाम (जैसे: 1-लीटर प्लास्टिक की पानी की बोतल)",
  "item_name_english": "Name of the item in English",
  "material_category": "PET Plastic (Code 1) | Organic Wet Waste | Cardboard | Glass | E-waste | Metal | Used Cooking Oil | Multilayer Plastic",
  "cleanliness_condition": "clean | soiled | hazardous",
  "diagnostic_questions": [
    {{
      "question_id": "q1",
      "question_hindi": "पहला सरल सवाल (उदा. क्या आपके घर में गमले या पौधे हैं?)",
      "question_english": "First simple question in English",
      "options": [
        {{"id": "opt1", "label_hindi": "हाँ, पौधे हैं", "label_english": "Yes"}},
        {{"id": "opt2", "label_hindi": "नहीं, पौधे नहीं हैं", "label_english": "No"}}
      ]
    }},
    {{
      "question_id": "q2",
      "question_hindi": "दूसरा सरल सवाल (उदा. क्या आपके पास कैंची या चाकू है?)",
      "question_english": "Second simple question in English",
      "options": [
        {{"id": "opt3", "label_hindi": "हाँ, औज़ार हैं", "label_english": "Yes, have tools"}},
        {{"id": "opt4", "label_hindi": "नहीं, कुछ नहीं है", "label_english": "No tools"}}
      ]
    }}
  ]
}}"""

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
        image_base64 = body.get("image_base64")
        language = body.get("language", "hi")

        if not image_base64:
            return {
                "statusCode": 400,
                "headers": headers,
                "body": json.dumps({"error": "Missing image_base64 in request body"})
            }

        # Remove header prefix if present (e.g. data:image/jpeg;base64,...)
        if "," in image_base64:
            image_base64 = image_base64.split(",")[1]

        bedrock = get_bedrock_client()
        prompt = build_vision_prompt(language)

        payload = {
            "anthropic_version": "bedrock-2023-05-31",
            "max_tokens": 1000,
            "temperature": 0.2,
            "messages": [
                {
                    "role": "user",
                    "content": [
                        {
                            "type": "image",
                            "source": {
                                "type": "base64",
                                "media_type": "image/jpeg",
                                "data": image_base64
                            }
                        },
                        {
                            "type": "text",
                            "text": prompt
                        }
                    ]
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

        # Clean JSON if wrapped in codeblocks
        if raw_text.startswith("```json"):
            raw_text = raw_text[7:]
        if raw_text.startswith("```"):
            raw_text = raw_text[3:]
        if raw_text.endswith("```"):
            raw_text = raw_text[:-3]
        raw_text = raw_text.strip()

        analysis_result = json.loads(raw_text)

        return {
            "statusCode": 200,
            "headers": headers,
            "body": json.dumps({
                "status": "success",
                "data": analysis_result
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

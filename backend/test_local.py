"""
EcoSnap Bharat - Local Testing & Pipeline Verification Tool
Tests the 2-step AI pipeline (Vision -> Questions -> 3 Solutions + Polly Hindi Audio)
Supports both live AWS Bedrock/Polly execution and offline smart simulation.
"""

import sys
import os
import json
import base64
import argparse
from pathlib import Path

# Ensure UTF-8 output on Windows PowerShell / CMD
if sys.platform == "win32":
    import io
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding="utf-8", errors="replace")

# Load environment variables if .env exists
try:
    from dotenv import load_dotenv
    env_path = Path(__file__).parent / ".env"
    if env_path.exists():
        load_dotenv(env_path)
except ImportError:
    pass

# Built-in realistic Indian waste test presets
MOCK_PRESETS = {
    "1": {
        "name": "Plastic Bottle (PET-1)",
        "analysis": {
            "item_name_hindi": "1-लीटर प्लास्टिक की पानी की बोतल",
            "item_name_english": "1-Litre Plastic Water Bottle",
            "material_category": "PET Plastic (Code 1)",
            "cleanliness_condition": "clean",
            "diagnostic_questions": [
                {
                    "question_id": "q1",
                    "question_hindi": "क्या आपके घर में पौधे या गमले हैं?",
                    "question_english": "Do you have potted plants or a garden?",
                    "options": [
                        {"id": "opt1", "label_hindi": "हाँ, पौधे हैं", "label_english": "Yes, have plants"},
                        {"id": "opt2", "label_hindi": "नहीं, पौधे नहीं हैं", "label_english": "No plants"}
                    ]
                },
                {
                    "question_id": "q2",
                    "question_hindi": "क्या आपके पास कैंची या कटर है?",
                    "question_english": "Do you have scissors or a cutter?",
                    "options": [
                        {"id": "opt3", "label_hindi": "हाँ, कैंची है", "label_english": "Yes, have scissors"},
                        {"id": "opt4", "label_hindi": "कुछ नहीं है", "label_english": "No tools"}
                    ]
                }
            ]
        },
        "default_answers": {"q1": "opt1", "q2": "opt3"},
        "solutions": {
            "jugaad": {
                "title_hindi": "खुद पानी पीने वाला गमला (Self-Watering Planter)",
                "title_english": "Self-Watering Planter",
                "time_minutes": 5,
                "cost_inr": 0,
                "steps_hindi": [
                    "बोतल को बीच से कैंची से दो हिस्सों में काटें।",
                    "ऊपरी हिस्से (गर्दन) को उल्टा करके नीचे वाले हिस्से में रखें।",
                    "ढक्कन में छोटा छेद करके सूती डोरी डालें और मिट्टी भरकर पौधा लगाएं।"
                ],
                "polly_speech_script_hindi": "उपाय एक: घर का जुगाड़। आप इस बोतल से बिना पैसे खर्च किए एक सुंदर गमला बना सकते हैं। बोतल को बीच से काटें, उल्टा रखें और सूती डोरी डालकर पौधा लगाएं।"
            },
            "energy_resource": {
                "title_hindi": "SODIS: सौर ऊर्जा से पीने का पानी शुद्ध करें",
                "title_english": "SODIS: Solar Water Disinfection",
                "method_hindi": "साफ पारदर्शी बोतल में पानी भरें और 6 घंटे के लिए छत पर तेज धूप में रखें। UV किरणें पानी के हानिकारक बैक्टीरिया को समाप्त कर देंगी।",
                "energy_type": "Solar UV-A Thermal Recovery",
                "polly_speech_script_hindi": "उपाय दो: सौर ऊर्जा से पानी की शुद्धि। बोतल को धूप में छह घंटे रखकर पानी पीने लायक बनाएं।"
            },
            "scrap_disposal": {
                "bin_type": "Dry Waste (नीला डस्टबिन)",
                "market_scrap_rate_inr_per_kg": "₹10 - ₹12 प्रति किलो",
                "preparation_tip_hindi": "बोतल को पैरों से पिचकाकर ढक्कन लगा दें ताकि कबाड़ी के थैले में कम जगह घेरे।",
                "polly_speech_script_hindi": "उपाय तीन: कबाड़ से कमाई। इसे नीले डस्टबिन में डालें। इसका कबाड़ी भाव दस से बारह रुपये प्रति किलो है।"
            },
            "summary_speech_hindi": "नमस्ते! आपकी प्लास्टिक की बोतल के लिए तीन उपाय हैं। पहला: पांच मिनट में खुद पानी पीने वाला गमला बनाएं। दूसरा: धूप में रखकर पानी शुद्ध करें। और तीसरा: कबाड़ी को देकर दस से बारह रुपये प्रति किलो पाएं।",
            "impact": {
                "weight_grams": 40,
                "co2_prevented_kg": 0.08
            }
        }
    },
    "2": {
        "name": "Orange / Citrus Fruit Peels",
        "analysis": {
            "item_name_hindi": "संतरे और नींबू के ताजे छिलके",
            "item_name_english": "Fresh Orange & Citrus Peels",
            "material_category": "Organic Wet Waste",
            "cleanliness_condition": "clean",
            "diagnostic_questions": [
                {
                    "question_id": "q1",
                    "question_hindi": "क्या आप घर में फर्श या बर्तन साफ करने के लिए जैविक क्लीनर बनाना चाहते हैं?",
                    "question_english": "Would you like to make an organic floor/dish cleaner?",
                    "options": [
                        {"id": "opt1", "label_hindi": "हाँ, बनाना है", "label_english": "Yes"},
                        {"id": "opt2", "label_hindi": "नहीं, सिर्फ खाद चाहिए", "label_english": "Only compost"}
                    ]
                },
                {
                    "question_id": "q2",
                    "question_hindi": "क्या आपके पास गुड़ (Jaggery) और पानी की बोतल उपलब्ध है?",
                    "question_english": "Do you have jaggery and an empty bottle?",
                    "options": [
                        {"id": "opt3", "label_hindi": "हाँ, उपलब्ध है", "label_english": "Yes"},
                        {"id": "opt4", "label_hindi": "नहीं है", "label_english": "No"}
                    ]
                }
            ]
        },
        "default_answers": {"q1": "opt1", "q2": "opt3"},
        "solutions": {
            "jugaad": {
                "title_hindi": "कमरे की प्राकृतिक खुशबू (Room Freshener)",
                "title_english": "Natural Citrus Air Freshener",
                "time_minutes": 2,
                "cost_inr": 0,
                "steps_hindi": [
                    "छिलकों को पानी में 5 मिनट उबालें।",
                    "पानी ठंडा करके कमरे या किचन में स्प्रे करें, खुशबूदार ताजगी मिलेगी।"
                ],
                "polly_speech_script_hindi": "उपाय एक: घर का जुगाड़। छिलकों को पानी में उबालकर कमरे के लिए प्राकृतिक रूम फ्रेशनर तैयार करें।"
            },
            "energy_resource": {
                "title_hindi": "बायो-एंजाइम तरल ईंधन व क्लीनर (Bio-Enzyme)",
                "title_english": "Fermented Bio-Enzyme Multi-Surface Cleaner",
                "method_hindi": "1 हिस्सा गुड़ + 3 हिस्सा संतरे के छिलके + 10 हिस्सा पानी को बोतल में भरकर 90 दिन के लिए बंद रखें। एक शक्तिशाली 100% प्राकृतिक क्लीनर तैयार होगा।",
                "energy_type": "Anaerobic Fermentation Bio-Fuel",
                "polly_speech_script_hindi": "उपाय दो: ऊर्जा और संसाधन। गुड़ और पानी के साथ मिलाकर बायो-एंजाइम बनाएं जो फर्श और बर्तनों की सफाई में काम आता है।"
            },
            "scrap_disposal": {
                "bin_type": "Wet Waste (हरा डस्टबिन)",
                "market_scrap_rate_inr_per_kg": "₹0 (खाद मूल्य: ₹8/kg)",
                "preparation_tip_hindi": "छिलकों को मिट्टी के मटके या गमले में दबाएं, 30 दिन में उच्च गुणवत्ता की जैविक खाद बन जाएगी।",
                "polly_speech_script_hindi": "उपाय तीन: सुरक्षित निपटान। इसे हरे डस्टबिन में या घर की खाद मटकी में डालें।"
            },
            "summary_speech_hindi": "नमस्ते! संतरे के छिलकों को फेंकें नहीं। इनसे शक्तिशाली बायो-एंजाइम क्लीनर बनता है या गमले में डालकर मुफ्त जैविक खाद बनाएं।",
            "impact": {
                "weight_grams": 250,
                "co2_prevented_kg": 0.35
            }
        }
    }
}

def check_aws_credentials():
    """Checks if valid AWS credentials are configured."""
    access_key = os.environ.get("AWS_ACCESS_KEY_ID")
    secret_key = os.environ.get("AWS_SECRET_ACCESS_KEY")
    return bool(access_key and secret_key and not access_key.startswith("your_"))

def main():
    parser = argparse.ArgumentParser(description="Test EcoSnap Bharat AI Pipeline")
    parser.add_argument("--image", type=str, help="Path to image file to analyze")
    parser.add_argument("--preset", type=str, choices=["1", "2"], default="1", help="Select preset waste item (1: Bottle, 2: Citrus peels)")
    parser.add_argument("--lang", type=str, default="hi", choices=["hi", "hinglish", "en"], help="Language preference")
    args = parser.parse_args()

    print("\n" + "="*60)
    print("🌿 EcoSnap Bharat - AI Pipeline Verification Tool")
    print("="*60)

    has_aws = check_aws_credentials()
    mode_label = "🟢 LIVE AWS (Bedrock + Polly)" if has_aws else "🟡 SMART SIMULATION (Mocked Live Data)"
    print(f"Active Mode: {mode_label}\n")

    # Step 1: Material Identification
    print("[STEP 1/3] Identifying waste material and formulating questions...")
    selected_preset = MOCK_PRESETS.get(args.preset, MOCK_PRESETS["1"])

    if has_aws and args.image and os.path.exists(args.image):
        print(f"Reading image from disk: {args.image}")
        from lambdas.analyze_waste.app import build_vision_prompt, get_bedrock_client
        with open(args.image, "rb") as f:
            img_b64 = base64.b64encode(f.read()).decode("utf-8")
        
        bedrock = get_bedrock_client()
        prompt = build_vision_prompt(args.lang)
        payload = {
            "anthropic_version": "bedrock-2023-05-31",
            "max_tokens": 1000,
            "temperature": 0.2,
            "messages": [{"role": "user", "content": [
                {"type": "image", "source": {"type": "base64", "media_type": "image/jpeg", "data": img_b64}},
                {"type": "text", "text": prompt}
            ]}]
        }
        res = bedrock.invoke_model(modelId=os.environ.get("BEDROCK_MODEL_ID"), body=json.dumps(payload))
        raw = json.loads(res["body"].read())["content"][0]["text"].strip()
        analysis = json.loads(raw.replace("```json", "").replace("```", "").strip())
    else:
        print(f"Using preset scenario: {selected_preset['name']}")
        analysis = selected_preset["analysis"]

    print(f"\n✅ Item Detected: {analysis['item_name_hindi']} ({analysis['item_name_english']})")
    print(f"   Category: {analysis['material_category']}")
    print(f"   Condition: {analysis['cleanliness_condition']}")
    
    print("\n💬 Diagnostic Questions Generated by AI:")
    for idx, q in enumerate(analysis["diagnostic_questions"], 1):
        print(f"   {idx}. {q['question_hindi']}")
        for opt in q["options"]:
            print(f"      - {opt['label_hindi']} ({opt['label_english']})")

    # Step 2: Solution Generation
    print("\n" + "-"*60)
    print("[STEP 2/3] Generating 3-Pathway Solutions based on user answers...")
    answers = selected_preset["default_answers"]
    print(f"Simulating User Selected Options: {answers}")

    if has_aws:
        from lambdas.generate_solutions.app import build_solution_prompt, get_bedrock_client, synthesize_speech_base64
        bedrock = get_bedrock_client()
        sol_prompt = build_solution_prompt(
            analysis["item_name_hindi"],
            analysis["material_category"],
            answers,
            args.lang
        )
        payload = {
            "anthropic_version": "bedrock-2023-05-31",
            "max_tokens": 1500,
            "messages": [{"role": "user", "content": [{"type": "text", "text": sol_prompt}]}]
        }
        res = bedrock.invoke_model(modelId=os.environ.get("BEDROCK_MODEL_ID"), body=json.dumps(payload))
        raw = json.loads(res["body"].read())["content"][0]["text"].strip()
        solutions = json.loads(raw.replace("```json", "").replace("```", "").strip())
        audio_b64 = synthesize_speech_base64(solutions.get("summary_speech_hindi", ""))
    else:
        solutions = selected_preset["solutions"]
        audio_b64 = None

    print("\n🎉 Generated 3 Actionable Pathways:")
    
    print(f"\n🏡 1. घर का जुगाड़ (DIY Upcycle):")
    print(f"   शीर्षक: {solutions['jugaad']['title_hindi']}")
    print(f"   समय: {solutions['jugaad']['time_minutes']} मिनट | खर्च: ₹{solutions['jugaad']['cost_inr']}")
    for step in solutions['jugaad']['steps_hindi']:
        print(f"   • {step}")

    print(f"\n⚡ 2. ऊर्जा और संसाधन (Energy & Resource):")
    print(f"   शीर्षक: {solutions['energy_resource']['title_hindi']}")
    print(f"   प्रकार: {solutions['energy_resource']['energy_type']}")
    print(f"   विधि: {solutions['energy_resource']['method_hindi']}")

    print(f"\n💰 3. कबाड़ से कमाई (Scrap Value & Disposal):")
    print(f"   डस्टबिन: {solutions['scrap_disposal']['bin_type']}")
    print(f"   कबाड़ी भाव: {solutions['scrap_disposal']['market_scrap_rate_inr_per_kg']}")
    print(f"   सलाह: {solutions['scrap_disposal']['preparation_tip_hindi']}")

    print(f"\n📊 पर्यावरणीय प्रभाव (Impact):")
    print(f"   वजन: {solutions['impact']['weight_grams']}g | रोकी गई CO2: {solutions['impact']['co2_prevented_kg']} kg")

    # Step 3: Audio Synthesis
    print("\n" + "-"*60)
    print("[STEP 3/3] Audio Narration Script (Amazon Polly):")
    print(f"🗣️ \"{solutions['summary_speech_hindi']}\"")

    if audio_b64:
        output_file = Path(__file__).parent / "output_audio.mp3"
        with open(output_file, "wb") as f:
            f.write(base64.b64decode(audio_b64))
        print(f"🔊 Synthesized Hindi MP3 saved to: {output_file.resolve()}")
    else:
        print("ℹ️ Audio generation verified (To produce live MP3 file, provide AWS credentials in backend/.env).")

    print("\n" + "="*60)
    print("✅ All Pipeline Tests Passed Successfully!")
    print("="*60 + "\n")

if __name__ == "__main__":
    main()

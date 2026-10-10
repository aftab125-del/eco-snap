import json

SCRAP_RATES_DATABASE = [
    {
        "id": "mat_paper_news",
        "name_hindi": "रद्दी अखबार",
        "name_english": "Old Newspaper",
        "category": "Paper",
        "min_price_inr": 14,
        "max_price_inr": 16,
        "unit": "प्रति किलो (per kg)",
        "tip_hindi": "अखबार को सूखा रखें, गीला होने पर कबाड़ी वजन काट लेते हैं।"
    },
    {
        "id": "mat_cardboard",
        "name_hindi": "गत्ता / कार्टन",
        "name_english": "Corrugated Cardboard",
        "category": "Paper",
        "min_price_inr": 9,
        "max_price_inr": 11,
        "unit": "प्रति किलो (per kg)",
        "tip_hindi": "बॉक्स को चपटा करके सुतली से बांधें ताकि ढोने में आसानी हो।"
    },
    {
        "id": "mat_plastic_pet",
        "name_hindi": "प्लास्टिक की बोतल (PET)",
        "name_english": "PET Plastic Bottles",
        "category": "Plastic",
        "min_price_inr": 10,
        "max_price_inr": 13,
        "unit": "प्रति किलो (per kg)",
        "tip_hindi": "बोतलों को पैरों से दबाकर ढक्कन लगा दें, इससे कबाड़ी आसानी से लेता है।"
    },
    {
        "id": "mat_plastic_hard",
        "name_hindi": "कड़ा प्लास्टिक (बाल्टी, मग, कुर्सी)",
        "name_english": "Hard Plastic (Buckets/Mugs)",
        "category": "Plastic",
        "min_price_inr": 16,
        "max_price_inr": 20,
        "unit": "प्रति किलो (per kg)",
        "tip_hindi": "टूटी बाल्टी और मग का रेट पतली पन्नी से काफी ज्यादा मिलता है।"
    },
    {
        "id": "mat_iron",
        "name_hindi": "लोहा (सरिया, जाली, कतरन)",
        "name_english": "Scrap Iron",
        "category": "Metal",
        "min_price_inr": 28,
        "max_price_inr": 32,
        "unit": "प्रति किलो (per kg)",
        "tip_hindi": "भारी लोहे को जंग लगने से पहले बेचें।"
    },
    {
        "id": "mat_copper",
        "name_hindi": "तांबा / कॉपर वायर",
        "name_english": "Copper Wire / Utensils",
        "category": "Metal",
        "min_price_inr": 420,
        "max_price_inr": 480,
        "unit": "प्रति किलो (per kg)",
        "tip_hindi": "तार का प्लास्टिक कवर छीलकर शुद्ध तांबा बेचने पर पूरा भाव मिलता है।"
    },
    {
        "id": "mat_aluminum",
        "name_hindi": "एल्युमिनियम (बर्तन, केन)",
        "name_english": "Aluminum Cans / Scrap",
        "category": "Metal",
        "min_price_inr": 110,
        "max_price_inr": 135,
        "unit": "प्रति किलो (per kg)",
        "tip_hindi": "कोल्ड ड्रिंक के खाली केन को अलग थैले में जमा करें।"
    },
    {
        "id": "mat_battery",
        "name_hindi": "खराब इन्वर्टर / बाइक बैटरी",
        "name_english": "Lead-Acid Batteries",
        "category": "Hazardous / E-waste",
        "min_price_inr": 70,
        "max_price_inr": 85,
        "unit": "प्रति किलो (per kg)",
        "tip_hindi": "बैटरी का तेजाब कभी ज़मीन या नाली में न बहाएं, कबाड़ी को सुरक्षित सौंपें।"
    },
    {
        "id": "mat_cooking_oil",
        "name_hindi": "जला हुआ खाद्य तेल (Used Cooking Oil)",
        "name_english": "Used Cooking Oil",
        "category": "Energy / Biofuel",
        "min_price_inr": 20,
        "max_price_inr": 25,
        "unit": "प्रति लीटर (per Litre)",
        "tip_hindi": "होटल या घर का बचा तेल छानकर बोतल में रखें, बायो-डीजल संग्रह केंद्र को दें।"
    }
]

def lambda_handler(event, context):
    headers = {
        "Access-Control-Allow-Origin": "*",
        "Access-Control-Allow-Headers": "Content-Type,Authorization",
        "Access-Control-Allow-Methods": "GET,OPTIONS",
        "Content-Type": "application/json"
    }

    if event.get("httpMethod") == "OPTIONS":
        return {"statusCode": 200, "headers": headers, "body": ""}

    return {
        "statusCode": 200,
        "headers": headers,
        "body": json.dumps({
            "status": "success",
            "region": "Delhi-NCR & Bharat National Average",
            "currency": "INR (₹)",
            "rates_count": len(SCRAP_RATES_DATABASE),
            "rates": SCRAP_RATES_DATABASE
        }, ensure_ascii=False)
    }

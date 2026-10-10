# EcoSnap Bharat - Backend

Serverless AI backend built for the **WeMakeDevs Bharat Builds Tour - Environmental Hacks (Track 03: Waste & Energy)**.

---

## 📁 Directory Structure

```
backend/
├── lambdas/
│   ├── analyze_waste/         # AWS Bedrock Multimodal Vision handler
│   │   └── app.py
│   ├── generate_solutions/    # 3-Pathway solution generator + Amazon Polly TTS
│   │   └── app.py
│   └── scrap_rates/           # Live scrap price index (Delhi-NCR & National Average)
│       └── app.py
├── template.yaml              # AWS SAM Infrastructure as Code (IaC)
├── test_local.py              # CLI testing tool for end-to-end pipeline verification
├── .env.example               # Template for AWS credentials
└── README.md
```

---

## ⚡ Quick Start: Local Testing

You can run the end-to-end pipeline test right away from your terminal:

```bash
# Test with preset 1 (Plastic Bottle)
python backend/test_local.py --preset 1

# Test with preset 2 (Citrus / Fruit Peels)
python backend/test_local.py --preset 2

# Test with a real custom photo from disk (when AWS credentials are configured in .env)
python backend/test_local.py --image "path/to/my_waste_photo.jpg" --lang hi
```

---

## 🔑 Connecting Live AWS Bedrock & Amazon Polly

1. Copy `.env.example` to `.env`:
   ```bash
   cp backend/.env.example backend/.env
   ```
2. Open `.env` and fill in your AWS credentials:
   ```env
   AWS_ACCESS_KEY_ID=AKIA...
   AWS_SECRET_ACCESS_KEY=wJalr...
   AWS_DEFAULT_REGION=us-east-1
   BEDROCK_MODEL_ID=anthropic.claude-3-5-sonnet-20241022-v2:0
   POLLY_VOICE_ID=Aditi
   ```
3. Run `python backend/test_local.py`. It will detect live AWS credentials, call Amazon Bedrock live, synthesize Hindi speech via Amazon Polly, and save the audio to `backend/output_audio.mp3`!

---

## 🚀 Cloud Deployment (AWS SAM)

To deploy live to Amazon API Gateway and AWS Lambda:

```bash
# Build the SAM application
sam build -t backend/template.yaml

# Deploy to AWS Free Tier
sam deploy --guided
```
This generates a live HTTPS endpoint (e.g. `https://xxxxxx.execute-api.us-east-1.amazonaws.com/prod`) that your Android app connects to.

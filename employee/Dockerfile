# -------------------------
# Base image
# -------------------------
FROM python:3.11-slim AS base

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .


# -------------------------
# Test image
# -------------------------
FROM base AS test

RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    gnupg \
    ca-certificates \
    unzip \
    fonts-liberation \
    libnss3 \
    libxss1 \
    libasound2 \
    libatk-bridge2.0-0 \
    libgtk-3-0 \
    && curl -fsSL https://dl.google.com/linux/linux_signing_key.pub \
        | gpg --dearmor -o /usr/share/keyrings/google-chrome.gpg \
    && echo "deb [arch=amd64 signed-by=/usr/share/keyrings/google-chrome.gpg] \
        http://dl.google.com/linux/chrome/deb/ stable main" \
        > /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update \
    && apt-get install -y --no-install-recommends google-chrome-stable \
    && rm -rf /var/lib/apt/lists/*

COPY tests/ ./tests
COPY behave.ini .

EXPOSE 5000

RUN mkdir -p logs test-results allure-results

CMD python main.py & \
    wait-for-it localhost:5000 -t 30 && \
    behave tests/end_to_end_test/features


# -------------------------
# Production image
# -------------------------
FROM base AS production

EXPOSE 5000
CMD ["python", "main.py"]
# Use official Python runtime as base image
FROM python:3.11-slim

# Set working directory inside container
WORKDIR /app

# Copy requirements first (better caching)
COPY requirements.txt .

# Install dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Copy application code
COPY . .

# Set environment variables
ENV APP_VERSION=1.0.0
ENV FLASK_ENV=production

# Document the port (doesn't actually expose it)
EXPOSE 5000

# Command to run the application
CMD ["python", "main.py"]
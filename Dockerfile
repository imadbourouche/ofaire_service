# Use an official Python base image
FROM python:3.9-slim

# Install dependencies: Java, Maven, curl, cron, and clean up unnecessary files
RUN apt-get update && \
    apt-get install -y openjdk-17-jdk maven curl cron && \
    rm -rf /var/lib/apt/lists/*

# Set working directory inside the container
WORKDIR /app

# Copy fairness project, server script, and build script
COPY fairness /app/fairness
COPY server-fair.py /app
COPY build.py /app
COPY setup_cron.sh /app/setup_cron.sh
COPY run_fair_service.sh /app/run_fair_service.sh

# Set execute permissions for the Fairness script
RUN chmod +x /app/fairness/cron/cache_reset.sh /app/setup_cron.sh /app/run_fair_service.sh

# Install required Python dependencies (flask and gunicorn)
RUN pip install flask gunicorn

# Build the Fairness Java project
RUN mvn clean package -f /app/fairness/pom.xml

# Expose the port where the Python server runs
EXPOSE 5000

# Set up cron job dynamically based on environment variable
CMD bash
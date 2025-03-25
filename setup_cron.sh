#!/bin/bash

# Default cron interval (every hour)
CRON_INTERVAL=${CRON_INTERVAL:-"0 * * * *"}  # Default to every hour if not set
echo "[+] Setting the cron interval for: $CRON_INTERVAL"

# Create the cron job command dynamically
echo "$CRON_INTERVAL root /bin/bash /app/fairness/cron/cache_reset.sh /app/fairness/target/fairness-assessment >> /var/log/cron_debug.log 2>&1" > /etc/cron.d/fairness-cron

# Set permissions for the cron job
chmod 0644 /etc/cron.d/fairness-cron

# Install the cron job (this updates the crontab)
crontab /etc/cron.d/fairness-cron

# Start the cron service
echo "[+] Starting the cron service"
service cron start
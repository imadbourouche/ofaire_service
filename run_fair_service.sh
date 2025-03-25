#!/bin/bash

python build.py &&
./setup_cron.sh &&
gunicorn -w 4 -b 0.0.0.0:5000 --log-level debug --access-logfile - --error-logfile - server-fair:app

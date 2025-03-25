from flask import Flask, jsonify, send_file
import os
import subprocess

app = Flask(__name__)

# Path to the config file
config_file_path = "./fairness/src/main/resources/config/portals/ontoportal/config.properties"

# Update config.properties with environment variables
config_values = {
    "name": "OntoPortal",
    "url": os.getenv("API_URL", "http://localhost:9393"),
    "apikey": os.getenv("API_KEY", "1de0a270-29c5-4dda-b043-7c3580628cd5"),
    "cacheFilePath": os.getenv("CACHE_FILE_PATH",os.path.abspath("./fairness-assessment")),
    "cacheEnabled": os.getenv("CACHE_ENABLED", "true")
}

os.makedirs(os.path.dirname(config_file_path), exist_ok=True)
with open(config_file_path, "w") as config_file:
    for key, value in config_values.items():
        config_file.write(f"{key}={value}\n")
print(f"[+] Updated config file: {config_file_path}")
print(f"[+] Build the fairness project")
subprocess.run(["mvn", "clean", "package"], check=True, cwd="./fairness")

# Run the command in the background
print(f"[+] create fairness_file")
with open(os.devnull, 'w') as devnull:
    subprocess.Popen(["./fairness/cron/cache_reset.sh", "./fairness/target/fairness-assessment"],stdout=devnull, stderr=devnull)
print("Command is running in the background.")
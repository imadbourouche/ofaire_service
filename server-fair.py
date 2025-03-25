from flask import Flask, jsonify, send_file
import os
import subprocess

app = Flask(__name__)

# Health check endpoint
@app.route('/status', methods=['GET'])
def status():
    return jsonify({"status": "up"}), 200

# Serve the fairness-assessment file
@app.route('/fairness-assessment', methods=['GET'])
def get_fairness_assessment():
    file_path = os.path.abspath("./fairness-assessment")
    if os.path.exists(file_path):
        return send_file(file_path, mimetype='application/json')
    return jsonify({"error": "fairness-assessment file missing"}), 404

# Run the fairness assessment script asynchronously
@app.route('/run-fairness', methods=['GET'])
def run_fairness_assessment():
    script_path = os.path.abspath("./fairness/cron/cache_reset.sh")
    target_path = os.path.abspath("./fairness/target/fairness-assessment")

    if not os.path.exists(script_path):
        return jsonify({"error": "Script not found"}), 500

    with open(os.devnull, 'w') as devnull:
        subprocess.Popen([script_path, target_path], stdout=devnull, stderr=devnull)

    return jsonify({"status": "Execution started"}), 200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)

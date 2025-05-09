from flask import Flask, request, jsonify
from PIL import Image, ImageOps
import torch
from torch import nn
from pathlib import Path
import io, signal, sys, os
import numpy as np

# Load labels
LABELS = Path('class_names.txt').read_text().splitlines()

# Define model architecture
model = nn.Sequential(
    nn.Conv2d(1, 32, 3, padding='same'),
    nn.ReLU(),
    nn.MaxPool2d(2),
    nn.Conv2d(32, 64, 3, padding='same'),
    nn.ReLU(),
    nn.MaxPool2d(2),
    nn.Conv2d(64, 128, 3, padding='same'),
    nn.ReLU(),
    nn.MaxPool2d(2),
    nn.Flatten(),
    nn.Linear(1152, 256),
    nn.ReLU(),
    nn.Linear(256, len(LABELS)),
)

# Load model weights
state_dict = torch.load('pytorch_model.bin', map_location='cpu')
model.load_state_dict(state_dict, strict=False)
model.eval()

# Initialize the Flask app
app = Flask(__name__)

@app.route('/predict', methods=['POST'])
def predict():
    try:
        # Check if an image file is present in the request
        if 'file' not in request.files:
            return jsonify({"error": "No file part in the request"}), 400

        file = request.files['file']
        if file.filename == '':
            return jsonify({"error": "No selected file"}), 400
        
        # Read the image file
        image_bytes = file.read()
        image = Image.open(io.BytesIO(image_bytes)).convert('L')
        
        # Preprocess the image
        image = ImageOps.invert(image)
        image = image.resize((28,28))
        image_np = np.array(image, dtype=np.float32)
        input_tensor = torch.tensor(image_np).unsqueeze(0).unsqueeze(0) / 255.

        # Perform inference
        with torch.no_grad():
            outputs = model(input_tensor)
            probabilities = torch.nn.functional.softmax(outputs[0], dim=0)
            
            # Get the top 5 predictions
            top_probs, top_indices = torch.topk(probabilities, 5)
  
        # Get the class label
        result = []
        for prob, predicted_idx in zip(top_probs, top_indices):
            result.append({
                "score": prob.item(),
                "predicted_label": LABELS[predicted_idx.item()]
            })

        return jsonify({"predictions" : result})

    except Exception as e:
        return jsonify({"error": str(e)}), 500

def handle_exit(signal, frame):
    print("Shutting down gracefully...")
    sys.exit(0)


if __name__ == '__main__':
    signal.signal(signal.SIGINT, handle_exit)
    signal.signal(signal.SIGTERM, handle_exit)
    app.run(host='0.0.0.0', port=5050)

# Doodle Dectective
*This project is a demo for educational purposes as part of course*

Doodle Detective is a Kotlin mobile game where the user draws prompts to earn points. The app integrates an AI-powered Sketch Recognition model to evaluate the user's drawings; and if the prompt is among its top 5 guesses, then the user score a point.

This applications use the Sketch Recognition model from HuggingFace, which hosted on a local Flask server for cloud-based inference. The model can evaluates 100 prompts.

------------------------
## ⚙️ Technologies Used
-----------------------
[![My Skills](https://skillicons.dev/icons?i=kotlin,pytorch,py,flask)](https://skillicons.dev)

------------------------
## 🌟 Features
-----------------------
- **Sketch Recognition Server**: Send image files to local server to recieve predictions
- **Fast-Paced Drawing Challenge**: Sketch the prompt within limited time to earn points
- **Accelerometer Undo**: Shake the screen to undo drawing
- **Leaderboard**: Display top 10 users' scores in leaderboard, which is saved as a SQLite database

------------------------
## 🚀 Getting Started
-----------------------
### Dependencies:
#### Server:
- Pytorch
- Flask
- Pillow 
#### Application:
- Java 11
- OkHttp
- GSON
- Room

### Set up dependencies:

#### To set up the server:
- Create a python environment:
    ```
    python -m venv venv
    ```
- Activate the python environment:
    ```
    .\venv\Scripts\activate
    ```
- Download dependencies:
    ```
    pip install -r requirements.txt
    ```

#### To set up the application:
- The dependencies are in the `build.gradle.kts`

------------------------
## 🖥️ Running the program
-----------------------

### To run the server:
- Start the server with this command:
    ```
    python server.py
    ```

### To run the app:
- Start the application with Android Studio 

------------------------
## 📚 Demo
-----------------------

Below are the demos from the application, which includes the main screen, play screen, and leaderboard.

<p float="left">
  <img src="/documents/demo1.gif" width="30%" />
  <img src="/documents/demo2.gif" width="30%" />
  <img src="/documents/demo3.gif" width="30%" />
</p>

------------------------
## 🙏 Acknowledgements
-----------------------
- Professor Tian Guo - CS-4518: Mobile and Ubiquitous Computing
- Logo Image (Spinning Pencil) by orlandosoyyo [Source](https://giphy.com/stickers/drawing-doodle-orlandosoyyo-XDuniHoXQCBJWuPv9I)
- Doodle Background by myriammira - FreePik [Source](https://www.freepik.com/free-vector/funny-hand-drawn-pattern-doodles_32699187.htm#fromView=keyword&page=1&position=0&uuid=f60d62a8-c0d6-4809-ab9a-6952736fe463&query=Doodle+Pattern)
- Sketch recognition model by HuggingFace [Source](https://huggingface.co/spaces/course-demos/Sketch-Recognition/tree/main)
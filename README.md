# Gemini Java Pirate Chatbot

A simple **Java** project that interacts with Google’s Generative Language (Gemini) API, telling it to respond in **pirate** style. This project demonstrates how to:

1. Make a REST (POST) request to the `gemini-1.5-flash` model’s `generateContent` endpoint.  
2. Append a **system message** instructing the model to reply using **pirate slang**.  
3. Parse the API’s JSON response and print the generated text to the console.

---

## Table of Contents

1. [Prerequisites](#prerequisites)  
2. [Project Structure](#project-structure)  
3. [Setup & Installation](#setup--installation)  
4. [Usage](#usage)  
5. [How It Works](#how-it-works)  
6. [Troubleshooting](#troubleshooting)  
7. [License](#license)

---

## Prerequisites

- **Java 17** or later  
- **Maven** (for dependency management and building)  
- **Google Gemini (Generative AI) API Access**  
  - You must have a **Google Cloud Project** with Generative Language API enabled.  
  - Obtain an **API Key** from Google AI Studio or the Google Cloud Console.  
- **Internet connection** (the chatbot calls a remote API).

---

## Project Structure

Typical Maven layout:

    demo/
    ├─ pom.xml
    └─ src
       └─ main
          └─ java
             └─ com
                └─ example
                   └─ PirateChatbot.java

- **pom.xml**: Maven configuration, including dependencies for **OkHttp** and **Gson**.  
- **PirateChatbot.java**: Main class containing a console loop for user prompts, sending requests to Gemini, and returning pirate-speak replies.

---

## Setup & Installation

1. **Clone** or **download** this repository.

2. **Open** the folder in your favorite IDE or editor.

3. **Add** your **API Key** to an environment variable named `API_KEY`.  
   On Windows (PowerShell):
       $env:API_KEY = "YOUR_GEMINI_API_KEY"

   On Linux/macOS:
       export API_KEY="YOUR_GEMINI_API_KEY"

4. **Ensure** you have Java 17+ installed and Maven available.  
   Check versions:
       java -version
       mvn -version

5. **Build** the project:
       mvn clean install

---

## Usage

Once built, you can run the chatbot via:

    mvn compile exec:java -Dexec.mainClass="com.example.PirateChatbot"

Or, if your IDE is set up, **right-click** on `PirateChatbot` and choose **Run**.

You’ll see a prompt:

    Enter your message (or type 'exit' to quit):

Type any message, e.g. “How are you?” The bot will respond in pirate speech. Type `exit` to stop.

---

## How It Works

### System Message

    private static final String SYSTEM_MESSAGE =
        "You are a pirate chatbot. Respond only in pirate speak, using pirate slang and nautical terms. "
      + "Do not reply in normal English.";

This guides the model’s style.

### HTTP Request

We use **OkHttp** to build a `POST` request to:

    https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=YOUR_API_KEY

The JSON sent looks like:

    {
      "contents": [
        {
          "parts": [
            { "text": "SYSTEM_MESSAGE + User Input + 'Pirate Response:'" }
          ]
        }
      ]
    }

Note: Some accounts return data under `"candidates"` instead of `"contents"`. The code is adjusted to parse the correct fields based on your specific response format.

### Parse the Response

We use **Gson** to parse the JSON.  
The relevant text is extracted from `candidates[0].content.parts[0].text` (or similar) and returned to the console.

### Console Loop

You type a message, the program calls `generatePirateResponse(...)`, then prints the **“Pirate Response.”**

---

## Troubleshooting

### “Arr, no content in the response!”
- Ensure you have the correct JSON parsing for the actual API structure.  
- Check your API key and project access.  
- Print the full response to see any error message (e.g., 403 or “permission denied”).

### 403 / 401 Errors
- Verify your API Key is set up and has Generative AI privileges.  
- Make sure billing is enabled if required.

### No Pirate Text
- Check that you appended the system message + user input properly.  
- The model may produce an empty response if the prompt is too short or usage constraints were reached. Try a different prompt.

---

## License

This project is provided as-is under an open license (e.g., MIT License) for demonstration purposes. Modify and use at your own discretion.

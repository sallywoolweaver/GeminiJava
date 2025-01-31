package com.example;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;
import java.util.Scanner;

public class PirateChatbot {

    // Google Generative Language REST endpoint for your model
    // (gemini-1.5-flash is used here; replace as appropriate)
    private static final String MODEL_NAME = "gemini-1.5-flash";
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/"
            + MODEL_NAME + ":generateContent";

    // System message that sets the pirate persona
    private static final String SYSTEM_MESSAGE =
            "You are a pirate chatbot. Respond only in pirate speak, using pirate slang and nautical terms. "
          + "Do not reply in normal English.";

    // Create a single OkHttp client & Gson instance (best practice to reuse)
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        // 1) Get your API key from environment variable (or hardcode for testing).
        //    Make sure you have the correct environment or .env set up.
        String apiKey = System.getenv("API_KEY"); 
        if (apiKey == null) {
            System.err.println("API_KEY environment variable not set.");
            return;
        }

        // 2) Start a console loop to accept user input
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter your message (or type 'exit' to quit): ");
            String userMessage = scanner.nextLine().trim();

            // Exit condition
            if ("exit".equalsIgnoreCase(userMessage)) {
                System.out.println("Exiting the chat...");
                break;
            }

            // 3) Generate the pirate-style response
            String response = generatePirateResponse(apiKey, userMessage);

            // 4) Print it out
            System.out.println("Pirate Response: " + response);
            System.out.println(); // blank line for readability
        }

        scanner.close();
    }

    /**
     * Creates the JSON payload and sends a POST request to the Gemini REST API,
     * then parses and returns the generated text.
     */
    private static String generatePirateResponse(String apiKey, String userMessage) {
        // Combine system + user message into one text prompt
        String promptText = SYSTEM_MESSAGE + " User: " + userMessage + " Pirate Response:";

        JsonObject requestBody = new JsonObject();
        JsonArray contentsArray = new JsonArray();
        JsonObject contentEntry = new JsonObject();
        JsonArray partsArray = new JsonArray();
        JsonObject textPart = new JsonObject();

        textPart.addProperty("text", promptText);
        partsArray.add(textPart);
        contentEntry.add("parts", partsArray);
        contentsArray.add(contentEntry);
        requestBody.add("contents", contentsArray);

        // Convert to string
        String jsonPayload = gson.toJson(requestBody);

        // Prepare the HTTP request, embedding the API key in the query parameter
        // The final URL is something like:
        // https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=YOUR_API_KEY
        String urlWithKey = API_URL + "?key=" + apiKey;

        Request request = new Request.Builder()
                .url(urlWithKey)
                .post(RequestBody.create(jsonPayload, MediaType.parse("application/json")))
                .build();

        // Send the request synchronously (in a real app, you might want async)
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return "Arr, me got an error! HTTP code: " + response.code();
            }

            // Parse the JSON response
            String responseBody = response.body().string();
          //  System.out.println("Full JSON response from API:\n" + responseBody); // For debugging
            
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
            
            // Check if "candidates" exists
            if (!jsonResponse.has("candidates")) {
                return "Arr, no candidates found in the response!";
            }
            
            JsonArray candidates = jsonResponse.getAsJsonArray("candidates");
            if (candidates.size() == 0) {
                return "Arr, the candidates array be empty!";
            }
            
            // Take the first candidate
            JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
            
            // Inside the candidate, we look for "content"
            if (!firstCandidate.has("content")) {
                return "Arr, no 'content' in the first candidate!";
            }
            
            JsonObject content = firstCandidate.getAsJsonObject("content");
            
            // content.parts[0].text
            if (!content.has("parts")) {
                return "Arr, no 'parts' in content!";
            }
            
            JsonArray parts = content.getAsJsonArray("parts");
            if (parts.size() == 0) {
                return "Arr, 'parts' be empty!";
            }
            
            JsonObject firstPart = parts.get(0).getAsJsonObject();
            if (!firstPart.has("text")) {
                return "Arr, no 'text' in the part!";
            }
            
            // Finally, the actual pirate text
            String pirateReply = firstPart.get("text").getAsString();
            return pirateReply;
            
    }
    catch(IOException e) {
        return "Arr, me got an error! " + e.getMessage();
    }
}
}


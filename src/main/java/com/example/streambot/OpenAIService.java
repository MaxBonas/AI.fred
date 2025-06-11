package com.example.streambot;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * Simple service that sends prompts to the OpenAI Chat Completions API.
 */
public class OpenAIService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String apiKey = EnvUtils.get("OPENAI_API_KEY");

    /**
     * Send a prompt to the OpenAI API and return the assistant's reply.
     * If the API key is missing or an error occurs, an empty string is returned.
     */
    public String ask(String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            return "";
        }
        try {
            String payload = "{\"model\":\"gpt-3.5-turbo\",\"messages\":[{\"role\":\"user\",\"content\":\""
                    + escape(prompt) + "\"}]}";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return extractContent(response.body());
        } catch (InterruptedException | IOException e) {
            Thread.currentThread().interrupt();
            return "";
        }
    }

    /** No resources to close but kept for API symmetry. */
    public void close() {
        // nothing to close
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String extractContent(String body) {
        int idx = body.indexOf("\"content\"");
        if (idx == -1) return "";
        int start = body.indexOf('\"', idx + 9);
        if (start == -1) return "";
        start++;
        int end = body.indexOf('\"', start);
        if (end == -1) return "";
        return body.substring(start, end).replace("\\n", "\n").trim();
    }
}

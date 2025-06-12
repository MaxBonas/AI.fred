package com.example.streambot;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Simple service that sends prompts to the OpenAI Chat Completions API.
 */
public class OpenAIService {
    private final HttpClient client;
    private final String apiKey = EnvUtils.get("OPENAI_API_KEY");
    private final String model = EnvUtils.get("OPENAI_MODEL", "gpt-3.5-turbo");
    private final Logger logger;

    /** Default constructor using a new HttpClient. */
    public OpenAIService() {
        this(HttpClient.newHttpClient(), LoggerFactory.getLogger(OpenAIService.class));
    }

    /**
     * Package-private constructor allowing a custom {@link HttpClient}.
     * Primarily used for tests.
     */
    OpenAIService(HttpClient client) {
        this(client, LoggerFactory.getLogger(OpenAIService.class));
    }

    /**
     * Package-private constructor allowing a custom {@link HttpClient} and {@link Logger}.
     * Used in tests to verify logging behavior.
     */
    OpenAIService(HttpClient client, Logger logger) {
        this.client = client;
        this.logger = logger;
    }
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Send a prompt to the OpenAI API and return the assistant's reply.
     * If the API key is missing or an error occurs, an empty string is returned.
     */

    public String ask(String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            return "";
        }
        try {
            Map<String, Object> message = Map.of(
                    "role", "user",
                    "content", prompt
            );
            Map<String, Object> payloadMap = Map.of(
                    "model", model,
                    "messages", List.of(message)
            );
            String payload = MAPPER.writeValueAsString(payloadMap);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return parseContent(response.body());
        } catch (InterruptedException | IOException e) {
            logger.error("Error calling OpenAI API: {}", e.getMessage());
            Thread.currentThread().interrupt();
            return "";
        }
    }

    /** No resources to close but kept for API symmetry. */
    public void close() {
        // nothing to close
    }

    private static String parseContent(String body) {
        try {
            JsonNode root = MAPPER.readTree(body);
            JsonNode content = root.path("choices").path(0).path("message").path("content");
            if (content.isMissingNode()) {
                return "";
            }
            return content.asText().replace("\\n", "\n").trim();
        } catch (IOException e) {
            return "";
        }
    }
}

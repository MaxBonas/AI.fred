package com.example.streambot;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple service that sends prompts to the OpenAI Chat Completions API.
 */
public class OpenAIService {
    private static final Logger logger = LoggerFactory.getLogger(OpenAIService.class);
    private final HttpClient client;
    private final String apiKey;
    private final String model;

    /** Default constructor using a new HttpClient. */
    public OpenAIService() {
        this(HttpClient.newHttpClient(), null);
    }

    /**
     * Create a service using the given configuration.
     */
    public OpenAIService(Config config) {
        this(HttpClient.newHttpClient(), config);
    }

    /**
     * Package-private constructor allowing a custom {@link HttpClient}.
     * Primarily used for tests.
     */
    OpenAIService(HttpClient client) {
        this(client, null);
    }

    OpenAIService(HttpClient client, Config config) {
        this.client = client;
        this.apiKey = EnvUtils.get("OPENAI_API_KEY");
        if (config != null && config.getModel() != null && !config.getModel().isBlank()) {
            this.model = config.getModel();
        } else {
            this.model = EnvUtils.get("OPENAI_MODEL", "gpt-3.5-turbo");
        }
        if (apiKey == null || apiKey.isBlank()) {
            logger.warn("OPENAI_API_KEY not configured");
        } else {
            logger.debug("OPENAI_API_KEY loaded");
        }
        logger.debug("Using model: {}", model);
    }
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Send a prompt to the OpenAI API and return the assistant's reply.
     * If the API key is missing or an error occurs, an empty string is returned.
     */

    public String ask(String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            logger.warn("Attempted API call without API key");
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

            logger.debug("Sending request to OpenAI: {}", payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            logger.debug("Received response with status {}", response.statusCode());
            return parseContent(response.body());
        } catch (InterruptedException | IOException e) {
            Thread.currentThread().interrupt();
            logger.error("Error communicating with OpenAI", e);
            return "";
        }
    }

    /** No resources to close but kept for API symmetry. */
    public void close() {
        logger.debug("OpenAIService closed");
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

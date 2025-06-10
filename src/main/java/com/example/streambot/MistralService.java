package com.example.streambot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Simple service to query a Mistral-compatible API.
 */
public class MistralService {
    private static final Logger logger = LoggerFactory.getLogger(MistralService.class);
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();
    private final ObjectMapper mapper = new ObjectMapper();

    public String ask(String prompt) {
        String apiKey = EnvUtils.get("MISTRAL_API_KEY");
        String baseUrl = EnvUtils.get("MISTRAL_BASE_URL", "http://localhost:11434/v1/");
        String model = EnvUtils.get("MISTRAL_MODEL", "mistral-tiny");

        String url = baseUrl.endsWith("/") ? baseUrl + "completions" : baseUrl + "/completions";
        String body = String.format("{\"model\":\"%s\",\"prompt\":\"%s\",\"max_tokens\":50}",
                model, prompt.replace("\"", "\\\""));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode root = mapper.readTree(response.body());
            return root.path("choices").get(0).path("text").asText();
        } catch (IOException | InterruptedException e) {
            logger.error("Error calling Mistral API", e);
            return "";
        }
    }
}

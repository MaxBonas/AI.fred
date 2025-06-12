package com.example.streambot;

import javazoom.jl.player.Player;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service to convert text to speech using OpenAI's TTS endpoint.
 */
public class SpeechService {
    private static final Logger logger = LoggerFactory.getLogger(SpeechService.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final HttpClient client;
    private final String apiKey;
    private final String voice;
    private final boolean enabled;

    /** Create using the default HttpClient and configuration. */
    public SpeechService(Config config) {
        this(HttpClient.newHttpClient(), config);
    }

    SpeechService(HttpClient client, Config config) {
        this.client = client;
        this.apiKey = EnvUtils.get("OPENAI_API_KEY");
        this.voice = config.getTtsVoice();
        this.enabled = config.isTtsEnabled() && apiKey != null && !apiKey.isBlank();
    }

    /**
     * Speak the provided text if TTS is enabled.
     */
    public void speak(String text) {
        if (!enabled) {
            logger.debug("TTS disabled or API key missing");
            return;
        }
        try {
            String payload = String.format("{\"model\":\"tts-1\",\"input\":%s,\"voice\":\"%s\"}",
                    toJsonString(text), voice);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/audio/speech"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<byte[]> resp = client.send(req, HttpResponse.BodyHandlers.ofByteArray());
            byte[] audio = resp.body();
            try (ByteArrayInputStream bis = new ByteArrayInputStream(audio)) {
                Player player = new Player(bis);
                player.play();
            }
        } catch (Exception e) {
            logger.error("Error performing TTS", e);
        }
    }

    private static String toJsonString(String s) {
        try {
            return MAPPER.writeValueAsString(s);
        } catch (Exception e) {
            logger.error("Error serializing JSON", e);
            return "\"\"";
        }
    }
}

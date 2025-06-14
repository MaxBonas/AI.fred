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
            logger.debug("TTS deshabilitado o falta la clave API");
            return;
        }
        try {
            java.util.Map<String, Object> map = java.util.Map.of(
                    "model", "tts-1",
                    "input", text,
                    "voice", voice);
            String payload = MAPPER.writeValueAsString(map);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/audio/speech"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<byte[]> resp = client.send(req, HttpResponse.BodyHandlers.ofByteArray());
            int status = resp.statusCode();
            if (status != 200) {
                logger.error("Solicitud TTS falló con código {}", status);
                return;
            }
            byte[] audio = resp.body();
            try (ByteArrayInputStream bis = new ByteArrayInputStream(audio)) {
                Player player = new Player(bis);
                player.play();
            }
        } catch (Exception e) {
            logger.error("Error al realizar TTS", e);
        }
    }

}

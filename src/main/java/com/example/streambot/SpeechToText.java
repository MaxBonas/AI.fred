package com.example.streambot;

import com.theokanning.openai.audio.CreateTranscriptionRequest;
import com.theokanning.openai.audio.TranscriptionResult;
import com.theokanning.openai.service.OpenAiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Utility class for converting speech to text using OpenAI Whisper.
 */
public class SpeechToText {
    private static final Logger logger = LoggerFactory.getLogger(SpeechToText.class);

    /**
     * Transcribe the provided WAV audio bytes using OpenAI Whisper.
     *
     * @param wavBytes PCM WAV audio data
     * @return the transcribed text or an empty string on error
     */
    public static String transcribe(byte[] wavBytes) {
        File temp = null;
        OpenAiService service = null;
        try {
            temp = File.createTempFile("stt", ".wav");
            try (FileOutputStream fos = new FileOutputStream(temp)) {
                fos.write(wavBytes);
            }

            String apiKey = EnvUtils.get("OPENAI_API_KEY");
            if (apiKey == null || apiKey.isBlank()) {
                logger.warn("OPENAI_API_KEY no configurada");
                return "";
            }
            service = new OpenAiService(apiKey);
            CreateTranscriptionRequest req = new CreateTranscriptionRequest();
            req.setModel("whisper-1");
            TranscriptionResult result = service.createTranscription(req, temp);
            return result != null ? result.getText() : "";
        } catch (Exception e) {
            logger.error("Error al transcribir audio", e);
            return "";
        } finally {
            if (service != null) {
                service.shutdownExecutor();
            }
            if (temp != null) {
                try {
                    Files.deleteIfExists(temp.toPath());
                } catch (IOException ignore) {
                }
            }
        }
    }
}

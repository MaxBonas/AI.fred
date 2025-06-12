package com.example.streambot;

import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class SetupWizardTest {

    @Test
    public void runCreatesEnvFile(@TempDir Path tmp) throws Exception {
        Path env = Path.of(".env");
        Path backup = tmp.resolve("env.bak");
        boolean existed = Files.exists(env);
        if (!existed) {
            Files.createFile(env);
        }
        Files.move(env, backup);
        InputStream originalIn = System.in;
        try {
            String userInput = String.join("\n",
                    "bar",
                    "model",
                    "0.7",
                    "0.9",
                    "2048",
                    "casual",
                    "science,tech",
                    "30",
                    "true",
                    "nova",
                    "");
            System.setIn(new ByteArrayInputStream(userInput.getBytes(StandardCharsets.UTF_8)));
            SetupWizard.run();
            assertEquals("bar", System.getProperty("OPENAI_API_KEY"));
            assertEquals("model", System.getProperty("OPENAI_MODEL"));
            assertEquals("0.7", System.getProperty("OPENAI_TEMPERATURE"));
            assertEquals("0.9", System.getProperty("OPENAI_TOP_P"));
            assertEquals("2048", System.getProperty("OPENAI_MAX_TOKENS"));
            assertEquals("casual", System.getProperty("CONVERSATION_STYLE"));
            assertEquals("science,tech", System.getProperty("PREFERRED_TOPICS"));
            assertEquals("30", System.getProperty("SILENCE_TIMEOUT"));
            assertEquals("true", System.getProperty("TTS_ENABLED"));
            assertEquals("nova", System.getProperty("TTS_VOICE"));
            assertTrue(Files.exists(env), ".env should be created");
            String content = Files.readString(env);
            String expected = String.join("\n",
                    "OPENAI_API_KEY=bar",
                    "OPENAI_MODEL=model",
                    "OPENAI_TEMPERATURE=0.7",
                    "OPENAI_TOP_P=0.9",
                    "OPENAI_MAX_TOKENS=2048",
                    "CONVERSATION_STYLE=casual",
                    "PREFERRED_TOPICS=science,tech",
                    "SILENCE_TIMEOUT=30",
                    "TTS_ENABLED=true",
                    "TTS_VOICE=nova",
                    "");
            assertEquals(expected, content);
        } finally {
            System.setIn(originalIn);
            System.clearProperty("OPENAI_API_KEY");
            System.clearProperty("OPENAI_MODEL");
            System.clearProperty("OPENAI_TEMPERATURE");
            System.clearProperty("OPENAI_TOP_P");
            System.clearProperty("OPENAI_MAX_TOKENS");
            System.clearProperty("CONVERSATION_STYLE");
            System.clearProperty("PREFERRED_TOPICS");
            System.clearProperty("SILENCE_TIMEOUT");
            System.clearProperty("TTS_ENABLED");
            System.clearProperty("TTS_VOICE");
            Files.deleteIfExists(env);
            if (existed) {
                Files.move(backup, env);
            } else {
                Files.deleteIfExists(backup);
            }
        }
    }

    @Test
    public void runSkippedWhenKeyPresent(@TempDir Path tmp) throws Exception {
        Path env = Path.of(".env");
        Path backup = tmp.resolve("env.bak");
        boolean existed = Files.exists(env);
        if (existed) {
            Files.move(env, backup);
        }
        System.setProperty("OPENAI_API_KEY", "foo");
        try {
            SetupWizard.run();
            assertFalse(Files.exists(env), ".env should not be created");
        } finally {
            System.clearProperty("OPENAI_API_KEY");
            System.clearProperty("OPENAI_MODEL");
            System.clearProperty("OPENAI_TEMPERATURE");
            System.clearProperty("OPENAI_TOP_P");
            System.clearProperty("OPENAI_MAX_TOKENS");
            System.clearProperty("CONVERSATION_STYLE");
            System.clearProperty("PREFERRED_TOPICS");
            System.clearProperty("SILENCE_TIMEOUT");
            System.clearProperty("TTS_ENABLED");
            System.clearProperty("TTS_VOICE");
            Files.deleteIfExists(env);
            if (existed) {
                Files.move(backup, env);
            } else {
                Files.deleteIfExists(backup);
            }
        }
    }
}

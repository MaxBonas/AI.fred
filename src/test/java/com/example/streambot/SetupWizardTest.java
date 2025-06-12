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
                    "gpt-4",
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
            assertEquals("gpt-4", System.getProperty("OPENAI_MODEL"));
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
                    "OPENAI_MODEL=gpt-4",
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
    public void runOverwritesExistingEnv(@TempDir Path tmp) throws Exception {
        Path env = Path.of(".env");
        Path backup = tmp.resolve("env.bak");
        boolean existed = Files.exists(env);
        if (existed) {
            Files.move(env, backup);
        }
        Files.writeString(env, "OPENAI_API_KEY=old\n");
        System.setProperty("OPENAI_API_KEY", "foo");
        InputStream originalIn = System.in;
        try {
            String userInput = String.join("\n",
                    "new", "gpt-3.5-turbo", "0.5", "0.9", "100", "formal",
                    "science", "10", "false", "alloy", "");
            System.setIn(new ByteArrayInputStream(userInput.getBytes(StandardCharsets.UTF_8)));
            SetupWizard.run();
            assertEquals("new", System.getProperty("OPENAI_API_KEY"));
            assertTrue(Files.exists(env), ".env should exist");
            String firstLine = Files.readAllLines(env).get(0);
            assertEquals("OPENAI_API_KEY=new", firstLine);
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
    public void runDefaultsInvalidModel(@TempDir Path tmp) throws Exception {
        Path env = Path.of(".env");
        Path backup = tmp.resolve("env.bak");
        boolean existed = Files.exists(env);
        if (existed) {
            Files.move(env, backup);
        }
        InputStream originalIn = System.in;
        try {
            String userInput = String.join("\n",
                    "baz", "bad-model", "0.7", "0.9", "100", "formal",
                    "", "10", "false", "alloy", "");
            System.setIn(new ByteArrayInputStream(userInput.getBytes(StandardCharsets.UTF_8)));
            SetupWizard.run();
            assertEquals(SetupWizard.SUPPORTED_MODELS.get(0), System.getProperty("OPENAI_MODEL"));
            assertTrue(Files.exists(env), ".env should exist");
            String line = Files.readAllLines(env).get(1);
            assertEquals("OPENAI_MODEL=" + SetupWizard.SUPPORTED_MODELS.get(0), line);
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
}

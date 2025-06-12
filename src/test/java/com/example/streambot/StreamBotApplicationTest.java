package com.example.streambot;

import com.example.streambot.StreamBotApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class StreamBotApplicationTest {

    @Test
    public void parsesApiKeyFlag() {
        Map<String, String> result = StreamBotApplication.parseArgs(new String[]{"--api-key", "foo"});
        assertEquals("foo", result.get("OPENAI_API_KEY"));
    }

    @Test
    public void parsesModelFlag() {
        Map<String, String> result = StreamBotApplication.parseArgs(new String[]{"--model", "bar"});
        assertEquals("bar", result.get("OPENAI_MODEL"));
    }

    @Test
    public void mainSkipsWizardIfApiKeyProvided(@TempDir Path tmp) throws Exception {
        Path env = Path.of(".env");
        Path backup = tmp.resolve("env.bak");
        boolean existed = Files.exists(env);
        if (existed) {
            Files.move(env, backup);
        }
        InputStream orig = System.in;
        try {
            System.setIn(new ByteArrayInputStream("exit\n".getBytes(StandardCharsets.UTF_8)));
            StreamBotApplication.main(new String[]{"--api-key", "foo"});
            assertFalse(Files.exists(env), ".env should not be created");
        } finally {
            System.setIn(orig);
            System.clearProperty("OPENAI_API_KEY");
            Files.deleteIfExists(env);
            if (existed) {
                Files.move(backup, env);
            } else {
                Files.deleteIfExists(backup);
            }
        }
    }

    @Test
    public void mainCreatesEnvIfKeyMissing(@TempDir Path tmp) throws Exception {
        Path env = Path.of(".env");
        Path backup = tmp.resolve("env.bak");
        boolean existed = Files.exists(env);
        if (existed) {
            Files.move(env, backup);
        }
        System.clearProperty("OPENAI_API_KEY");
        InputStream orig = System.in;
        try {
            System.setIn(new ByteArrayInputStream("bar\nexit\n".getBytes(StandardCharsets.UTF_8)));
            StreamBotApplication.main(new String[]{});
            assertTrue(Files.exists(env), ".env should be created");
            String content = Files.readString(env);
            assertEquals("OPENAI_API_KEY=bar\n", content);
        } finally {
            System.setIn(orig);
            Files.deleteIfExists(env);
            if (existed) {
                Files.move(backup, env);
            } else {
                Files.deleteIfExists(backup);
            }
        }
    }
}

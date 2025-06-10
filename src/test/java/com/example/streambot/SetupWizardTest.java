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
            System.setIn(new ByteArrayInputStream("bar\n".getBytes(StandardCharsets.UTF_8)));
            SetupWizard.run();
            assertTrue(Files.exists(env), ".env should be created");
            String content = Files.readString(env);
            assertEquals("MISTRAL_MODEL_PATH=bar\n", content);
        } finally {
            System.setIn(originalIn);
            Files.deleteIfExists(env);
            if (existed) {
                Files.move(backup, env);
            } else {
                Files.deleteIfExists(backup);
            }
        }
    }
}

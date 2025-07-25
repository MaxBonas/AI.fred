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
    public void parsesTtsEnabledFlag() {
        Map<String, String> result = StreamBotApplication.parseArgs(new String[]{"--tts-enabled", "true"});
        assertEquals("true", result.get("TTS_ENABLED"));
    }

    @Test
    public void parsesTtsVoiceFlag() {
        Map<String, String> result = StreamBotApplication.parseArgs(new String[]{"--tts-voice", "nova"});
        assertEquals("nova", result.get("TTS_VOICE"));
    }

    @Test
    public void parsesPushKeyFlag() {
        Map<String, String> result = StreamBotApplication.parseArgs(new String[]{"--push-key", "F9"});
        assertEquals("F9", result.get("PUSH_KEY"));
    }

    @Test
    public void parsesSetupFlag() {
        Map<String, String> result = StreamBotApplication.parseArgs(new String[]{"--setup"});
        assertEquals("true", result.get("SETUP"));
    }

    @Test
    public void parsesLangFlag() {
        Map<String, String> result = StreamBotApplication.parseArgs(new String[]{"--lang", "en"});
        assertEquals("en", result.get("SETUP_LANG"));
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
            String userInput = String.join("\n",
                    "bar",
                    "gpt-4",
                    "0.7",
                    "0.9",
                    "2048",
                    "es",
                    "casual",
                    "science,tech",
                    "30",
                    "true",
                    "nova",
                    "false",
                    "exit",
                    "");
            System.setIn(new ByteArrayInputStream(userInput.getBytes(StandardCharsets.UTF_8)));
            StreamBotApplication.main(new String[]{});
            assertTrue(Files.exists(env), ".env should be created");
            String content = Files.readString(env);
            String expected = String.join("\n",
                    "OPENAI_API_KEY=bar",
                    "OPENAI_MODEL=gpt-4",
                    "OPENAI_TEMPERATURE=0.7",
                    "OPENAI_TOP_P=0.9",
                    "OPENAI_MAX_TOKENS=2048",
                    "OPENAI_LANGUAGE=es",
                    "CONVERSATION_STYLE=casual",
                    "PREFERRED_TOPICS=science,tech",
                    "SILENCE_TIMEOUT=30",
                    "TTS_ENABLED=true",
                    "TTS_VOICE=nova",
                    "USE_MICROPHONE=false",
                    "MICROPHONE_NAME=exit",
                    "");
            assertEquals(expected, content);
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

    @Test
    public void mainRunsWizardWhenSetupFlag(@TempDir Path tmp) throws Exception {
        Path env = Path.of(".env");
        Path backup = tmp.resolve("env.bak");
        boolean existed = Files.exists(env);
        if (existed) {
            Files.move(env, backup);
        }
        InputStream orig = System.in;
        try {
            String userInput = String.join("\n",
                    "baz",
                    "gpt-3.5-turbo",
                    "0.7",
                    "0.9",
                    "2048",
                    "es",
                    "casual",
                    "science,tech",
                    "30",
                    "true",
                    "nova",
                    "false",
                    "exit",
                    "");
            System.setIn(new ByteArrayInputStream(userInput.getBytes(StandardCharsets.UTF_8)));
            StreamBotApplication.main(new String[]{"--api-key", "foo", "--setup"});
            assertTrue(Files.exists(env), ".env should be created");
            String firstLine = Files.readAllLines(env).get(0);
            assertEquals("OPENAI_API_KEY=baz", firstLine);
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

    @Test
    public void helpFlagPrintsUsage() throws Exception {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        java.io.PrintStream origOut = System.out;
        java.io.InputStream origIn = System.in;
        try {
            System.setOut(new java.io.PrintStream(out));
            System.setIn(new ByteArrayInputStream(new byte[0]));
            StreamBotApplication.main(new String[]{"--help"});
        } finally {
            System.setOut(origOut);
            System.setIn(origIn);
        }
        String text = out.toString(java.nio.charset.StandardCharsets.UTF_8);
        assertTrue(text.contains("--api-key"), "usage should mention api-key");
        assertTrue(text.contains("--help"), "usage should mention help flag");
        assertTrue(text.contains("--push-key"), "usage should mention push key option");
    }
}

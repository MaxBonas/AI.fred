package com.example.streambot;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigTest {

    @AfterEach
    public void clear() {
        System.clearProperty("OPENAI_MODEL");
        System.clearProperty("OPENAI_TEMPERATURE");
        System.clearProperty("OPENAI_TOP_P");
        System.clearProperty("OPENAI_MAX_TOKENS");
        System.clearProperty("CONVERSATION_STYLE");
        System.clearProperty("PREFERRED_TOPICS");
        System.clearProperty("SILENCE_TIMEOUT");
        System.clearProperty("TTS_ENABLED");
        System.clearProperty("TTS_VOICE");
    }

    @Test
    public void loadReadsProperties() {
        System.setProperty("OPENAI_MODEL", "gpt-test");
        System.setProperty("OPENAI_TEMPERATURE", "0.5");
        System.setProperty("OPENAI_TOP_P", "0.8");
        System.setProperty("OPENAI_MAX_TOKENS", "1024");
        System.setProperty("CONVERSATION_STYLE", "formal");
        System.setProperty("PREFERRED_TOPICS", "one, two ,three");
        System.setProperty("SILENCE_TIMEOUT", "15");
        System.setProperty("TTS_ENABLED", "true");
        System.setProperty("TTS_VOICE", "onyx");

        Config cfg = Config.load();
        assertEquals("gpt-test", cfg.getModel());
        assertEquals(0.5, cfg.getTemperature());
        assertEquals(0.8, cfg.getTopP());
        assertEquals(1024, cfg.getMaxTokens());
        assertEquals(List.of("one", "two", "three"), cfg.getTopics());
        assertEquals("formal", cfg.getConversationStyle());
        assertEquals(15, cfg.getSilenceTimeout());
        assertTrue(cfg.isTtsEnabled());
        assertEquals("onyx", cfg.getTtsVoice());
    }

    @Test
    public void loadUsesDefaults() {
        Config cfg = Config.load();
        assertEquals("gpt-3.5-turbo", cfg.getModel());
        assertEquals(0.7, cfg.getTemperature());
        assertEquals(0.9, cfg.getTopP());
        assertEquals(2048, cfg.getMaxTokens());
        assertTrue(cfg.getTopics().isEmpty());
        assertEquals("neutral", cfg.getConversationStyle());
        assertEquals(30, cfg.getSilenceTimeout());
        assertFalse(cfg.isTtsEnabled());
        assertEquals("alloy", cfg.getTtsVoice());
    }
}

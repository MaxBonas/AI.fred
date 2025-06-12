package com.example.streambot;

import com.example.streambot.StreamBotApplication;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class StreamBotApplicationTest {

    @Test
    public void parsesApiKeyFlag() {
        Map<String, String> result = StreamBotApplication.parseArgs(new String[]{"--api-key", "foo"});
        assertEquals("foo", result.get("OPENAI_API_KEY"));
    }
}

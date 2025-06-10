package com.example.streambot;

import com.example.streambot.StreamBotApplication;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class StreamBotApplicationTest {

    @Test
    public void parsesModelPathFlag() {
        Map<String, String> result = StreamBotApplication.parseArgs(new String[]{"--model-path", "/x"});
        assertEquals("/x", result.get("MISTRAL_MODEL_PATH"));
    }
}

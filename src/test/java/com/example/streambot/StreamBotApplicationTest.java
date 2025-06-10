package com.example.streambot;

import com.example.streambot.StreamBotApplication;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class StreamBotApplicationTest {

    @SuppressWarnings("unchecked")
    private Map<String, String> invokeParseArgs(String... args) throws Exception {
        Method m = StreamBotApplication.class.getDeclaredMethod("parseArgs", String[].class);
        m.setAccessible(true);
        return (Map<String, String>) m.invoke(null, (Object) args);
    }

    @Test
    public void parsesModelPathFlag() throws Exception {
        Map<String, String> result = invokeParseArgs("--model-path", "/x");
        assertEquals("/x", result.get("MISTRAL_MODEL_PATH"));
    }
}

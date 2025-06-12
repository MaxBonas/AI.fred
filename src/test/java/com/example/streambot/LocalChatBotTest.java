package com.example.streambot;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

// Use a dummy service to avoid hitting the real OpenAI API
import com.example.streambot.DummyOpenAIService;

import static org.junit.jupiter.api.Assertions.*;

public class LocalChatBotTest {

    @Test
    public void replExitsOnExit() throws Exception {
        DummyOpenAIService svc = new DummyOpenAIService();
        LocalChatBot bot = new LocalChatBot(svc);

        InputStream origIn = System.in;
        System.setIn(new ByteArrayInputStream("hi\nexit\n".getBytes(StandardCharsets.UTF_8)));
        try {
            bot.start();
        } finally {
            System.setIn(origIn);
        }

        assertTrue(svc.closed, "service closed");
        assertEquals(List.of("hi"), svc.received);
    }

}

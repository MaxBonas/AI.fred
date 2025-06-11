package com.example.streambot;

import ai.djl.inference.Predictor;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LocalChatBotTest {

    @Test
    public void replExitsOnExit() throws Exception {
        DummyService svc = new DummyService();
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

    static class DummyService extends LocalMistralService {
        List<String> received = new ArrayList<>();
        boolean closed = false;

        DummyService() {
            super((Predictor<String, String>) null);
        }

        @Override
        public String ask(String prompt) {
            received.add(prompt);
            return "ok";
        }

        @Override
        public void close() {
            closed = true;
        }
    }
}

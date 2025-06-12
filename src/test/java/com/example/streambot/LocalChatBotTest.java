package com.example.streambot;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.List;

import com.example.streambot.Config;

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

    @Test
    public void suggestsTopicAfterSilence() throws Exception {
        System.setProperty("SILENCE_TIMEOUT", "1");
        System.setProperty("CONVERSATION_STYLE", "casual");
        System.setProperty("PREFERRED_TOPICS", "science");
        Config cfg = Config.load();
        DummyOpenAIService svc = new DummyOpenAIService();
        LocalChatBot bot = new LocalChatBot(svc, cfg);

        PipedOutputStream pos = new PipedOutputStream();
        PipedInputStream pis = new PipedInputStream(pos);
        InputStream orig = System.in;
        System.setIn(pis);
        Thread t = new Thread(bot::start);
        t.start();

        try {
            TimeUnit.MILLISECONDS.sleep(1200);
            pos.write("exit\n".getBytes(StandardCharsets.UTF_8));
            pos.flush();
            t.join(2000);
        } finally {
            System.setIn(orig);
            System.clearProperty("SILENCE_TIMEOUT");
            System.clearProperty("CONVERSATION_STYLE");
            System.clearProperty("PREFERRED_TOPICS");
        }

        assertTrue(svc.closed, "service closed");
        assertEquals(List.of("Suggest a casual conversation topic about science."), svc.received);
    }

}

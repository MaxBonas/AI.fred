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
import com.example.streambot.MicrophoneMonitor;
import com.example.streambot.OpenAIService;

// Use a dummy service to avoid hitting the real OpenAI API
import com.example.streambot.DummyOpenAIService;

import static org.junit.jupiter.api.Assertions.*;

public class LocalChatBotTest {

    private static class StubMonitor extends MicrophoneMonitor {
        private Runnable cb;
        StubMonitor() { super(() -> {}); }
        void setCallback(Runnable r) { this.cb = r; }
        @Override public void start() { }
        @Override public void stop() { }
        void trigger() { if (cb != null) cb.run(); }
    }

    private static class BotWithStubMic extends LocalChatBot {
        final StubMonitor stub;
        BotWithStubMic(OpenAIService svc, Config cfg, StubMonitor stub) {
            super(svc, cfg);
            this.stub = stub;
        }
        @Override
        protected MicrophoneMonitor createMonitor(Runnable cb) {
            stub.setCallback(cb);
            return stub;
        }
    }

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
        assertEquals(List.of("Sugiere un tema de conversación casual sobre science."), svc.received);
    }

    @Test
    public void microphoneActivityPreventsSuggestion() throws Exception {
        System.setProperty("SILENCE_TIMEOUT", "1");
        System.setProperty("USE_MICROPHONE", "true");
        Config cfg = Config.load();
        DummyOpenAIService svc = new DummyOpenAIService();
        StubMonitor stub = new StubMonitor();
        BotWithStubMic bot = new BotWithStubMic(svc, cfg, stub);

        PipedOutputStream pos = new PipedOutputStream();
        PipedInputStream pis = new PipedInputStream(pos);
        InputStream orig = System.in;
        System.setIn(pis);
        Thread t = new Thread(bot::start);
        t.start();

        try {
            TimeUnit.MILLISECONDS.sleep(500);
            stub.trigger();
            TimeUnit.MILLISECONDS.sleep(700);
            pos.write("exit\n".getBytes(StandardCharsets.UTF_8));
            pos.flush();
            t.join(2000);
        } finally {
            System.setIn(orig);
            System.clearProperty("SILENCE_TIMEOUT");
            System.clearProperty("USE_MICROPHONE");
        }

        assertTrue(svc.closed, "service closed");
        assertTrue(svc.received.isEmpty(), "no prompt sent");
    }

    @Test
    public void pushToTalkActivePreventsSuggestion() throws Exception {
        System.setProperty("SILENCE_TIMEOUT", "1");
        Config cfg = Config.load();
        DummyOpenAIService svc = new DummyOpenAIService();
        LocalChatBot bot = new LocalChatBot(svc, cfg);

        // simulate push-to-talk being held
        bot.getController().pushToTalkActive = true;

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
        }

        assertTrue(svc.closed, "service closed");
        assertTrue(svc.received.isEmpty(), "no prompt sent");
    }

}

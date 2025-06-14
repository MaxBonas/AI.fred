package com.example.streambot;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Field;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class ChatBotControllerTest {

    private static class StubMonitor extends MicrophoneMonitor {
        boolean started = false;
        boolean stopped = false;
        StubMonitor(Runnable cb) { super(cb); }
        @Override public synchronized void start() { started = true; }
        @Override public void stop() { stopped = true; }
    }

    private static class StubSpeech extends SpeechService {
        final List<String> spoken = new ArrayList<>();
        StubSpeech(Config cfg) { super(cfg); }
        @Override
        public void speak(String text) { spoken.add(text); }
    }

    @AfterEach
    public void clearProps() {
        System.clearProperty("SILENCE_TIMEOUT");
        System.clearProperty("CONVERSATION_STYLE");
        System.clearProperty("PREFERRED_TOPICS");
        System.clearProperty("USE_MICROPHONE");
    }


    @Test
    public void startSchedulesSilencePrompt() throws Exception {
        System.setProperty("SILENCE_TIMEOUT", "1");
        System.setProperty("CONVERSATION_STYLE", "casual");
        System.setProperty("PREFERRED_TOPICS", "science");
        System.setProperty("USE_MICROPHONE", "true");
        Config cfg = Config.load();
        DummyOpenAIService ai = new DummyOpenAIService("resp");
        StubMonitor mon = new StubMonitor(() -> {});
        StubSpeech speech = new StubSpeech(cfg);
        ChatBotController ctrl = new ChatBotController(ai, cfg, cb -> mon, speech);

        ctrl.start();
        TimeUnit.MILLISECONDS.sleep(1200);
        ctrl.stop();

        assertEquals(List.of("Sugiere un tema de conversación casual sobre science."), ai.received, "prompt sent");
        assertEquals(List.of("resp"), speech.spoken, "speech spoken");
        assertTrue(mon.started, "monitor started");
    }

    @Test
    public void stopStopsSchedulerAndMonitor() throws Exception {
        System.setProperty("SILENCE_TIMEOUT", "1");
        System.setProperty("USE_MICROPHONE", "true");
        Config cfg = Config.load();
        DummyOpenAIService ai = new DummyOpenAIService();
        StubMonitor mon = new StubMonitor(() -> {});
        StubSpeech speech = new StubSpeech(cfg);
        ChatBotController ctrl = new ChatBotController(ai, cfg, cb -> mon, speech);

        ctrl.start();
        ctrl.stop();

        Field schedField = ChatBotController.class.getDeclaredField("scheduler");
        schedField.setAccessible(true);
        ScheduledExecutorService sched = (ScheduledExecutorService) schedField.get(ctrl);
        assertTrue(sched.isShutdown(), "scheduler shutdown");
        assertTrue(mon.stopped, "monitor stopped");
    }

    @Test
    public void onUserSpeechTriggersServices() throws Exception {
        Config cfg = Config.load();
        DummyOpenAIService ai = new DummyOpenAIService("reply");
        StubSpeech speech = new StubSpeech(cfg);
        ChatBotController ctrl = new ChatBotController(ai, cfg, null, speech);

        ctrl.onUserSpeech("hi");

        assertEquals(List.of("hi"), ai.received, "input sent to service");
        assertEquals(List.of("reply"), speech.spoken, "speech triggered");
    }
}


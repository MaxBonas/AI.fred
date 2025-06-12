package com.example.streambot;

import java.util.ArrayList;
import java.util.List;

/**
 * Dummy implementation of {@link OpenAIService} used in tests to avoid real API calls.
 */
public class DummyOpenAIService extends OpenAIService {
    public final List<String> received = new ArrayList<>();
    public boolean closed = false;
    private final String reply;

    public DummyOpenAIService() {
        this("ok");
    }

    public DummyOpenAIService(String reply) {
        this.reply = reply;
    }

    @Override
    public String ask(String prompt) {
        received.add(prompt);
        return reply;
    }

    @Override
    public void close() {
        closed = true;
    }
}

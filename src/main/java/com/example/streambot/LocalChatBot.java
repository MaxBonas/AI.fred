package com.example.streambot;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.streambot.MicrophoneMonitor;
import com.example.streambot.SpeechService;

/**
 * A simple console-based chatbot that interacts with the OpenAI API
 * without connecting to external chat services.
 */
public class LocalChatBot {
    private static final Logger logger = LoggerFactory.getLogger(LocalChatBot.class);
    private final OpenAIService aiService;
    private final Config config;
    private final SpeechService speechService;
    
    /** Factory method for creating microphone monitors. */
    protected MicrophoneMonitor createMonitor(Runnable callback) {
        return new MicrophoneMonitor(callback);
    }

    public LocalChatBot() {
        this(Config.load());
    }

    /**
     * Create a bot using the provided configuration.
     */
    public LocalChatBot(Config config) {
        this(new OpenAIService(config), config);
    }

    /**
     * Create a bot with the given service. Primarily used for testing.
     */
    public LocalChatBot(OpenAIService service) {
        this(service, Config.load());
    }

    /**
     * Create a bot with the given service and configuration. Primarily used for testing.
     */
    public LocalChatBot(OpenAIService service, Config config) {
        this.aiService = service;
        this.config = config != null ? config : Config.load();
        this.speechService = new SpeechService(this.config);
    }

    /**
     * Start a simple REPL that sends user input to OpenAI and prints the response.
     */
    public void start() {
        long timeoutMillis = config.getSilenceTimeout() * 1000L;
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        AtomicLong lastInput = new AtomicLong(System.currentTimeMillis());
        MicrophoneMonitor monitor = null;
        Runnable silenceCheck = () -> {
            long now = System.currentTimeMillis();
            if (now - lastInput.get() >= timeoutMillis) {
                String prompt = buildSuggestionPrompt();
                logger.debug("Silence detected. Sending prompt: {}", prompt);
                String response = aiService.ask(prompt);
                logger.debug("Received suggestion: {}", response);
                System.out.println("AI: " + response);
                speechService.speak(response);
                lastInput.set(now);
            }
        };
        scheduler.scheduleAtFixedRate(silenceCheck, timeoutMillis, timeoutMillis, TimeUnit.MILLISECONDS);
        if (config.isUseMicrophone()) {
            monitor = createMonitor(() -> lastInput.set(System.currentTimeMillis()));
            monitor.start();
        }
        try (Scanner scanner = new Scanner(System.in)) {
            logger.info("ChatBot iniciado. Escribe 'exit' para salir.");
            while (scanner.hasNextLine()) {
                String input = scanner.nextLine().trim();
                lastInput.set(System.currentTimeMillis());
                if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
                    break;
                }
                if (input.isEmpty()) {
                    continue;
                }
                logger.debug("Sending prompt: {}", input);
                String response = aiService.ask(input);
                logger.debug("Received response: {}", response);
                System.out.println("AI: " + response);
                speechService.speak(response);
            }
        } finally {
            if (monitor != null) {
                monitor.stop();
            }
            scheduler.shutdownNow();
            aiService.close();
            logger.debug("ChatBot service closed");
        }
    }

    private String buildSuggestionPrompt() {
        String style = config.getConversationStyle();
        List<String> topics = config.getTopics();
        StringBuilder sb = new StringBuilder("Sugiere un tema de conversación");
        if (style != null && !style.isBlank() && !"neutral".equalsIgnoreCase(style)) {
            sb.append(' ').append(style);
        }
        if (!topics.isEmpty()) {
            sb.append(" sobre ").append(String.join(", ", topics));
        }
        sb.append('.');
        return sb.toString();
    }
}

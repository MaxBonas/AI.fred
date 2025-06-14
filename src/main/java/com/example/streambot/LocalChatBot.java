package com.example.streambot;

import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.streambot.MicrophoneMonitor;
import com.example.streambot.ChatBotController;

/**
 * A simple console-based chatbot that interacts with the OpenAI API
 * without connecting to external chat services.
 */
public class LocalChatBot {
    
    private static final Logger logger = LoggerFactory.getLogger(LocalChatBot.class);
    private final Config config;
    private final ChatBotController controller;
        
    /** Factory method for creating microphone monitors. */
    protected MicrophoneMonitor createMonitor(Runnable callback) {
        return new MicrophoneMonitor(callback, config.getMicrophoneName());
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
        this.config = config != null ? config : Config.load();
        this.controller = new ChatBotController(service, this.config, this::createMonitor);
    }

    /**
     * Create a bot using an already constructed controller.
     */
    public LocalChatBot(ChatBotController controller, Config config) {
        this.config = config != null ? config : Config.load();
        this.controller = controller != null
                ? controller
                : new ChatBotController(null, this.config, this::createMonitor);
    }

    /** Convenience constructor using the supplied controller. */
    public LocalChatBot(ChatBotController controller) {
        this(controller, Config.load());
    }

    /** Access the underlying controller. */
    public ChatBotController getController() {
        return controller;
    }

    /**
     * Start a simple REPL that sends user input to OpenAI and prints the response.
     */
    public void start() {
        controller.start();
        try (Scanner scanner = new Scanner(System.in)) {
            logger.info("ChatBot iniciado. Escribe 'exit' para salir.");
            while (scanner.hasNextLine()) {
                String input = scanner.nextLine().trim();
                if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
                    break;
                }
                if (input.isEmpty()) {
                    continue;
                }
                logger.debug("Enviando indicación: {}", input);
                controller.onUserSpeech(input);
            }
        } finally {
            controller.stop();
            logger.debug("Servicio de ChatBot cerrado");
        }
    }

}

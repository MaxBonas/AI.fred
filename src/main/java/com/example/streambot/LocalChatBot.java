package com.example.streambot;

import java.util.Scanner;

/**
 * A simple console-based chatbot that interacts with OpenAI directly
 * without connecting to Twitch.
 */
public class LocalChatBot {
    private final OpenAIService aiService;

    public LocalChatBot() {
        this.aiService = new OpenAIService();
    }

    /**
     * Start a simple REPL that sends user input to OpenAI and prints the response.
     */
    public void start() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Local ChatBot started. Type 'exit' to quit.");
            while (scanner.hasNextLine()) {
                String input = scanner.nextLine().trim();
                if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
                    break;
                }
                if (input.isEmpty()) {
                    continue;
                }
                String response = aiService.ask(input);
                System.out.println("AI: " + response);
            }
        }
    }
}

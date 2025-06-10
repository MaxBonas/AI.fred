package com.example.streambot;

import java.util.HashMap;
import java.util.Map;

public class StreamBotApplication {

    public static void main(String[] args) {
        SetupWizard.run();

        Map<String, String> cli = parseArgs(args);
        cli.forEach(System::setProperty);

        LocalChatBot bot = new LocalChatBot();
        bot.start();
    }

    private static Map<String, String> parseArgs(String[] args) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--mistral-key" -> {
                    if (i + 1 < args.length) map.put("MISTRAL_API_KEY", args[++i]);
                }
                case "--base-url" -> {
                    if (i + 1 < args.length) map.put("MISTRAL_BASE_URL", args[++i]);
                }
                case "--model" -> {
                    if (i + 1 < args.length) map.put("MISTRAL_MODEL", args[++i]);
                }
                default -> {
                }
            }
        }
        return map;
    }
}

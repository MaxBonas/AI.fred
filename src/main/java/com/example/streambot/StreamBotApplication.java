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

    // Package-private for tests
    static Map<String, String> parseArgs(String[] args) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            if ("--api-key".equals(args[i]) && i + 1 < args.length) {
                map.put("OPENAI_API_KEY", args[++i]);
            }
        }
        return map;
    }
}

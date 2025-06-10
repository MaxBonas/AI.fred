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
            if ("--model-path".equals(args[i]) && i + 1 < args.length) {
                map.put("MISTRAL_MODEL_PATH", args[++i]);
            }
        }
        return map;
    }
}

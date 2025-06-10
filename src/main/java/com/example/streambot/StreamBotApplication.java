package com.example.streambot;

import io.github.cdimascio.dotenv.Dotenv;

import java.util.HashMap;
import java.util.Map;

public class StreamBotApplication {

    public static void main(String[] args) {
        SetupWizard.run();

        Map<String, String> cli = parseArgs(args);
        cli.forEach(System::setProperty);

        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String useTwitchVal = System.getProperty("USE_TWITCH", dotenv.get("USE_TWITCH", "true"));
        boolean useTwitch = Boolean.parseBoolean(useTwitchVal);

        if (useTwitch) {
            ChatBot bot = new ChatBot();
            bot.start();
        } else {
            LocalChatBot bot = new LocalChatBot();
            bot.start();
        }
    }

    private static Map<String, String> parseArgs(String[] args) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--openai-key" -> {
                    if (i + 1 < args.length) map.put("OPENAI_API_KEY", args[++i]);
                }
                case "--twitch-token" -> {
                    if (i + 1 < args.length) map.put("TWITCH_OAUTH_TOKEN", args[++i]);
                }
                case "--channel" -> {
                    if (i + 1 < args.length) map.put("TWITCH_CHANNEL", args[++i]);
                }
                case "--obs-only" -> map.put("USE_TWITCH", "false");
                default -> {
                }
            }
        }
        return map;
    }
}

package com.example.streambot;

import io.github.cdimascio.dotenv.Dotenv;

public class StreamBotApplication {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        boolean useTwitch = Boolean.parseBoolean(dotenv.get("USE_TWITCH", "true"));

        if (useTwitch) {
            ChatBot bot = new ChatBot();
            bot.start();
        } else {
            LocalChatBot bot = new LocalChatBot();
            bot.start();
        }
    }
}

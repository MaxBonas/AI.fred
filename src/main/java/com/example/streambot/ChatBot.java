package com.example.streambot;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.auth.providers.OAuth2Credential;
import io.github.cdimascio.dotenv.Dotenv;

public class ChatBot {
    private final TwitchClient client;
    private final OpenAIService aiService;

    public ChatBot() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String token = dotenv.get("TWITCH_OAUTH_TOKEN");
        String channel = dotenv.get("TWITCH_CHANNEL");

        client = TwitchClientBuilder.builder()
                .withEnableChat(true)
                .withChatAccount(oauthCredential(token))
                .build();
        aiService = new OpenAIService();

        client.getEventManager().onEvent(ChannelMessageEvent.class, event -> {
            if (event.getChannel().getName().equalsIgnoreCase(channel)) {
                handleMessage(event);
            }
        });
    }

    private static OAuth2Credential oauthCredential(String token) {
        return new OAuth2Credential("twitch", token);
    }

    private void handleMessage(ChannelMessageEvent event) {
        // Example: when someone types !topic ask OpenAI for a question
        if (event.getMessage().startsWith("!topic")) {
            String response = aiService.ask("Give me an interesting question for a livestream audience.");
            client.getChat().sendMessage(event.getChannel().getName(), response);
        }
    }

    public void start() {
        // join channel defined in .env
        String channel = Dotenv.load().get("TWITCH_CHANNEL");
        client.getChat().joinChannel(channel);
    }
}

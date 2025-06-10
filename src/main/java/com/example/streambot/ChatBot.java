package com.example.streambot;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.auth.providers.OAuth2Credential;
import com.example.streambot.EnvUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatBot {
    private static final Logger logger = LoggerFactory.getLogger(ChatBot.class);

    private final TwitchClient client;
    private final OpenAIService aiService;

    public ChatBot() {
        String token = EnvUtils.get("TWITCH_OAUTH_TOKEN");
        String channel = EnvUtils.get("TWITCH_CHANNEL");

        if (token == null || token.isBlank() || channel == null || channel.isBlank()) {
            logger.error("TWITCH_OAUTH_TOKEN or TWITCH_CHANNEL missing; skipping Twitch connection.");
            client = null;
        } else {
            client = TwitchClientBuilder.builder()
                    .withEnableChat(true)
                    .withChatAccount(oauthCredential(token))
                    .build();

            logger.info("Conectando a Twitch en el canal {}", channel);

            client.getEventManager().onEvent(ChannelMessageEvent.class, event -> {
                if (event.getChannel().getName().equalsIgnoreCase(channel)) {
                    handleMessage(event);
                }
            });
        }
        aiService = new OpenAIService();
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
        if (client == null) {
            return;
        }
        String channel = EnvUtils.get("TWITCH_CHANNEL");
        logger.info("Uni\u00e9ndose al canal {}", channel);
        client.getChat().joinChannel(channel);
    }
}

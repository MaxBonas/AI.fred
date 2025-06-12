package com.example.streambot;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Utilities for environment lookup
import com.example.streambot.EnvUtils;

/**
 * Entry point for StreamBot.
 */

public class StreamBotApplication {
    private static final Logger logger = LoggerFactory.getLogger(StreamBotApplication.class);

    public static void main(String[] args) {
        logger.info("Starting StreamBot");
        Map<String, String> cli = parseArgs(args);
        logger.debug("Parsed CLI arguments: {}", cli);
        cli.forEach(System::setProperty);

        EnvUtils.reload();
        boolean forceSetup = Boolean.parseBoolean(cli.getOrDefault("SETUP", "false"));
        boolean keyMissing = EnvUtils.get("OPENAI_API_KEY") == null || EnvUtils.get("OPENAI_API_KEY").isBlank();
        if (forceSetup || keyMissing) {
            logger.info("Running setup wizard.");
            SetupWizard.run();
        }

        Config config = Config.load();
        LocalChatBot bot = new LocalChatBot(config);
        bot.start();
    }

    // Package-private for tests
    static Map<String, String> parseArgs(String[] args) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            if ("--api-key".equals(args[i]) && i + 1 < args.length) {
                map.put("OPENAI_API_KEY", args[++i]);
                logger.debug("Parsed api key from CLI");
            } else if ("--model".equals(args[i]) && i + 1 < args.length) {
                map.put("OPENAI_MODEL", args[++i]);
                logger.debug("Parsed model from CLI: {}", map.get("OPENAI_MODEL"));
            } else if ("--tts-enabled".equals(args[i]) && i + 1 < args.length) {
                map.put("TTS_ENABLED", args[++i]);
                logger.debug("Parsed TTS_ENABLED from CLI: {}", map.get("TTS_ENABLED"));
            } else if ("--tts-voice".equals(args[i]) && i + 1 < args.length) {
                map.put("TTS_VOICE", args[++i]);
                logger.debug("Parsed TTS_VOICE from CLI: {}", map.get("TTS_VOICE"));
            } else if ("--setup".equals(args[i])) {
                map.put("SETUP", "true");
                logger.debug("Parsed setup flag");
            }
        }
        return map;
    }
}

package com.example.streambot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple controller placeholder to handle user speech transcripts.
 */
public class ChatBotController {
    private static final Logger logger = LoggerFactory.getLogger(ChatBotController.class);

    /**
     * Handle transcribed user speech.
     *
     * @param transcript the recognized text from the microphone
     */
    public static void onUserSpeech(String transcript) {
        logger.info("Transcribed speech: {}", transcript);
    }
}

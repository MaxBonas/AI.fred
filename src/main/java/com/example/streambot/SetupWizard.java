package com.example.streambot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Access environment and system properties
import com.example.streambot.EnvUtils;

/**
 * Simple interactive wizard to create the .env file if it does not exist.
 */
public class SetupWizard {
    private static final Logger logger = LoggerFactory.getLogger(SetupWizard.class);
    /**
     * Run the wizard if .env is missing.
     */
    public static void run() {
        String existing = EnvUtils.get("OPENAI_API_KEY");
        if (existing != null && !existing.isBlank()) {
            logger.debug("API key already present; skipping wizard");
            return;
        }

        File env = new File(".env");
        if (env.exists()) {
            logger.debug(".env file already exists; skipping wizard");
            return;
        }

        try (Scanner scanner = new Scanner(System.in)) {
            logger.info("Starting setup wizard");
            System.out.println("Configuraci\u00f3n inicial de StreamBot:");
            System.out.print("OPENAI_API_KEY: ");
            String key = scanner.nextLine().trim();

            System.out.print("OPENAI_MODEL: ");
            String model = scanner.nextLine().trim();

            System.out.print("OPENAI_TEMPERATURE: ");
            String temp = scanner.nextLine().trim();

            System.out.print("OPENAI_TOP_P: ");
            String topP = scanner.nextLine().trim();

            System.out.print("OPENAI_MAX_TOKENS: ");
            String maxTokens = scanner.nextLine().trim();

            System.out.print("CONVERSATION_STYLE: ");
            String style = scanner.nextLine().trim();

            System.out.print("PREFERRED_TOPICS: ");
            String topics = scanner.nextLine().trim();

            System.out.print("SILENCE_TIMEOUT: ");
            String timeout = scanner.nextLine().trim();

            System.out.print("TTS_ENABLED: ");
            String ttsEnabled = scanner.nextLine().trim();

            System.out.print("TTS_VOICE: ");
            String ttsVoice = scanner.nextLine().trim();

            try (PrintWriter out = new PrintWriter(new FileWriter(env))) {
                out.println("OPENAI_API_KEY=" + key);
                out.println("OPENAI_MODEL=" + model);
                out.println("OPENAI_TEMPERATURE=" + temp);
                out.println("OPENAI_TOP_P=" + topP);
                out.println("OPENAI_MAX_TOKENS=" + maxTokens);
                out.println("CONVERSATION_STYLE=" + style);
                out.println("PREFERRED_TOPICS=" + topics);
                out.println("SILENCE_TIMEOUT=" + timeout);
                out.println("TTS_ENABLED=" + ttsEnabled);
                out.println("TTS_VOICE=" + ttsVoice);
            }

            System.setProperty("OPENAI_API_KEY", key);
            System.setProperty("OPENAI_MODEL", model);
            System.setProperty("OPENAI_TEMPERATURE", temp);
            System.setProperty("OPENAI_TOP_P", topP);
            System.setProperty("OPENAI_MAX_TOKENS", maxTokens);
            System.setProperty("CONVERSATION_STYLE", style);
            System.setProperty("PREFERRED_TOPICS", topics);
            System.setProperty("SILENCE_TIMEOUT", timeout);
            System.setProperty("TTS_ENABLED", ttsEnabled);
            System.setProperty("TTS_VOICE", ttsVoice);

            System.out.println("Archivo .env creado.\n");
            logger.info(".env file created");
        } catch (IOException e) {
            logger.error("Error al crear .env", e);
            System.err.println("Error al crear .env: " + e.getMessage());
        }
    }
}

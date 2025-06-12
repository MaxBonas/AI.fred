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

            try (PrintWriter out = new PrintWriter(new FileWriter(env))) {
                out.println("OPENAI_API_KEY=" + key);
            }
            System.setProperty("OPENAI_API_KEY", key);
            System.out.println("Archivo .env creado.\n");
            logger.info(".env file created");
        } catch (IOException e) {
            logger.error("Error al crear .env", e);
            System.err.println("Error al crear .env: " + e.getMessage());
        }
    }
}

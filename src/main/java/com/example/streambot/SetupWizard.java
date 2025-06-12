package com.example.streambot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

// Access environment and system properties
import com.example.streambot.EnvUtils;

/**
 * Simple interactive wizard to create the .env file if it does not exist.
 */
public class SetupWizard {
    /**
     * Run the wizard if .env is missing.
     */
    public static void run() {
        String existing = EnvUtils.get("OPENAI_API_KEY");
        if (existing != null && !existing.isBlank()) {
            return;
        }

        File env = new File(".env");
        if (env.exists()) {
            return;
        }

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Configuraci\u00f3n inicial de StreamBot:");
            System.out.print("OPENAI_API_KEY: ");
            String key = scanner.nextLine().trim();

            try (PrintWriter out = new PrintWriter(new FileWriter(env))) {
                out.println("OPENAI_API_KEY=" + key);
            }
            System.setProperty("OPENAI_API_KEY", key);
            System.out.println("Archivo .env creado.\n");
        } catch (IOException e) {
            System.err.println("Error al crear .env: " + e.getMessage());
        }
    }
}

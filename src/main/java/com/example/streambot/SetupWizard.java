package com.example.streambot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Simple interactive wizard to create the .env file if it does not exist.
 */
public class SetupWizard {
    /**
     * Run the wizard if .env is missing.
     */
    public static void run() {
        File env = new File(".env");
        if (env.exists()) {
            return;
        }

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Configuraci\u00f3n inicial de StreamBot:");
            System.out.print("OPENAI_API_KEY: ");
            String apiKey = scanner.nextLine().trim();
            System.out.print("TWITCH_OAUTH_TOKEN: ");
            String token = scanner.nextLine().trim();
            System.out.print("TWITCH_CHANNEL: ");
            String channel = scanner.nextLine().trim();

            try (PrintWriter out = new PrintWriter(new FileWriter(env))) {
                out.println("OPENAI_API_KEY=" + apiKey);
                out.println("TWITCH_OAUTH_TOKEN=" + token);
                out.println("TWITCH_CHANNEL=" + channel);
            }
            System.out.println("Archivo .env creado.\n");
        } catch (IOException e) {
            System.err.println("Error al crear .env: " + e.getMessage());
        }
    }
}

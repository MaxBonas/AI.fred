package com.example.streambot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Access environment and system properties
import com.example.streambot.EnvUtils;

/**
 * Simple interactive wizard to create or update the {@code .env} file.
 */
public class SetupWizard {
    private static final Logger logger = LoggerFactory.getLogger(SetupWizard.class);
    /** List of models that the application supports. */
    public static final List<String> SUPPORTED_MODELS = List.of(
            "gpt-3.5-turbo",
            "gpt-3.5-turbo-16k",
            "gpt-4",
            "gpt-4-32k");
    /**
     * Run the wizard to create or update the {@code .env} file.
     * Existing values are overwritten and system properties are updated.
     */
    public static void run() {
        File env = new File(".env");

        try (Scanner scanner = new Scanner(System.in)) {
            String lang = EnvUtils.get("SETUP_LANG", "es").toLowerCase();
            boolean en = lang.startsWith("en");
            logger.info(en ? "Starting setup wizard" : "Iniciando asistente de configuración");
            System.out.println(en ? "Initial StreamBot setup:" : "Configuración inicial de StreamBot:");
            System.out.print("OPENAI_API_KEY: ");
            String key = scanner.nextLine().trim();

            System.out.print("OPENAI_MODEL " + SUPPORTED_MODELS + ": ");
            String model = scanner.nextLine().trim();
            if (!SUPPORTED_MODELS.contains(model)) {
                String def = SUPPORTED_MODELS.get(0);
                System.out.println(en ? "Invalid model, using " + def : "Modelo no válido, se usará " + def);
                logger.warn("Modelo no soportado '{}', se utilizará {}", model, def);
                model = def;
            }

            System.out.print("OPENAI_TEMPERATURE: ");
            String temp = scanner.nextLine().trim();

            System.out.print("OPENAI_TOP_P: ");
            String topP = scanner.nextLine().trim();

            System.out.print("OPENAI_MAX_TOKENS: ");
            String maxTokens = scanner.nextLine().trim();

            System.out.print("OPENAI_LANGUAGE: ");
            String language = scanner.nextLine().trim();

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

            System.out.print("USE_MICROPHONE: ");
            String useMic = scanner.nextLine().trim();

            List<String> micNames = new ArrayList<>();
            DataLine.Info dinfo = new DataLine.Info(TargetDataLine.class, null);
            for (Mixer.Info mi : AudioSystem.getMixerInfo()) {
                Mixer m = AudioSystem.getMixer(mi);
                if (m.isLineSupported(dinfo)) {
                    micNames.add(mi.getName());
                }
            }
            if (!micNames.isEmpty()) {
                System.out.println(en ? "Available microphones:" : "Micrófonos disponibles:");
                for (String name : micNames) {
                    System.out.println("- " + name);
                }
            }
            System.out.print("MICROPHONE_NAME: ");
            String micName = scanner.nextLine().trim();

            try (PrintWriter out = new PrintWriter(new FileWriter(env))) {
                out.println("OPENAI_API_KEY=" + key);
                out.println("OPENAI_MODEL=" + model);
                out.println("OPENAI_TEMPERATURE=" + temp);
                out.println("OPENAI_TOP_P=" + topP);
                out.println("OPENAI_MAX_TOKENS=" + maxTokens);
                out.println("OPENAI_LANGUAGE=" + language);
                out.println("CONVERSATION_STYLE=" + style);
                out.println("PREFERRED_TOPICS=" + topics);
                out.println("SILENCE_TIMEOUT=" + timeout);
                out.println("TTS_ENABLED=" + ttsEnabled);
                out.println("TTS_VOICE=" + ttsVoice);
                out.println("USE_MICROPHONE=" + useMic);
                out.println("MICROPHONE_NAME=" + micName);
            }

            System.setProperty("OPENAI_API_KEY", key);
            System.setProperty("OPENAI_MODEL", model);
            System.setProperty("OPENAI_TEMPERATURE", temp);
            System.setProperty("OPENAI_TOP_P", topP);
            System.setProperty("OPENAI_MAX_TOKENS", maxTokens);
            System.setProperty("OPENAI_LANGUAGE", language);
            System.setProperty("CONVERSATION_STYLE", style);
            System.setProperty("PREFERRED_TOPICS", topics);
            System.setProperty("SILENCE_TIMEOUT", timeout);
            System.setProperty("TTS_ENABLED", ttsEnabled);
            System.setProperty("TTS_VOICE", ttsVoice);
            System.setProperty("USE_MICROPHONE", useMic);
            System.setProperty("MICROPHONE_NAME", micName);

            EnvUtils.reload();

            System.out.println(en ? ".env file created or updated.\n" : "Archivo .env creado o actualizado.\n");
            logger.info("Archivo .env creado o actualizado");
        } catch (IOException e) {
            logger.error("Error al crear .env", e);
            System.err.println("Error al crear .env: " + e.getMessage());
        }
    }
}

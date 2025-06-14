package com.example.streambot;

import java.util.HashMap;
import java.util.Map;

import com.github.kwhat.jnativehook.GlobalScreen;

import com.example.streambot.PushToTalk;
import com.example.streambot.ChatBotController;
import com.example.streambot.MicrophoneMonitor;

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
        logger.info("Iniciando StreamBot");
        Map<String, String> cli = parseArgs(args);
        logger.debug("Argumentos de CLI parseados: {}", cli);
        if (Boolean.parseBoolean(cli.getOrDefault("HELP", "false"))) {
            printUsage();
            return;
        }
        cli.forEach(System::setProperty);

        EnvUtils.reload();
        boolean forceSetup = Boolean.parseBoolean(cli.getOrDefault("SETUP", "false"));
        boolean keyMissing = EnvUtils.get("OPENAI_API_KEY") == null || EnvUtils.get("OPENAI_API_KEY").isBlank();
        if (forceSetup || keyMissing) {
            logger.info("Ejecutando asistente de configuración.");
            SetupWizard.run();
        }

        Config config = Config.load();
        ChatBotController controller = new ChatBotController(null, config,
                cb -> new MicrophoneMonitor(cb, config.getMicrophoneName()));
        PushToTalk ptt = null;
        try {
            GlobalScreen.registerNativeHook();
            ptt = new PushToTalk(controller, config.getPushKeyCode());
            GlobalScreen.addNativeKeyListener(ptt);
        } catch (Throwable e) {
            logger.warn("No se pudo registrar el hook global", e);
        }

        LocalChatBot bot = new LocalChatBot(controller, config);
        bot.start();

        if (ptt != null) {
            try {
                GlobalScreen.removeNativeKeyListener(ptt);
                GlobalScreen.unregisterNativeHook();
            } catch (Exception e) {
                logger.warn("Error al desregistrar el hook global", e);
            }
        }
    }

    // Package-private for tests
    static Map<String, String> parseArgs(String[] args) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            if ("--api-key".equals(args[i]) && i + 1 < args.length) {
                map.put("OPENAI_API_KEY", args[++i]);
                    logger.debug("Clave API obtenida de CLI");
            } else if ("--model".equals(args[i]) && i + 1 < args.length) {
                map.put("OPENAI_MODEL", args[++i]);
                    logger.debug("Modelo obtenido de CLI: {}", map.get("OPENAI_MODEL"));
            } else if ("--tts-enabled".equals(args[i]) && i + 1 < args.length) {
                map.put("TTS_ENABLED", args[++i]);
                    logger.debug("TTS_ENABLED obtenido de CLI: {}", map.get("TTS_ENABLED"));
            } else if ("--tts-voice".equals(args[i]) && i + 1 < args.length) {
                map.put("TTS_VOICE", args[++i]);
                    logger.debug("TTS_VOICE obtenido de CLI: {}", map.get("TTS_VOICE"));
            } else if ("--push-key".equals(args[i]) && i + 1 < args.length) {
                map.put("PUSH_KEY", args[++i]);
                    logger.debug("PUSH_KEY obtenido de CLI: {}", map.get("PUSH_KEY"));
            } else if ("--setup".equals(args[i])) {
                map.put("SETUP", "true");
                    logger.debug("Bandera de configuración detectada");
            } else if ("--help".equals(args[i])) {
                map.put("HELP", "true");
                    logger.debug("Bandera de ayuda detectada");
            }
        }
        return map;
    }

    private static void printUsage() {
        String msg = String.join(System.lineSeparator(),
                "Uso: java -jar streambot.jar [opciones]",
                "  --api-key CLAVE       clave API de OpenAI",
                "  --model MODELO       modelo de OpenAI",
                "  --tts-enabled VAL   habilitar texto a voz",
                "  --tts-voice VOZ   voz para la síntesis",
                "  --push-key TECLA   tecla para hablar",
                "  --setup             ejecutar configuración interactiva",
                "  --help              mostrar este mensaje",
                "");
        System.out.println(msg);
    }
}

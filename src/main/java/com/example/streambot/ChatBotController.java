package com.example.streambot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * Controller that handles interaction with the OpenAI API and manages
 * silence monitoring.
 */
public class ChatBotController {
    private static final Logger logger = LoggerFactory.getLogger(ChatBotController.class);

    private final OpenAIService aiService;
    private final SpeechService speechService;
    private final Config config;
    private final AtomicLong lastInput = new AtomicLong(System.currentTimeMillis());
    private final Function<Runnable, MicrophoneMonitor> monitorFactory;
    private ScheduledExecutorService scheduler;
    private MicrophoneMonitor monitor;

    public ChatBotController(OpenAIService service, Config config,
                             Function<Runnable, MicrophoneMonitor> monitorFactory) {
        this.aiService = service != null ? service : new OpenAIService(config);
        this.config = config != null ? config : Config.load();
        this.speechService = new SpeechService(this.config);
        this.monitorFactory = monitorFactory;
    }

    /** Start monitoring for silence and microphone activity. */
    public void start() {
        long timeoutMillis = config.getSilenceTimeout() * 1000L;
        scheduler = Executors.newSingleThreadScheduledExecutor();
        Runnable silenceCheck = () -> {
            long now = System.currentTimeMillis();
            if (now - lastInput.get() >= timeoutMillis) {
                String prompt = buildSuggestionPrompt();
                logger.debug("Silencio detectado. Enviando indicación: {}", prompt);
                String response = aiService.ask(prompt);
                logger.debug("Sugerencia recibida: {}", response);
                speechService.speak(response);
                lastInput.set(now);
            }
        };
        scheduler.scheduleAtFixedRate(silenceCheck, timeoutMillis, timeoutMillis, TimeUnit.MILLISECONDS);
        if (config.isUseMicrophone() && monitorFactory != null) {
            monitor = monitorFactory.apply(() -> lastInput.set(System.currentTimeMillis()));
            monitor.start();
        }
    }

    /** Stop any background resources. */
    public void stop() {
        if (monitor != null) {
            monitor.stop();
        }
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
        aiService.close();
    }

    /**
     * Handle transcribed user speech by sending it to OpenAI and speaking the reply.
     */
    public void onUserSpeech(String text) {
        if (text == null || text.isBlank()) {
            return;
        }
        lastInput.set(System.currentTimeMillis());
        logger.info("Transcribed speech: {}", text);
        String response = aiService.ask(text);
        logger.debug("Respuesta recibida: {}", response);
        speechService.speak(response);
    }

    private String buildSuggestionPrompt() {
        String style = config.getConversationStyle();
        List<String> topics = config.getTopics();
        StringBuilder sb = new StringBuilder("Sugiere un tema de conversación");
        if (style != null && !style.isBlank() && !"neutral".equalsIgnoreCase(style)) {
            sb.append(' ').append(style);
        }
        if (!topics.isEmpty()) {
            sb.append(" sobre ").append(String.join(", ", topics));
        }
        sb.append('.');
        return sb.toString();
    }
}

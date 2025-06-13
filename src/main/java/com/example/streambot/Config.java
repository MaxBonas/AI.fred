package com.example.streambot;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple configuration holder for StreamBot.
 */
public class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    private final String model;
    private final double temperature;
    private final double topP;
    private final int maxTokens;
    private final String language;
    private final List<String> topics;
    private final String conversationStyle;
    private final int silenceTimeout;
    private final boolean ttsEnabled;
    private final String ttsVoice;
    private final boolean useMicrophone;
    private final String microphoneName;

    private Config(String model, double temperature, double topP, int maxTokens,
                    List<String> topics, String conversationStyle,
                    int silenceTimeout, boolean ttsEnabled, String ttsVoice,
                    boolean useMicrophone, String microphoneName, String language) {
        this.model = model;
        this.temperature = temperature;
        this.topP = topP;
        this.maxTokens = maxTokens;
        this.language = language;
        this.topics = List.copyOf(topics);
        this.conversationStyle = conversationStyle;
        this.silenceTimeout = silenceTimeout;
        this.ttsEnabled = ttsEnabled;
        this.ttsVoice = ttsVoice;
        this.useMicrophone = useMicrophone;
        this.microphoneName = microphoneName;
    }

    public String getModel() {
        return model;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getTopP() {
        return topP;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public String getLanguage() {
        return language;
    }

    public List<String> getTopics() {
        return topics;
    }

    public String getConversationStyle() {
        return conversationStyle;
    }

    public int getSilenceTimeout() {
        return silenceTimeout;
    }

    public boolean isTtsEnabled() {
        return ttsEnabled;
    }

    public String getTtsVoice() {
        return ttsVoice;
    }

    public boolean isUseMicrophone() {
        return useMicrophone;
    }

    public String getMicrophoneName() {
        return microphoneName;
    }

    /**
     * Load configuration values from system properties or a .env file.
     * Defaults are used when a property is not present or cannot be parsed.
     */
    public static Config load() {
        EnvUtils.reload();
        String model = EnvUtils.get("OPENAI_MODEL", "gpt-3.5-turbo");
        double temperature = parseDouble(EnvUtils.get("OPENAI_TEMPERATURE"), 0.7);
        temperature = clamp("OPENAI_TEMPERATURE", temperature, 0.7, 0, 2);
        double topP = parseDouble(EnvUtils.get("OPENAI_TOP_P"), 0.9);
        topP = clamp("OPENAI_TOP_P", topP, 0.9, 0, 1);
        int maxTokens = parseInt(EnvUtils.get("OPENAI_MAX_TOKENS"), 2048);
        String language = EnvUtils.get("OPENAI_LANGUAGE", "es");
        String style = EnvUtils.get("CONVERSATION_STYLE", "neutral");
        int timeout = parseInt(EnvUtils.get("SILENCE_TIMEOUT"), 30);
        boolean ttsEnabled = Boolean.parseBoolean(EnvUtils.get("TTS_ENABLED", "false"));
        String ttsVoice = EnvUtils.get("TTS_VOICE", "alloy");
        boolean useMic = Boolean.parseBoolean(EnvUtils.get("USE_MICROPHONE", "false"));
        String micName = EnvUtils.get("MICROPHONE_NAME", "");
        String topicsProp = EnvUtils.get("PREFERRED_TOPICS", "");
        List<String> topics = new ArrayList<>();
        if (topicsProp != null && !topicsProp.isBlank()) {
            for (String t : topicsProp.split(",")) {
                String trimmed = t.trim();
                if (!trimmed.isEmpty()) {
                    topics.add(trimmed);
                }
            }
        }
        return new Config(model, temperature, topP, maxTokens,
                topics, style, timeout, ttsEnabled, ttsVoice,
                useMic, micName, language);
    }

    private static double parseDouble(String val, double def) {
        if (val == null || val.isBlank()) {
            return def;
        }
        try {
            return Double.parseDouble(val);
        } catch (NumberFormatException ex) {
            return def;
        }
    }

    private static int parseInt(String val, int def) {
        if (val == null || val.isBlank()) {
            return def;
        }
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException ex) {
            return def;
        }
    }

    private static double clamp(String name, double val, double def,
                                double min, double max) {
        if (val < min || val > max) {
            logger.warn("{}={} fuera del rango {}-{}; se usa el valor por defecto {}",
                    name, val, min, max, def);
            return def;
        }
        return val;
    }
}

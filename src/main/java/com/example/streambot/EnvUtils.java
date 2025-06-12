package com.example.streambot;

import io.github.cdimascio.dotenv.Dotenv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to manage environment variables and system properties.
 */
public final class EnvUtils {
    private static final Logger logger = LoggerFactory.getLogger(EnvUtils.class);
    private static final Dotenv DOTENV = Dotenv.configure().ignoreIfMissing().load();

    private EnvUtils() {
        // Utility class
    }

    /**
     * Resolve a property value looking first at system properties then at the
     * loaded .env file. If none are defined, returns {@code null}.
     */
    public static String get(String key) {
        return get(key, null);
    }

    /**
     * Resolve a property value looking first at system properties then at the
     * loaded .env file. If not found, returns the provided default value.
     */
    public static String get(String key, String def) {
        String value = System.getProperty(key);
        if (value == null || value.isBlank()) {
            value = DOTENV.get(key);
        }
        if ((value == null || value.isBlank()) && def != null) {
            logger.debug("{} not found; using default", key);
            return def;
        }
        logger.debug("Resolved {} present? {}", key, value != null && !value.isBlank());
        return value;
    }
}

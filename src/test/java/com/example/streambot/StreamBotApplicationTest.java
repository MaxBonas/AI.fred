import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class StreamBotApplicationTest {

    @SuppressWarnings("unchecked")
    private Map<String, String> invokeParseArgs(String... args) throws Exception {
        Method m = StreamBotApplication.class.getDeclaredMethod("parseArgs", String[].class);
        m.setAccessible(true);
        return (Map<String, String>) m.invoke(null, (Object) args);
    }

    @Test
    public void parsesMistralKeyFlag() throws Exception {
        Map<String, String> result = invokeParseArgs("--mistral-key", "abc");
        assertEquals("abc", result.get("MISTRAL_API_KEY"));
    }

    @Test
    public void parsesBaseUrlFlag() throws Exception {
        Map<String, String> result = invokeParseArgs("--base-url", "http://x");
        assertEquals("http://x", result.get("MISTRAL_BASE_URL"));
    }

    @Test
    public void parsesModelFlag() throws Exception {
        Map<String, String> result = invokeParseArgs("--model", "mixtral");
        assertEquals("mixtral", result.get("MISTRAL_MODEL"));
    }

    @Test
    public void parsesAllFlagsTogether() throws Exception {
        Map<String, String> result = invokeParseArgs(
                "--mistral-key", "k",
                "--base-url", "u",
                "--model", "m"
        );
        assertEquals("k", result.get("MISTRAL_API_KEY"));
        assertEquals("u", result.get("MISTRAL_BASE_URL"));
        assertEquals("m", result.get("MISTRAL_MODEL"));
    }
}

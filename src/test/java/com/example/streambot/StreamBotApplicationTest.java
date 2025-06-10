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
    public void parsesOpenAIKeyFlag() throws Exception {
        Map<String, String> result = invokeParseArgs("--openai-key", "abc");
        assertEquals("abc", result.get("OPENAI_API_KEY"));
    }

    @Test
    public void parsesTwitchTokenFlag() throws Exception {
        Map<String, String> result = invokeParseArgs("--twitch-token", "oauth:xyz");
        assertEquals("oauth:xyz", result.get("TWITCH_OAUTH_TOKEN"));
    }

    @Test
    public void parsesChannelFlag() throws Exception {
        Map<String, String> result = invokeParseArgs("--channel", "mychan");
        assertEquals("mychan", result.get("TWITCH_CHANNEL"));
    }

    @Test
    public void parsesObsOnlyFlag() throws Exception {
        Map<String, String> result = invokeParseArgs("--obs-only");
        assertEquals("false", result.get("USE_TWITCH"));
    }

    @Test
    public void parsesAllFlagsTogether() throws Exception {
        Map<String, String> result = invokeParseArgs(
                "--openai-key", "k",
                "--twitch-token", "t",
                "--channel", "c",
                "--obs-only"
        );
        assertEquals("k", result.get("OPENAI_API_KEY"));
        assertEquals("t", result.get("TWITCH_OAUTH_TOKEN"));
        assertEquals("c", result.get("TWITCH_CHANNEL"));
        assertEquals("false", result.get("USE_TWITCH"));
    }
}

package com.example.streambot;

import org.junit.jupiter.api.Test;

import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDList;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import static org.junit.jupiter.api.Assertions.*;

public class LocalMistralServiceTest {

    @Test
    public void closeDoesNotThrow() {
        LocalMistralService service = assertDoesNotThrow(() -> new LocalMistralService((Predictor<String, String>) null));
        assertDoesNotThrow(service::close);
    }

    @Test
    public void askReturnsDecodedString() throws Exception {
        LocalMistralService service = new LocalMistralService((Predictor<String, String>) null) {
            @Override
            public String ask(String prompt) {
                return "mock";
            }
        };
        String out = service.ask("ignored");
        assertEquals("mock", out);
    }

    static class NoopTranslator implements Translator<String, String> {
        @Override
        public NDList processInput(TranslatorContext ctx, String input) {
            return new NDList();
        }

        @Override
        public String processOutput(TranslatorContext ctx, NDList list) {
            return "";
        }
    }
}

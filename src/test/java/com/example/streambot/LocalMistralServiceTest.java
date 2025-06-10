package com.example.streambot;

import org.junit.jupiter.api.Test;

import ai.djl.Device;
import ai.djl.Model;
import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDList;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import java.lang.reflect.Field;
import sun.misc.Unsafe;
import static org.junit.jupiter.api.Assertions.*;

public class LocalMistralServiceTest {

    @Test
    public void closeDoesNotThrow() {
        LocalMistralService service = assertDoesNotThrow(() -> newInstance());
        assertDoesNotThrow(service::close);
    }

    @Test
    public void askReturnsDecodedString() throws Exception {
        LocalMistralService service = newInstance();
        Field f = LocalMistralService.class.getDeclaredField("predictor");
        f.setAccessible(true);
        f.set(service, newDummyPredictor());
        String out = service.ask("ignored");
        assertEquals("mock", out);
    }

    private static LocalMistralService newInstance() throws Exception {
        Field uf = Unsafe.class.getDeclaredField("theUnsafe");
        uf.setAccessible(true);
        Unsafe unsafe = (Unsafe) uf.get(null);
        return (LocalMistralService) unsafe.allocateInstance(LocalMistralService.class);
    }

    private static Predictor<String, String> newDummyPredictor() throws Exception {
        Field uf = Unsafe.class.getDeclaredField("theUnsafe");
        uf.setAccessible(true);
        Unsafe unsafe = (Unsafe) uf.get(null);
        return (Predictor<String, String>) unsafe.allocateInstance(DummyPredictor.class);
    }

    static class DummyPredictor extends Predictor<String, String> {
        DummyPredictor() {
            super(null, null, null, false);
        }

        @Override
        public String predict(String input) {
            return "mock";
        }

        @Override
        public void close() {
            // no-op
        }
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

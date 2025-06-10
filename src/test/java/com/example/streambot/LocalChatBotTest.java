package com.example.streambot;

import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LocalChatBotTest {

    @Test
    public void replExitsOnExit() throws Exception {
        LocalChatBot bot = newInstance();
        DummyService svc = dummyService();
        setField(bot, "aiService", svc);

        InputStream origIn = System.in;
        System.setIn(new ByteArrayInputStream("hi\nexit\n".getBytes(StandardCharsets.UTF_8)));
        try {
            bot.start();
        } finally {
            System.setIn(origIn);
        }

        assertTrue(svc.closed, "service closed");
        assertEquals(List.of("hi"), svc.received);
    }

    private static LocalChatBot newInstance() throws Exception {
        Field uf = Unsafe.class.getDeclaredField("theUnsafe");
        uf.setAccessible(true);
        Unsafe unsafe = (Unsafe) uf.get(null);
        return (LocalChatBot) unsafe.allocateInstance(LocalChatBot.class);
    }

    private static DummyService dummyService() throws Exception {
        Field uf = Unsafe.class.getDeclaredField("theUnsafe");
        uf.setAccessible(true);
        Unsafe unsafe = (Unsafe) uf.get(null);
        DummyService svc = (DummyService) unsafe.allocateInstance(DummyService.class);
        svc.received = new ArrayList<>();
        return svc;
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        Field f = LocalChatBot.class.getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    static class DummyService extends LocalMistralService {
        List<String> received;
        boolean closed = false;

        @Override
        public String ask(String prompt) {
            received.add(prompt);
            return "ok";
        }

        @Override
        public void close() {
            closed = true;
        }
    }
}

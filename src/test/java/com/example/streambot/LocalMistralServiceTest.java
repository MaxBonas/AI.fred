package com.example.streambot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class LocalMistralServiceTest {

    @Test
    public void closeDoesNotThrow() {
        LocalMistralService service = new LocalMistralService();
        assertDoesNotThrow(service::close);
    }
}

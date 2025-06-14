package com.example.streambot;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;

import static org.junit.jupiter.api.Assertions.*;

public class SpeechServiceTest {
    private static class DummyResponse implements HttpResponse<byte[]> {
        private final byte[] body;
        private final int status;
        DummyResponse(byte[] body) { this(body, 200); }
        DummyResponse(byte[] body, int status) {
            this.body = body;
            this.status = status;
        }
        @Override public int statusCode() { return status; }
        @Override public HttpRequest request() { return null; }
        @Override public Optional<HttpResponse<byte[]>> previousResponse() { return Optional.empty(); }
        @Override public HttpHeaders headers() { return HttpHeaders.of(Map.of(), (a,b) -> true); }
        @Override public byte[] body() { return body; }
        @Override public Optional<SSLSession> sslSession() { return Optional.empty(); }
        @Override public URI uri() { return URI.create("https://api.openai.com"); }
        @Override public HttpClient.Version version() { return HttpClient.Version.HTTP_1_1; }
    }

    private static class StubHttpClient extends HttpClient {
        private final HttpClient delegate = HttpClient.newHttpClient();
        private final HttpResponse<byte[]> response;
        private final IOException exception;
        HttpRequest lastRequest;
        String body;
        StubHttpClient(HttpResponse<byte[]> response) {
            this.response = response;
            this.exception = null;
        }
        StubHttpClient(IOException ex) {
            this.response = null;
            this.exception = ex;
        }
        @Override public Optional<java.net.CookieHandler> cookieHandler() { return delegate.cookieHandler(); }
        @Override public Optional<Duration> connectTimeout() { return delegate.connectTimeout(); }
        @Override public Redirect followRedirects() { return delegate.followRedirects(); }
        @Override public Optional<java.net.ProxySelector> proxy() { return delegate.proxy(); }
        @Override public SSLContext sslContext() { return delegate.sslContext(); }
        @Override public SSLParameters sslParameters() { return delegate.sslParameters(); }
        @Override public Optional<java.net.Authenticator> authenticator() { return delegate.authenticator(); }
        @Override public HttpClient.Version version() { return delegate.version(); }
        @Override public Optional<Executor> executor() { return delegate.executor(); }
        @Override
        public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> handler) throws IOException {
            lastRequest = request;
            body = request.bodyPublisher().map(TestUtils::publisherToString).orElse(null);
            if (exception != null) {
                throw exception;
            }
            return (HttpResponse<T>) response;
        }
        @Override public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> handler) { return CompletableFuture.completedFuture(null); }
        @Override public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> handler, HttpResponse.PushPromiseHandler<T> h) { return CompletableFuture.completedFuture(null); }
    }

    @AfterEach
    public void clearProps() {
        System.clearProperty("OPENAI_API_KEY");
        System.clearProperty("TTS_ENABLED");
        System.clearProperty("TTS_VOICE");
        javazoom.jl.player.Player.played = false;
    }

    @Test
    public void speakSendsPayloadAndPlaysAudio() throws Exception {
        System.setProperty("OPENAI_API_KEY", "key");
        System.setProperty("TTS_ENABLED", "true");
        System.setProperty("TTS_VOICE", "nova");
        Config cfg = Config.load();
        byte[] data = {1,2,3};
        DummyResponse resp = new DummyResponse(data);
        StubHttpClient client = new StubHttpClient(resp);
        SpeechService svc = new SpeechService(client, cfg);

        svc.speak("hi");

        assertTrue(javazoom.jl.player.Player.played, "playback should occur");
        assertNotNull(client.lastRequest, "request sent");
        Map<?, ?> payload = new com.fasterxml.jackson.databind.ObjectMapper()
                .readValue(client.body, Map.class);
        assertEquals("tts-1", payload.get("model"));
        assertEquals("hi", payload.get("input"));
        assertEquals("nova", payload.get("voice"));
    }

    @Test
    public void speakEncodesNewlines() throws Exception {
        System.setProperty("OPENAI_API_KEY", "key");
        System.setProperty("TTS_ENABLED", "true");
        System.setProperty("TTS_VOICE", "nova");
        Config cfg = Config.load();
        DummyResponse resp = new DummyResponse(new byte[0]);
        StubHttpClient client = new StubHttpClient(resp);
        SpeechService svc = new SpeechService(client, cfg);

        svc.speak("hello\nworld");

        assertNotNull(client.body, "payload sent");
        Map<?, ?> map = new com.fasterxml.jackson.databind.ObjectMapper().readValue(client.body, Map.class);
        assertEquals("hello\nworld", map.get("input"));
        assertEquals("tts-1", map.get("model"));
        assertEquals("nova", map.get("voice"));
    }

    @Test
    public void speakHandlesErrors() {
        System.setProperty("OPENAI_API_KEY", "key");
        System.setProperty("TTS_ENABLED", "true");
        Config cfg = Config.load();
        StubHttpClient client = new StubHttpClient(new IOException("boom"));
        SpeechService svc = new SpeechService(client, cfg);

        assertDoesNotThrow(() -> svc.speak("hello"));
        assertFalse(javazoom.jl.player.Player.played, "play should not be called");
    }

    @Test
    public void speakSkipsPlaybackOnBadStatus() throws Exception {
        System.setProperty("OPENAI_API_KEY", "key");
        System.setProperty("TTS_ENABLED", "true");
        System.setProperty("TTS_VOICE", "nova");
        Config cfg = Config.load();
        DummyResponse resp = new DummyResponse(new byte[] {1, 2, 3}, 500);
        StubHttpClient client = new StubHttpClient(resp);
        SpeechService svc = new SpeechService(client, cfg);

        svc.speak("fail");

        assertFalse(javazoom.jl.player.Player.played, "playback should be skipped");
    }
}

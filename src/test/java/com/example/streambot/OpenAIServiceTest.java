package com.example.streambot;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.example.streambot.Config;
import com.example.streambot.TestUtils;

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

public class OpenAIServiceTest {

    private static class SimpleResponse implements HttpResponse<String> {
        private final String body;
        private final int status;
        SimpleResponse(String body) {
            this(body, 200);
        }
        SimpleResponse(String body, int status) {
            this.body = body;
            this.status = status;
        }
        @Override public int statusCode() { return status; }
        @Override public HttpRequest request() { return null; }
        @Override public Optional<HttpResponse<String>> previousResponse() { return Optional.empty(); }
        @Override public HttpHeaders headers() { return HttpHeaders.of(Map.of(), (a,b) -> true); }
        @Override public String body() { return body; }
        @Override public Optional<SSLSession> sslSession() { return Optional.empty(); }
        @Override public URI uri() { return URI.create("https://api.openai.com"); }
        @Override public HttpClient.Version version() { return HttpClient.Version.HTTP_1_1; }
    }

    private static class StubHttpClient extends HttpClient {
        private final HttpClient delegate = HttpClient.newHttpClient();
        private final HttpResponse<String> response;
        private final IOException exception;
        HttpRequest lastRequest;
        String body;
        StubHttpClient(HttpResponse<String> response) {
            this.response = response;
            this.exception = null;
        }
        StubHttpClient(IOException exception) {
            this.response = null;
            this.exception = exception;
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
            body = request.bodyPublisher()
                    .map(bp -> TestUtils.publisherToString(bp))
                    .orElse(null);
            if (exception != null) {
                throw exception;
            }
            return (HttpResponse<T>) response;
        }
        @Override
        public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> handler) {
            return CompletableFuture.completedFuture(null);
        }
        @Override
        public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> handler, HttpResponse.PushPromiseHandler<T> pushPromiseHandler) {
            return CompletableFuture.completedFuture(null);
        }
    }

    @AfterEach
    public void clearKey() {
        System.clearProperty("OPENAI_API_KEY");
        System.clearProperty("OPENAI_MODEL");
        System.clearProperty("OPENAI_LANGUAGE");
    }

    @Test
    public void askParsesResponse() {
        String json = "{\"choices\":[{\"message\":{\"content\":\"hello\"}}]}";
        System.setProperty("OPENAI_API_KEY", "key");
        HttpResponse<String> resp = new SimpleResponse(json);
        Config cfg = Config.load();
        OpenAIService svc = new OpenAIService(new StubHttpClient(resp), cfg);
        String reply = svc.ask("hi");
        assertEquals("hello", reply);
    }

    @Test
    public void askReturnsEmptyOnException() {
        System.setProperty("OPENAI_API_KEY", "key");
        Config cfg = Config.load();
        OpenAIService svc = new OpenAIService(new StubHttpClient(new IOException("boom")), cfg);
        String reply = svc.ask("hi");
        assertEquals("", reply);
    }

    @Test
    public void askReturnsEmptyOnNon200Status() {
        System.setProperty("OPENAI_API_KEY", "key");
        HttpResponse<String> resp = new SimpleResponse("{}", 500);
        Config cfg = Config.load();
        OpenAIService svc = new OpenAIService(new StubHttpClient(resp), cfg);
        String reply = svc.ask("hi");
        assertEquals("", reply);
    }

    @Test
    public void threadNotInterruptedOnIOException() {
        System.setProperty("OPENAI_API_KEY", "key");
        Config cfg = Config.load();
        OpenAIService svc = new OpenAIService(new StubHttpClient(new IOException("fail")), cfg);
        assertFalse(Thread.currentThread().isInterrupted());
        svc.ask("hi");
        assertFalse(Thread.currentThread().isInterrupted());
    }

    @Test
    public void usesModelFromEnv() throws Exception {
        System.setProperty("OPENAI_API_KEY", "key");
        System.setProperty("OPENAI_MODEL", "gpt-test");
        HttpResponse<String> resp = new SimpleResponse("{\"choices\":[{\"message\":{\"content\":\"ok\"}}]}");
        Config cfg = Config.load();
        OpenAIService svc = new OpenAIService(new StubHttpClient(resp), cfg);
        svc.ask("hi");
        var field = OpenAIService.class.getDeclaredField("model");
        field.setAccessible(true);
        assertEquals("gpt-test", field.get(svc));
    }

    @Test
    public void payloadIncludesConfigValues() throws Exception {
        System.setProperty("OPENAI_API_KEY", "key");
        System.setProperty("OPENAI_TEMPERATURE", "0.6");
        System.setProperty("OPENAI_TOP_P", "0.8");
        System.setProperty("OPENAI_MAX_TOKENS", "123");
        HttpResponse<String> resp = new SimpleResponse("{\"choices\":[{\"message\":{\"content\":\"ok\"}}]}");
        StubHttpClient client = new StubHttpClient(resp);
        Config cfg = Config.load();
        OpenAIService svc = new OpenAIService(client, cfg);
        svc.ask("hi");
        String body = client.body;
        assertTrue(body.contains("\"temperature\":0.6"));
        assertTrue(body.contains("\"top_p\":0.8"));
        assertTrue(body.contains("\"max_tokens\":123"));
    }

    @Test
    public void includesLanguageSystemMessage() throws Exception {
        System.setProperty("OPENAI_API_KEY", "key");
        System.setProperty("OPENAI_LANGUAGE", "fr");
        HttpResponse<String> resp = new SimpleResponse("{\"choices\":[{\"message\":{\"content\":\"ok\"}}]}");
        StubHttpClient client = new StubHttpClient(resp);
        Config cfg = Config.load();
        OpenAIService svc = new OpenAIService(client, cfg);
        svc.ask("hi");
        String body = client.body;
        assertTrue(body.contains("Responde siempre en fr"));
    }

    private String invokeParse(String json) throws Exception {
        var m = OpenAIService.class.getDeclaredMethod("parseContent", String.class);
        m.setAccessible(true);
        return (String) m.invoke(null, json);
    }

    @Test
    public void parseReturnsEmptyWhenChoicesMissing() throws Exception {
        assertEquals("", invokeParse("{}"));
    }

    @Test
    public void parseReturnsEmptyWhenMessageContentMissing() throws Exception {
        assertEquals("", invokeParse("{\"choices\":[{\"message\":{}}]}"));
    }

    @Test
    public void parseReturnsContentNormally() throws Exception {
        assertEquals("hi", invokeParse("{\"choices\":[{\"message\":{\"content\":\"hi\"}}]}"));
    }
}

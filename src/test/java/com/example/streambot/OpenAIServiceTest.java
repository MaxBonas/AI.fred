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

public class OpenAIServiceTest {

    private static class SimpleResponse implements HttpResponse<String> {
        private final String body;
        SimpleResponse(String body) {
            this.body = body;
        }
        @Override public int statusCode() { return 200; }
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
    }

    @Test
    public void askParsesResponse() {
        String json = "{\"choices\":[{\"message\":{\"content\":\"hello\"}}]}";
        System.setProperty("OPENAI_API_KEY", "key");
        HttpResponse<String> resp = new SimpleResponse(json);
        OpenAIService svc = new OpenAIService(new StubHttpClient(resp));
        String reply = svc.ask("hi");
        assertEquals("hello", reply);
    }

    @Test
    public void askReturnsEmptyOnException() {
        System.setProperty("OPENAI_API_KEY", "key");
        OpenAIService svc = new OpenAIService(new StubHttpClient(new IOException("boom")));
        String reply = svc.ask("hi");
        assertEquals("", reply);
    }
}

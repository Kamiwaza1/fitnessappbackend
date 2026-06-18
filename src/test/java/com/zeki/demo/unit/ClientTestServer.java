package com.zeki.demo.unit;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

final class ClientTestServer implements AutoCloseable {

    private final HttpServer server;
    private final List<String> requests = new ArrayList<>();
    private Function<String, Response> handler = path -> Response.json(404, "{}");

    ClientTestServer() {
        try {
            server = HttpServer.create(new InetSocketAddress(0), 0);
            server.createContext("/", this::handle);
            server.start();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    String baseUrl() {
        return "http://localhost:" + server.getAddress().getPort();
    }

    List<String> requests() {
        return requests;
    }

    void respondWith(Function<String, Response> nextHandler) {
        handler = nextHandler;
    }

    private void handle(HttpExchange exchange) throws IOException {
        String pathAndQuery = exchange.getRequestURI().toString();
        requests.add(pathAndQuery);
        Response response = handler.apply(pathAndQuery);
        byte[] body = response.body().getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(response.status(), body.length);
        exchange.getResponseBody().write(body);
        exchange.close();
    }

    @Override
    public void close() {
        server.stop(0);
    }

    record Response(int status, String body) {
        static Response json(int status, String body) {
            return new Response(status, body);
        }
    }
}

package com.carbonfootprint;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class ApiServer {

    public static void main(String[] args) {

        try {

            HttpServer server = HttpServer.create(
                    new InetSocketAddress(8080), 0
            );

            server.createContext("/api/status", (HttpExchange exchange) -> {

                exchange.getResponseHeaders().add(
                        "Access-Control-Allow-Origin", "*"
                );

                exchange.getResponseHeaders().add(
                        "Access-Control-Allow-Methods", "GET, OPTIONS"
                );

                exchange.getResponseHeaders().add(
                        "Access-Control-Allow-Headers", "Content-Type"
                );

                if (exchange.getRequestMethod().equals("OPTIONS")) {
                    exchange.sendResponseHeaders(204, -1);
                    exchange.close();
                    return;
                }

                if (!exchange.getRequestMethod().equals("GET")) {
                    exchange.sendResponseHeaders(405, -1);
                    exchange.close();
                    return;
                }

                String resposta = """
                        {
                            "status": "online",
                            "mensagem": "API conectada com sucesso!"
                        }
                        """;

                byte[] respostaBytes = resposta.getBytes(
                        StandardCharsets.UTF_8
                );

                exchange.getResponseHeaders().set(
                        "Content-Type", "application/json; charset=UTF-8"
                );

                exchange.sendResponseHeaders(
                        200, respostaBytes.length
                );

                try (OutputStream output = exchange.getResponseBody()) {
                    output.write(respostaBytes);
                }

            });

            server.start();

            System.out.println("API iniciada com sucesso!");
            System.out.println("Porta: 8080");
            System.out.println("http://localhost:8080/api/status");

        } catch (IOException e) {
            System.out.println("Erro ao iniciar API: " + e.getMessage());
        }
    }
}
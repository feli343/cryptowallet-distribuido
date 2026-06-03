package com.cryptospace.gateway;

import com.cryptospace.shared.CarteiraResponse;
import com.cryptospace.shared.CarteiraService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.concurrent.Executors;

public class ApiGatewayApplication {
    private static final int PORTA_HTTP = 8080;
    private static final String RMI_URL = "rmi://localhost:1099/CarteiraService";

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORTA_HTTP), 0);
        server.createContext("/rpc", ApiGatewayApplication::handleRpc);
        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();

        System.out.println("API Gateway iniciado em http://localhost:" + PORTA_HTTP + "/rpc");
        System.out.println("Aguardando chamadas JSON-RPC do frontend...");
    }

    private static void handleRpc(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            send(exchange, 405, JsonRpcUtil.error(-32600, "Apenas POST é aceito para JSON-RPC", "null"));
            return;
        }

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        JsonRpcRequest request;

        try {
            request = JsonRpcUtil.parseRequest(body);
        } catch (Exception exception) {
            send(exchange, 200, JsonRpcUtil.error(-32700, "Erro ao interpretar o JSON enviado", "null"));
            return;
        }

        String id = request.getIdRaw();

        if (!"2.0".equals(request.getJsonrpc())) {
            send(exchange, 200, JsonRpcUtil.error(-32600, "Campo jsonrpc deve ser 2.0", id));
            return;
        }

        if (!"buscarCarteiraByHash".equals(request.getMethod())) {
            send(exchange, 200, JsonRpcUtil.error(-32601, "Método não encontrado", id));
            return;
        }

        if (request.getHash() == null || request.getHash().isBlank()) {
            send(exchange, 200, JsonRpcUtil.error(-32602, "Parâmetro hash é obrigatório", id));
            return;
        }

        try {
            CarteiraService service = (CarteiraService) Naming.lookup(RMI_URL);
            CarteiraResponse carteira = service.buscarCarteiraByHash(request.getHash().trim());
            send(exchange, 200, JsonRpcUtil.success(carteira, id));
        } catch (ConnectException exception) {
            send(exchange, 200, JsonRpcUtil.error(-32000, "Servidor de Dados Indisponível", id));
        } catch (RemoteException exception) {
            send(exchange, 200, JsonRpcUtil.error(-32000, "Falha de comunicação com o DATA-CORE", id));
        } catch (NotBoundException exception) {
            send(exchange, 200, JsonRpcUtil.error(-32000, "Serviço RMI não registrado", id));
        } catch (Exception exception) {
            send(exchange, 200, JsonRpcUtil.error(-32099, "Erro inesperado na API Gateway", id));
        }
    }

    private static void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
    }

    private static void send(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(bytes);
        }
    }
}

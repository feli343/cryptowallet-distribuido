package com.cryptospace.gateway;

public class JsonRpcRequest {
    private final String jsonrpc;
    private final String method;
    private final String hash;
    private final String idRaw;

    public JsonRpcRequest(String jsonrpc, String method, String hash, String idRaw) {
        this.jsonrpc = jsonrpc;
        this.method = method;
        this.hash = hash;
        this.idRaw = idRaw;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public String getMethod() {
        return method;
    }

    public String getHash() {
        return hash;
    }

    public String getIdRaw() {
        return idRaw;
    }
}

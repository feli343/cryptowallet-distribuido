package com.cryptospace.gateway;

import com.cryptospace.shared.CarteiraResponse;
import com.cryptospace.shared.TransacaoResponse;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JsonRpcUtil {
    private JsonRpcUtil() {
    }

    public static JsonRpcRequest parseRequest(String body) {
        String jsonrpc = extractString(body, "jsonrpc");
        String method = extractString(body, "method");
        String hash = extractFirstParam(body);
        String idRaw = extractId(body);
        return new JsonRpcRequest(jsonrpc, method, hash, idRaw);
    }

    public static String success(CarteiraResponse carteira, String idRaw) {
        StringBuilder transacoesJson = new StringBuilder();
        transacoesJson.append("[");

        for (int i = 0; i < carteira.getTransacoes().size(); i++) {
            TransacaoResponse transacao = carteira.getTransacoes().get(i);
            if (i > 0) {
                transacoesJson.append(",");
            }

            transacoesJson.append("{")
                    .append("\"id\":").append(transacao.getId()).append(",")
                    .append("\"tipo\":\"").append(escape(transacao.getTipo())).append("\",")
                    .append("\"valorBtc\":").append(number(transacao.getValorBtc())).append(",")
                    .append("\"descricao\":\"").append(escape(transacao.getDescricao())).append("\",")
                    .append("\"dataMovimentacao\":\"").append(escape(transacao.getDataMovimentacao())).append("\"")
                    .append("}");
        }

        transacoesJson.append("]");

        return "{"
                + "\"jsonrpc\":\"2.0\","
                + "\"result\":{"
                + "\"hash\":\"" + escape(carteira.getHash()) + "\","
                + "\"saldoTotalBtc\":" + number(carteira.getSaldoTotalBtc()) + ","
                + "\"transacoes\":" + transacoesJson
                + "},"
                + "\"id\":" + safeId(idRaw)
                + "}";
    }

    public static String error(int code, String message, String idRaw) {
        return "{"
                + "\"jsonrpc\":\"2.0\","
                + "\"error\":{"
                + "\"code\":" + code + ","
                + "\"message\":\"" + escape(message) + "\""
                + "},"
                + "\"id\":" + safeId(idRaw)
                + "}";
    }

    private static String extractString(String body, String field) {
        Pattern pattern = Pattern.compile("\\\"" + field + "\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(body);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private static String extractFirstParam(String body) {
        Pattern pattern = Pattern.compile("\\\"params\\\"\\s*:\\s*\\[\\s*\\\"([^\\\"]*)\\\"", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(body);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private static String extractId(String body) {
        Pattern pattern = Pattern.compile("\\\"id\\\"\\s*:\\s*(\\\"[^\\\"]*\\\"|-?\\d+|null)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(body);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "null";
    }

    private static String escape(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static String number(BigDecimal value) {
        if (value == null) {
            return "0";
        }
        return value.toPlainString();
    }

    private static String safeId(String idRaw) {
        if (idRaw == null || idRaw.isBlank()) {
            return "null";
        }
        return idRaw;
    }
}

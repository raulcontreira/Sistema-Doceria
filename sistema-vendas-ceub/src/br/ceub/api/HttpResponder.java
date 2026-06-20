package br.ceub.api;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import br.ceub.util.JsonUtil;

/**
 * Metodos auxiliares para ler o corpo de uma requisicao HTTP e escrever
 * respostas em formato JSON, evitando repetir esse codigo em cada
 * handler da API.
 */
public final class HttpResponder {

    private HttpResponder() {
    }

    /**
     * Le todo o corpo da requisicao HTTP e devolve como String (UTF-8).
     */
    public static String lerCorpo(HttpExchange exchange) throws IOException {
        byte[] bytes = exchange.getRequestBody().readAllBytes();
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Envia uma resposta JSON de sucesso com o objeto/lista/mapa informado.
     */
    public static void enviarJson(HttpExchange exchange, int statusHttp, Object corpo) throws IOException {
        String json = JsonUtil.toJson(corpo);
        enviarTexto(exchange, statusHttp, json);
    }

    /**
     * Envia uma resposta JSON padronizada de erro: {"erro": "mensagem"}.
     */
    public static void enviarErro(HttpExchange exchange, int statusHttp, String mensagem) throws IOException {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("erro", mensagem);
        enviarJson(exchange, statusHttp, corpo);
    }

    private static void enviarTexto(HttpExchange exchange, int statusHttp, String texto) throws IOException {
        byte[] bytes = texto.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusHttp, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}

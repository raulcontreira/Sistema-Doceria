package br.ceub.api.handlers;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import br.ceub.api.HttpResponder;
import br.ceub.controller.AuthController;
import br.ceub.service.AutenticacaoService;
import br.ceub.service.RegraNegocioException;

/**
 * Handler da unica rota PUBLICA da API: {@code POST /api/auth/login}.
 *
 * Esta rota NAO exige token (ela e justamente quem fornece o token), por
 * isso implementa {@link HttpHandler} diretamente, em vez de estender
 * {@link br.ceub.api.HandlerProtegido}. Todas as demais rotas da API
 * (clientes, produtos, vendas, relatorios) exigem o token gerado aqui.
 *
 * Exemplo de requisicao:
 * <pre>
 * POST /api/auth/login
 * Content-Type: application/json
 *
 * { "login": "admin", "senha": "admin123" }
 * </pre>
 *
 * Exemplo de resposta:
 * <pre>
 * { "token": "eyJhbGciOi...", "nome": "Administrador", "perfil": "ADMIN" }
 * </pre>
 */
public class AuthHandler implements HttpHandler {

    private final AuthController authController = new AuthController();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                HttpResponder.enviarErro(exchange, 405, "Metodo nao permitido. Use POST.");
                return;
            }

            String corpo = HttpResponder.lerCorpo(exchange);
            Map<String, Object> dados = br.ceub.util.JsonUtil.parseObjeto(corpo);
            String login = (String) dados.get("login");
            String senha = (String) dados.get("senha");

            AutenticacaoService.LoginResultado resultado = authController.login(login, senha);

            Map<String, Object> resposta = new LinkedHashMap<>();
            resposta.put("token", resultado.token);
            resposta.put("nome", resultado.usuario.getNome());
            resposta.put("perfil", resultado.usuario.getPerfil().name());

            HttpResponder.enviarJson(exchange, 200, resposta);

        } catch (RegraNegocioException e) {
            HttpResponder.enviarErro(exchange, 401, e.getMessage());
        } catch (Exception e) {
            HttpResponder.enviarErro(exchange, 500, "Erro interno: " + e.getMessage());
        } finally {
            exchange.close();
        }
    }
}

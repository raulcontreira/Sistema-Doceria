package br.ceub.api;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import br.ceub.security.JwtUtil;
import br.ceub.service.RegraNegocioException;

/**
 * Handler HTTP base com PROTECAO POR JWT.
 *
 * Este e o componente que cumpre o requisito "API protegida" +
 * "Autenticacao baseada em Token": antes de qualquer rota concreta
 * (clientes, produtos, vendas, relatorios) ser executada, este handler
 * confere se o cabecalho HTTP {@code Authorization: Bearer <token>}
 * contem um JWT valido e nao expirado. Se nao contiver, a requisicao
 * e recusada com HTTP 401 (Unauthorized) e o metodo da rota nunca chega
 * a ser executado.
 *
 * As subclasses (ClienteHandler, ProdutoHandler, VendaHandler,
 * RelatorioHandler) implementam apenas o metodo {@link #tratar}, que so
 * e chamado depois que o token ja foi validado.
 */
public abstract class HandlerProtegido implements HttpHandler {

    @Override
    public final void handle(HttpExchange exchange) throws IOException {
        try {
            String cabecalhoAuth = exchange.getRequestHeaders().getFirst("Authorization");
            String token = extrairToken(cabecalhoAuth);

            JwtUtil.TokenInfo tokenInfo = JwtUtil.validarToken(token);
            if (!tokenInfo.valido) {
                HttpResponder.enviarErro(exchange, 401, "Acesso negado: " + tokenInfo.motivoFalha);
                return;
            }

            tratar(exchange, tokenInfo);

        } catch (RegraNegocioException e) {
            HttpResponder.enviarErro(exchange, 400, e.getMessage());
        } catch (Exception e) {
            HttpResponder.enviarErro(exchange, 500, "Erro interno: " + e.getMessage());
        } finally {
            exchange.close();
        }
    }

    /**
     * Implementado por cada handler concreto. So e chamado quando o
     * token JWT enviado na requisicao ja foi validado com sucesso.
     */
    protected abstract void tratar(HttpExchange exchange, JwtUtil.TokenInfo usuarioAutenticado) throws IOException;

    private String extrairToken(String cabecalhoAuth) {
        if (cabecalhoAuth == null) {
            return null;
        }
        if (cabecalhoAuth.startsWith("Bearer ")) {
            return cabecalhoAuth.substring("Bearer ".length()).trim();
        }
        return cabecalhoAuth.trim();
    }
}

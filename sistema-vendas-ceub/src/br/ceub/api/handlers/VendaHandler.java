package br.ceub.api.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import br.ceub.api.HandlerProtegido;
import br.ceub.api.HttpResponder;
import br.ceub.controller.VendaController;
import br.ceub.model.Venda;
import br.ceub.security.JwtUtil;
import br.ceub.service.ItemPedido;
import br.ceub.util.JsonUtil;

/**
 * Handler REST do registro de vendas, protegido por JWT.
 *
 * Rotas atendidas (todas sob {@code /api/vendas}):
 * <ul>
 *   <li>GET    /api/vendas              -> lista todas as vendas</li>
 *   <li>GET    /api/vendas/{id}         -> busca uma venda pelo id</li>
 *   <li>POST   /api/vendas              -> registra uma nova venda</li>
 *   <li>POST   /api/vendas/{id}/cancelar -> cancela uma venda existente</li>
 * </ul>
 *
 * Exemplo de corpo para registrar uma venda:
 * <pre>
 * {
 *   "clienteId": 1,
 *   "itens": [
 *     { "produtoId": 1, "quantidade": 2 },
 *     { "produtoId": 3, "quantidade": 1 }
 *   ]
 * }
 * </pre>
 */
public class VendaHandler extends HandlerProtegido {

    private static final String PREFIXO_ROTA = "/api/vendas";
    private final VendaController vendaController = new VendaController();

    @Override
    protected void tratar(HttpExchange exchange, JwtUtil.TokenInfo usuarioAutenticado) throws IOException {
        String metodo = exchange.getRequestMethod();
        String caminhoRestante = extrairCaminhoRestante(exchange.getRequestURI().getPath());

        if ("GET".equalsIgnoreCase(metodo) && caminhoRestante.isEmpty()) {
            List<Venda> vendas = vendaController.listarTodos();
            HttpResponder.enviarJson(exchange, 200, vendas);

        } else if ("GET".equalsIgnoreCase(metodo)) {
            int id = Integer.parseInt(caminhoRestante);
            Venda venda = vendaController.buscarPorId(id);
            HttpResponder.enviarJson(exchange, 200, venda);

        } else if ("POST".equalsIgnoreCase(metodo) && caminhoRestante.isEmpty()) {
            Venda venda = registrarVendaApartirDoJson(HttpResponder.lerCorpo(exchange));
            HttpResponder.enviarJson(exchange, 201, venda);

        } else if ("POST".equalsIgnoreCase(metodo) && caminhoRestante.endsWith("/cancelar")) {
            int id = Integer.parseInt(caminhoRestante.replace("/cancelar", ""));
            Venda venda = vendaController.cancelarVenda(id);
            HttpResponder.enviarJson(exchange, 200, venda);

        } else {
            HttpResponder.enviarErro(exchange, 405, "Metodo/rota nao suportado para /api/vendas");
        }
    }

    @SuppressWarnings("unchecked")
    private Venda registrarVendaApartirDoJson(String json) {
        Map<String, Object> dados = JsonUtil.parseObjeto(json);
        int clienteId = (int) ((Number) dados.get("clienteId")).doubleValue();

        List<Object> itensJson = (List<Object>) dados.get("itens");
        List<ItemPedido> pedidos = new ArrayList<>();
        if (itensJson != null) {
            for (Object itemObj : itensJson) {
                Map<String, Object> item = (Map<String, Object>) itemObj;
                int produtoId = (int) ((Number) item.get("produtoId")).doubleValue();
                int quantidade = (int) ((Number) item.get("quantidade")).doubleValue();
                pedidos.add(new ItemPedido(produtoId, quantidade));
            }
        }

        return vendaController.registrarVenda(clienteId, pedidos);
    }

    private String extrairCaminhoRestante(String caminho) {
        String resto = caminho.substring(PREFIXO_ROTA.length());
        return resto.replaceAll("^/+", "").trim();
    }
}

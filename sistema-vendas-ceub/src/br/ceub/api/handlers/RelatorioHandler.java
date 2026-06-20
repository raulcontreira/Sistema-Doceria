package br.ceub.api.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import br.ceub.api.HandlerProtegido;
import br.ceub.api.HttpResponder;
import br.ceub.controller.RelatorioController;
import br.ceub.security.JwtUtil;
import br.ceub.service.RelatorioService;

/**
 * Handler REST dos relatorios de vendas, protegido por JWT.
 *
 * Rota atendida: GET /api/relatorios/vendas
 * Retorna a quantidade de vendas, faturamento total, ticket medio e o
 * ranking de produtos mais vendidos (considerando todo o historico de
 * vendas concluidas).
 */
public class RelatorioHandler extends HandlerProtegido {

    private final RelatorioController relatorioController = new RelatorioController();

    @Override
    protected void tratar(HttpExchange exchange, JwtUtil.TokenInfo usuarioAutenticado) throws IOException {
        String metodo = exchange.getRequestMethod();

        if (!"GET".equalsIgnoreCase(metodo)) {
            HttpResponder.enviarErro(exchange, 405, "Metodo nao suportado para /api/relatorios/vendas");
            return;
        }

        RelatorioService.ResumoVendas resumo = relatorioController.gerarResumoGeral();
        HttpResponder.enviarJson(exchange, 200, converterParaMapa(resumo));
    }

    private Map<String, Object> converterParaMapa(RelatorioService.ResumoVendas resumo) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("quantidadeVendas", resumo.quantidadeVendas);
        mapa.put("faturamentoTotal", resumo.faturamentoTotal);
        mapa.put("ticketMedio", resumo.ticketMedio);

        List<Object> produtos = new ArrayList<>();
        for (RelatorioService.ItemRelatorioProduto item : resumo.produtosMaisVendidos) {
            Map<String, Object> itemMapa = new LinkedHashMap<>();
            itemMapa.put("nomeProduto", item.nomeProduto);
            itemMapa.put("quantidadeVendida", item.quantidadeVendida);
            itemMapa.put("faturamento", item.faturamento);
            produtos.add(itemMapa);
        }
        mapa.put("produtosMaisVendidos", produtos);
        return mapa;
    }
}

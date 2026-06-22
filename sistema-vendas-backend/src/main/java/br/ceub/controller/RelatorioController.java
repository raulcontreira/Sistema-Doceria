package br.ceub.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ceub.service.RelatorioService;

/**
 * Controller REST dos relatorios de vendas.
 *
 * Rota: {@code GET /api/relatorios/vendas} — retorna quantidade de
 * vendas, faturamento total, ticket medio e o ranking de produtos mais
 * vendidos (considerando todo o historico de vendas concluidas).
 */
@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping("/vendas")
    public RelatorioService.ResumoVendas resumoVendas() {
        return relatorioService.gerarResumoGeral();
    }
}

package br.ceub.controller;

import java.time.LocalDateTime;

import br.ceub.service.RelatorioService;

/**
 * Controller dos relatorios de vendas. Repassa as chamadas vindas da
 * tela Swing (TelaRelatorios) e dos handlers REST (RelatorioHandler)
 * para o {@link RelatorioService}.
 */
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController() {
        this.relatorioService = new RelatorioService();
    }

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    public RelatorioService.ResumoVendas gerarResumoGeral() {
        return relatorioService.gerarResumoGeral();
    }

    public RelatorioService.ResumoVendas gerarResumoPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return relatorioService.gerarResumoPorPeriodo(inicio, fim);
    }
}

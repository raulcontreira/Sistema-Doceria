package br.ceub.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.ceub.model.ItemVenda;
import br.ceub.model.Venda;

/**
 * Geracao dos RELATORIOS DE VENDAS exigidos pelo projeto.
 *
 * Todos os relatorios sao calculados a partir da lista de vendas
 * (somente as com status CONCLUIDA contam para faturamento), o que
 * evita duplicar regras de negocio em outro lugar do sistema.
 */
public class RelatorioService {

    private final VendaService vendaService;

    public RelatorioService() {
        this.vendaService = new VendaService();
    }

    public RelatorioService(VendaService vendaService) {
        this.vendaService = vendaService;
    }

    /**
     * Linha do relatorio "produtos mais vendidos": nome do produto,
     * quantidade total vendida e faturamento gerado por ele.
     */
    public static class ItemRelatorioProduto {
        public final String nomeProduto;
        public final int quantidadeVendida;
        public final double faturamento;

        public ItemRelatorioProduto(String nomeProduto, int quantidadeVendida, double faturamento) {
            this.nomeProduto = nomeProduto;
            this.quantidadeVendida = quantidadeVendida;
            this.faturamento = faturamento;
        }
    }

    /**
     * Resumo geral de vendas em um determinado periodo (ou de todo o
     * historico, se {@code inicio}/{@code fim} forem null).
     */
    public static class ResumoVendas {
        public final int quantidadeVendas;
        public final double faturamentoTotal;
        public final double ticketMedio;
        public final List<ItemRelatorioProduto> produtosMaisVendidos;

        public ResumoVendas(int quantidadeVendas, double faturamentoTotal,
                             double ticketMedio, List<ItemRelatorioProduto> produtosMaisVendidos) {
            this.quantidadeVendas = quantidadeVendas;
            this.faturamentoTotal = faturamentoTotal;
            this.ticketMedio = ticketMedio;
            this.produtosMaisVendidos = produtosMaisVendidos;
        }
    }

    /**
     * Resumo considerando TODAS as vendas concluidas ja registradas.
     */
    public ResumoVendas gerarResumoGeral() {
        List<Venda> vendasConcluidas = filtrarConcluidas(vendaService.listarTodos());
        return montarResumo(vendasConcluidas);
    }

    /**
     * Resumo considerando apenas vendas concluidas dentro do periodo informado.
     */
    public ResumoVendas gerarResumoPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        List<Venda> vendasNoPeriodo = filtrarConcluidas(vendaService.buscarPorPeriodo(inicio, fim));
        return montarResumo(vendasNoPeriodo);
    }

    private List<Venda> filtrarConcluidas(List<Venda> vendas) {
        List<Venda> resultado = new ArrayList<>();
        for (Venda venda : vendas) {
            if (venda.getStatus() == Venda.Status.CONCLUIDA) {
                resultado.add(venda);
            }
        }
        return resultado;
    }

    private ResumoVendas montarResumo(List<Venda> vendas) {
        double faturamentoTotal = 0;
        Map<String, int[]> quantidadePorProduto = new LinkedHashMap<>(); // [0]=quantidade
        Map<String, double[]> faturamentoPorProduto = new LinkedHashMap<>(); // [0]=faturamento

        for (Venda venda : vendas) {
            faturamentoTotal += venda.getValorTotal();
            for (ItemVenda item : venda.getItens()) {
                quantidadePorProduto.putIfAbsent(item.getNomeProduto(), new int[]{0});
                quantidadePorProduto.get(item.getNomeProduto())[0] += item.getQuantidade();

                faturamentoPorProduto.putIfAbsent(item.getNomeProduto(), new double[]{0});
                faturamentoPorProduto.get(item.getNomeProduto())[0] += item.getSubtotal();
            }
        }

        List<ItemRelatorioProduto> produtosMaisVendidos = new ArrayList<>();
        for (Map.Entry<String, int[]> entrada : quantidadePorProduto.entrySet()) {
            String nomeProduto = entrada.getKey();
            int quantidade = entrada.getValue()[0];
            double faturamentoProduto = faturamentoPorProduto.get(nomeProduto)[0];
            produtosMaisVendidos.add(new ItemRelatorioProduto(nomeProduto, quantidade, faturamentoProduto));
        }
        produtosMaisVendidos.sort(Comparator.comparingInt((ItemRelatorioProduto i) -> i.quantidadeVendida).reversed());

        int quantidadeVendas = vendas.size();
        double ticketMedio = quantidadeVendas > 0 ? faturamentoTotal / quantidadeVendas : 0;

        return new ResumoVendas(quantidadeVendas, faturamentoTotal, ticketMedio, produtosMaisVendidos);
    }
}

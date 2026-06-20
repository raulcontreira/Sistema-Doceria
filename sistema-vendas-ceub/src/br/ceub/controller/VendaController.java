package br.ceub.controller;

import java.time.LocalDateTime;
import java.util.List;

import br.ceub.model.Venda;
import br.ceub.service.ItemPedido;
import br.ceub.service.VendaService;

/**
 * Controller do registro de vendas. Repassa as chamadas vindas da tela
 * Swing (TelaVendas) e dos handlers REST (VendaHandler) para o
 * {@link VendaService}.
 */
public class VendaController {

    private final VendaService vendaService;

    public VendaController() {
        this.vendaService = new VendaService();
    }

    public VendaController(VendaService vendaService) {
        this.vendaService = vendaService;
    }

    public Venda registrarVenda(int clienteId, List<ItemPedido> itens) {
        return vendaService.registrarVenda(clienteId, itens);
    }

    public Venda cancelarVenda(int vendaId) {
        return vendaService.cancelarVenda(vendaId);
    }

    public Venda buscarPorId(int id) {
        return vendaService.buscarPorId(id);
    }

    public List<Venda> listarTodos() {
        return vendaService.listarTodos();
    }

    public List<Venda> buscarPorCliente(int clienteId) {
        return vendaService.buscarPorCliente(clienteId);
    }

    public List<Venda> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return vendaService.buscarPorPeriodo(inicio, fim);
    }
}

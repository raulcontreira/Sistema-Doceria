package br.ceub.controller;

import java.util.List;

import br.ceub.model.Cliente;
import br.ceub.service.ClienteService;

/**
 * Controller do CRUD de clientes. Repassa as chamadas vindas da tela
 * Swing (TelaClientes) e dos handlers REST (ClienteHandler) para o
 * {@link ClienteService}, que contem as regras de negocio.
 */
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController() {
        this.clienteService = new ClienteService();
    }

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    public Cliente cadastrar(Cliente cliente) {
        return clienteService.cadastrar(cliente);
    }

    public Cliente buscarPorId(int id) {
        return clienteService.buscarPorId(id);
    }

    public List<Cliente> buscarPorNome(String nome) {
        return clienteService.buscarPorNome(nome);
    }

    public List<Cliente> listarTodos() {
        return clienteService.listarTodos();
    }

    public Cliente atualizar(Cliente cliente) {
        return clienteService.atualizar(cliente);
    }

    public void remover(int id) {
        clienteService.remover(id);
    }
}

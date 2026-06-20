package br.ceub.service;

import java.util.List;

import br.ceub.model.Cliente;
import br.ceub.repository.ClienteRepository;

/**
 * Regras de negocio do cadastro de clientes (CRUD de clientes).
 * Faz validacoes (nome obrigatorio, CPF/e-mail unicos) antes de
 * delegar a persistencia para o {@link ClienteRepository}.
 */
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService() {
        this.clienteRepository = new ClienteRepository();
    }

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente cadastrar(Cliente cliente) {
        validarCamposObrigatorios(cliente);

        if (cliente.getCpf() != null && !cliente.getCpf().trim().isEmpty()
                && clienteRepository.buscarPorCpf(cliente.getCpf()) != null) {
            throw new RegraNegocioException("Ja existe um cliente cadastrado com este CPF");
        }
        if (cliente.getEmail() != null && !cliente.getEmail().trim().isEmpty()
                && clienteRepository.buscarPorEmail(cliente.getEmail()) != null) {
            throw new RegraNegocioException("Ja existe um cliente cadastrado com este e-mail");
        }

        cliente.setId(0); // garante que o repository vai gerar um novo id
        return clienteRepository.salvar(cliente);
    }

    public Cliente buscarPorId(int id) {
        Cliente cliente = clienteRepository.buscarPorId(id);
        if (cliente == null) {
            throw new RegraNegocioException("Cliente nao encontrado (id " + id + ")");
        }
        return cliente;
    }

    public List<Cliente> buscarPorNome(String nome) {
        return clienteRepository.buscarPorNome(nome);
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.listarTodos();
    }

    public Cliente atualizar(Cliente cliente) {
        validarCamposObrigatorios(cliente);

        Cliente existente = clienteRepository.buscarPorId(cliente.getId());
        if (existente == null) {
            throw new RegraNegocioException("Cliente nao encontrado (id " + cliente.getId() + ")");
        }

        Cliente porCpf = clienteRepository.buscarPorCpf(cliente.getCpf());
        if (porCpf != null && porCpf.getId() != cliente.getId()) {
            throw new RegraNegocioException("Ja existe outro cliente com este CPF");
        }

        return clienteRepository.atualizar(cliente);
    }

    public void remover(int id) {
        Cliente existente = clienteRepository.buscarPorId(id);
        if (existente == null) {
            throw new RegraNegocioException("Cliente nao encontrado (id " + id + ")");
        }
        clienteRepository.deletar(id);
    }

    private void validarCamposObrigatorios(Cliente cliente) {
        if (cliente == null) {
            throw new RegraNegocioException("Dados do cliente nao informados");
        }
        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            throw new RegraNegocioException("Nome do cliente e obrigatorio");
        }
    }
}

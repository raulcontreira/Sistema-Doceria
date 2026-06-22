package br.ceub.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.ceub.model.Cliente;
import br.ceub.repository.ClienteRepository;

/**
 * Regras de negocio do cadastro de clientes (CRUD de clientes).
 * Faz validacoes (nome obrigatorio, CPF/e-mail unicos) antes de
 * delegar a persistencia para o {@link ClienteRepository}.
 *
 * A anotacao {@code @Service} registra esta classe como um Bean
 * gerenciado pelo Spring, permitindo que ela seja injetada
 * automaticamente (via construtor) no {@code ClienteController} e no
 * {@code VendaService}.
 */
@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente cadastrar(Cliente cliente) {
        validarCamposObrigatorios(cliente);

        if (cliente.getCpf() != null && !cliente.getCpf().isBlank()
                && clienteRepository.findByCpf(cliente.getCpf()).isPresent()) {
            throw new RegraNegocioException("Ja existe um cliente cadastrado com este CPF");
        }
        if (cliente.getEmail() != null && !cliente.getEmail().isBlank()
                && clienteRepository.findByEmail(cliente.getEmail()).isPresent()) {
            throw new RegraNegocioException("Ja existe um cliente cadastrado com este e-mail");
        }

        cliente.setId(null); // garante INSERT (id novo gerado pelo banco), nunca UPDATE
        return clienteRepository.save(cliente);
    }

    public Cliente buscarPorId(Integer id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Cliente nao encontrado (id " + id + ")"));
    }

    public List<Cliente> buscarPorNome(String nome) {
        return clienteRepository.findByNomeContainingIgnoreCase(nome);
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Cliente atualizar(Cliente cliente) {
        validarCamposObrigatorios(cliente);
        buscarPorId(cliente.getId()); // garante que o cliente existe (senao lanca excecao)

        clienteRepository.findByCpf(cliente.getCpf()).ifPresent(existente -> {
            if (!existente.getId().equals(cliente.getId())) {
                throw new RegraNegocioException("Ja existe outro cliente com este CPF");
            }
        });

        return clienteRepository.save(cliente);
    }

    public void remover(Integer id) {
        buscarPorId(id); // garante que existe antes de tentar remover
        clienteRepository.deleteById(id);
    }

    private void validarCamposObrigatorios(Cliente cliente) {
        if (cliente == null) {
            throw new RegraNegocioException("Dados do cliente nao informados");
        }
        if (cliente.getNome() == null || cliente.getNome().isBlank()) {
            throw new RegraNegocioException("Nome do cliente e obrigatorio");
        }
    }
}

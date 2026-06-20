package br.ceub.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import br.ceub.model.Cliente;
import br.ceub.util.ArquivoPersistenciaUtil;

/**
 * Camada de acesso a dados (Repository) do {@link Cliente}.
 * Base do CRUD de clientes exigido pelo projeto.
 *
 * Os dados sao {@code static} (compartilhados por todas as instancias
 * desta classe no mesmo processo) pelo mesmo motivo explicado em
 * {@link br.ceub.repository.ProdutoRepository}: garantir que a tela
 * Swing, o registro de vendas e a API REST sempre enxerguem o mesmo
 * estado dos clientes em memoria.
 */
public class ClienteRepository {

    private static final String ARQUIVO = "clientes.dat";

    private static final Map<Integer, Cliente> dados = carregarDoDisco();
    private static final AtomicInteger proximoId = new AtomicInteger(calcularProximoId());

    @SuppressWarnings("unchecked")
    private static Map<Integer, Cliente> carregarDoDisco() {
        Map<Integer, Cliente> carregados = ArquivoPersistenciaUtil.carregar(ARQUIVO);
        return carregados != null ? carregados : new LinkedHashMap<>();
    }

    private static int calcularProximoId() {
        int maiorId = 0;
        for (Integer id : dados.keySet()) {
            maiorId = Math.max(maiorId, id);
        }
        return maiorId + 1;
    }

    public Cliente salvar(Cliente cliente) {
        if (cliente.getId() == 0) {
            cliente.setId(proximoId.getAndIncrement());
        }
        dados.put(cliente.getId(), cliente);
        persistir();
        return cliente;
    }

    public Cliente buscarPorId(int id) {
        return dados.get(id);
    }

    public Cliente buscarPorCpf(String cpf) {
        if (cpf == null) {
            return null;
        }
        for (Cliente cliente : dados.values()) {
            if (cpf.equals(cliente.getCpf())) {
                return cliente;
            }
        }
        return null;
    }

    public Cliente buscarPorEmail(String email) {
        if (email == null) {
            return null;
        }
        for (Cliente cliente : dados.values()) {
            if (email.equalsIgnoreCase(cliente.getEmail())) {
                return cliente;
            }
        }
        return null;
    }

    public List<Cliente> buscarPorNome(String nome) {
        List<Cliente> resultado = new ArrayList<>();
        if (nome == null) {
            return resultado;
        }
        String filtro = nome.toLowerCase();
        for (Cliente cliente : dados.values()) {
            if (cliente.getNome() != null && cliente.getNome().toLowerCase().contains(filtro)) {
                resultado.add(cliente);
            }
        }
        return resultado;
    }

    public List<Cliente> listarTodos() {
        return new ArrayList<>(dados.values());
    }

    public Cliente atualizar(Cliente cliente) {
        if (!dados.containsKey(cliente.getId())) {
            return null;
        }
        dados.put(cliente.getId(), cliente);
        persistir();
        return cliente;
    }

    public boolean deletar(int id) {
        boolean removido = dados.remove(id) != null;
        if (removido) {
            persistir();
        }
        return removido;
    }

    private void persistir() {
        ArquivoPersistenciaUtil.salvar(ARQUIVO, dados);
    }
}

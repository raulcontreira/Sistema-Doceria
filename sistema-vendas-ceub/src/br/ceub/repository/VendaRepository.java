package br.ceub.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import br.ceub.model.Venda;
import br.ceub.util.ArquivoPersistenciaUtil;

/**
 * Camada de acesso a dados (Repository) da {@link Venda}.
 * Base do "Registro de Vendas" e dos "Relatorios de Vendas".
 *
 * Os dados sao {@code static} (compartilhados por todas as instancias
 * desta classe no mesmo processo), pelo mesmo motivo explicado em
 * {@link br.ceub.repository.ProdutoRepository}.
 */
public class VendaRepository {

    private static final String ARQUIVO = "vendas.dat";

    private static final Map<Integer, Venda> dados = carregarDoDisco();
    private static final AtomicInteger proximoId = new AtomicInteger(calcularProximoId());

    @SuppressWarnings("unchecked")
    private static Map<Integer, Venda> carregarDoDisco() {
        Map<Integer, Venda> carregados = ArquivoPersistenciaUtil.carregar(ARQUIVO);
        return carregados != null ? carregados : new LinkedHashMap<>();
    }

    private static int calcularProximoId() {
        int maiorId = 0;
        for (Integer id : dados.keySet()) {
            maiorId = Math.max(maiorId, id);
        }
        return maiorId + 1;
    }

    public Venda salvar(Venda venda) {
        if (venda.getId() == 0) {
            venda.setId(proximoId.getAndIncrement());
        }
        dados.put(venda.getId(), venda);
        persistir();
        return venda;
    }

    public Venda buscarPorId(int id) {
        return dados.get(id);
    }

    public List<Venda> listarTodos() {
        return new ArrayList<>(dados.values());
    }

    public List<Venda> buscarPorCliente(int clienteId) {
        List<Venda> resultado = new ArrayList<>();
        for (Venda venda : dados.values()) {
            if (venda.getClienteId() == clienteId) {
                resultado.add(venda);
            }
        }
        return resultado;
    }

    public List<Venda> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        List<Venda> resultado = new ArrayList<>();
        for (Venda venda : dados.values()) {
            LocalDateTime data = venda.getDataHora();
            if (data != null && !data.isBefore(inicio) && !data.isAfter(fim)) {
                resultado.add(venda);
            }
        }
        return resultado;
    }

    public Venda atualizar(Venda venda) {
        if (!dados.containsKey(venda.getId())) {
            return null;
        }
        dados.put(venda.getId(), venda);
        persistir();
        return venda;
    }

    private void persistir() {
        ArquivoPersistenciaUtil.salvar(ARQUIVO, dados);
    }
}

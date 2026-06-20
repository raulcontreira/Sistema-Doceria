package br.ceub.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import br.ceub.model.Produto;
import br.ceub.util.ArquivoPersistenciaUtil;

/**
 * Camada de acesso a dados (Repository) do {@link Produto}.
 * Base do CRUD de produtos e do controle de estoque.
 *
 * IMPORTANTE: os dados ({@code dados}/{@code proximoId}) sao {@code static},
 * isto e, compartilhados por TODAS as instancias desta classe dentro do
 * mesmo processo (mesma execucao do programa). Isso garante que a tela
 * Swing (TelaProdutos), o registro de vendas (VendaService) e a API REST
 * (ProdutoHandler) sempre enxerguem o MESMO estado de estoque em memoria,
 * mesmo criando cada um a sua propria instancia de {@code ProdutoController
 * -> ProdutoService -> ProdutoRepository}. O carregamento do arquivo em
 * disco acontece apenas UMA VEZ, na primeira vez que a classe e usada.
 */
public class ProdutoRepository {

    private static final String ARQUIVO = "produtos.dat";

    private static final Map<Integer, Produto> dados = carregarDoDisco();
    private static final AtomicInteger proximoId = new AtomicInteger(calcularProximoId());

    @SuppressWarnings("unchecked")
    private static Map<Integer, Produto> carregarDoDisco() {
        Map<Integer, Produto> carregados = ArquivoPersistenciaUtil.carregar(ARQUIVO);
        return carregados != null ? carregados : new LinkedHashMap<>();
    }

    private static int calcularProximoId() {
        int maiorId = 0;
        for (Integer id : dados.keySet()) {
            maiorId = Math.max(maiorId, id);
        }
        return maiorId + 1;
    }

    public Produto salvar(Produto produto) {
        if (produto.getId() == 0) {
            produto.setId(proximoId.getAndIncrement());
        }
        dados.put(produto.getId(), produto);
        persistir();
        return produto;
    }

    public Produto buscarPorId(int id) {
        return dados.get(id);
    }

    public List<Produto> buscarPorNome(String nome) {
        List<Produto> resultado = new ArrayList<>();
        if (nome == null) {
            return resultado;
        }
        String filtro = nome.toLowerCase();
        for (Produto produto : dados.values()) {
            if (produto.getNome() != null && produto.getNome().toLowerCase().contains(filtro)) {
                resultado.add(produto);
            }
        }
        return resultado;
    }

    public List<Produto> listarTodos() {
        return new ArrayList<>(dados.values());
    }

    public List<Produto> listarComEstoqueBaixo() {
        List<Produto> resultado = new ArrayList<>();
        for (Produto produto : dados.values()) {
            if (produto.isEstoqueBaixo()) {
                resultado.add(produto);
            }
        }
        return resultado;
    }

    public Produto atualizar(Produto produto) {
        if (!dados.containsKey(produto.getId())) {
            return null;
        }
        dados.put(produto.getId(), produto);
        persistir();
        return produto;
    }

    public boolean deletar(int id) {
        boolean removido = dados.remove(id) != null;
        if (removido) {
            persistir();
        }
        return removido;
    }

    /**
     * Persiste o estado atual do produto (usado apos alteracoes de
     * estoque feitas pelo ProdutoService, sem alterar o id).
     */
    public void persistirAlteracaoDeEstoque(Produto produto) {
        dados.put(produto.getId(), produto);
        persistir();
    }

    private void persistir() {
        ArquivoPersistenciaUtil.salvar(ARQUIVO, dados);
    }
}

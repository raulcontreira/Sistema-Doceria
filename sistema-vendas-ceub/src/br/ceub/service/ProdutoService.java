package br.ceub.service;

import java.util.List;

import br.ceub.model.Produto;
import br.ceub.repository.ProdutoRepository;

/**
 * Regras de negocio do cadastro de produtos e do CONTROLE DE ESTOQUE.
 *
 * O controle de estoque propriamente dito acontece nos metodos
 * {@link #baixarEstoque(int, int)} e {@link #reporEstoque(int, int)},
 * que sao chamados pelo {@link VendaService} quando uma venda e
 * registrada ou cancelada.
 */
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService() {
        this.produtoRepository = new ProdutoRepository();
    }

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public Produto cadastrar(Produto produto) {
        validarCamposObrigatorios(produto);
        produto.setId(0);
        return produtoRepository.salvar(produto);
    }

    public Produto buscarPorId(int id) {
        Produto produto = produtoRepository.buscarPorId(id);
        if (produto == null) {
            throw new RegraNegocioException("Produto nao encontrado (id " + id + ")");
        }
        return produto;
    }

    public List<Produto> buscarPorNome(String nome) {
        return produtoRepository.buscarPorNome(nome);
    }

    public List<Produto> listarTodos() {
        return produtoRepository.listarTodos();
    }

    public List<Produto> listarComEstoqueBaixo() {
        return produtoRepository.listarComEstoqueBaixo();
    }

    public Produto atualizar(Produto produto) {
        validarCamposObrigatorios(produto);
        Produto existente = produtoRepository.buscarPorId(produto.getId());
        if (existente == null) {
            throw new RegraNegocioException("Produto nao encontrado (id " + produto.getId() + ")");
        }
        return produtoRepository.atualizar(produto);
    }

    public void remover(int id) {
        Produto existente = produtoRepository.buscarPorId(id);
        if (existente == null) {
            throw new RegraNegocioException("Produto nao encontrado (id " + id + ")");
        }
        produtoRepository.deletar(id);
    }

    /**
     * Reduz a quantidade em estoque de um produto (usado quando uma
     * venda e registrada). Lanca exececao se o estoque for insuficiente.
     */
    public void baixarEstoque(int produtoId, int quantidade) {
        Produto produto = buscarPorId(produtoId);
        if (produto.getQuantidadeEstoque() < quantidade) {
            throw new RegraNegocioException(
                    "Estoque insuficiente para o produto '" + produto.getNome() + "'. "
                            + "Disponivel: " + produto.getQuantidadeEstoque() + ", solicitado: " + quantidade);
        }
        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidade);
        produtoRepository.persistirAlteracaoDeEstoque(produto);
    }

    /**
     * Devolve unidades ao estoque (usado quando uma venda e cancelada).
     */
    public void reporEstoque(int produtoId, int quantidade) {
        Produto produto = buscarPorId(produtoId);
        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + quantidade);
        produtoRepository.persistirAlteracaoDeEstoque(produto);
    }

    private void validarCamposObrigatorios(Produto produto) {
        if (produto == null) {
            throw new RegraNegocioException("Dados do produto nao informados");
        }
        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new RegraNegocioException("Nome do produto e obrigatorio");
        }
        if (produto.getPreco() < 0) {
            throw new RegraNegocioException("Preco do produto nao pode ser negativo");
        }
        if (produto.getQuantidadeEstoque() < 0) {
            throw new RegraNegocioException("Quantidade em estoque nao pode ser negativa");
        }
    }
}

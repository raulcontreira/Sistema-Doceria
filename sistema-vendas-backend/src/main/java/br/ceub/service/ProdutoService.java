package br.ceub.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.ceub.model.Produto;
import br.ceub.repository.ProdutoRepository;

/**
 * Regras de negocio do cadastro de produtos e do CONTROLE DE ESTOQUE.
 *
 * O controle de estoque propriamente dito acontece nos metodos
 * {@link #baixarEstoque(Integer, int)} e {@link #reporEstoque(Integer, int)},
 * que sao chamados pelo {@link VendaService} quando uma venda e
 * registrada ou cancelada.
 */
@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public Produto cadastrar(Produto produto) {
        validarCamposObrigatorios(produto);
        produto.setId(null);
        return produtoRepository.save(produto);
    }

    public Produto buscarPorId(Integer id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Produto nao encontrado (id " + id + ")"));
    }

    public List<Produto> buscarPorNome(String nome) {
        return produtoRepository.findByNomeContainingIgnoreCase(nome);
    }

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    public List<Produto> listarComEstoqueBaixo() {
        return produtoRepository.buscarComEstoqueBaixo();
    }

    public Produto atualizar(Produto produto) {
        validarCamposObrigatorios(produto);
        buscarPorId(produto.getId()); // garante que existe
        return produtoRepository.save(produto);
    }

    public void remover(Integer id) {
        buscarPorId(id);
        produtoRepository.deleteById(id);
    }

    /**
     * Reduz a quantidade em estoque de um produto (usado quando uma
     * venda e registrada). Lanca exececao se o estoque for insuficiente.
     */
    @Transactional
    public void baixarEstoque(Integer produtoId, int quantidade) {
        Produto produto = buscarPorId(produtoId);
        if (produto.getQuantidadeEstoque() < quantidade) {
            throw new RegraNegocioException(
                    "Estoque insuficiente para o produto '" + produto.getNome() + "'. "
                            + "Disponivel: " + produto.getQuantidadeEstoque() + ", solicitado: " + quantidade);
        }
        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidade);
        produtoRepository.save(produto);
    }

    /**
     * Devolve unidades ao estoque (usado quando uma venda e cancelada).
     */
    @Transactional
    public void reporEstoque(Integer produtoId, int quantidade) {
        Produto produto = buscarPorId(produtoId);
        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + quantidade);
        produtoRepository.save(produto);
    }

    private void validarCamposObrigatorios(Produto produto) {
        if (produto == null) {
            throw new RegraNegocioException("Dados do produto nao informados");
        }
        if (produto.getNome() == null || produto.getNome().isBlank()) {
            throw new RegraNegocioException("Nome do produto e obrigatorio");
        }
        if (produto.getPreco() == null || produto.getPreco() < 0) {
            throw new RegraNegocioException("Preco do produto deve ser informado e nao pode ser negativo");
        }
        if (produto.getQuantidadeEstoque() == null || produto.getQuantidadeEstoque() < 0) {
            throw new RegraNegocioException("Quantidade em estoque deve ser informada e nao pode ser negativa");
        }
        if (produto.getEstoqueMinimo() == null || produto.getEstoqueMinimo() < 0) {
            throw new RegraNegocioException("Estoque minimo deve ser informado e nao pode ser negativo");
        }
    }
}

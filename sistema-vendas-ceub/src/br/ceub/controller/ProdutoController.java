package br.ceub.controller;

import java.util.List;

import br.ceub.model.Produto;
import br.ceub.service.ProdutoService;

/**
 * Controller do CRUD de produtos e controle de estoque. Repassa as
 * chamadas vindas da tela Swing (TelaProdutos) e dos handlers REST
 * (ProdutoHandler) para o {@link ProdutoService}.
 */
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController() {
        this.produtoService = new ProdutoService();
    }

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    public Produto cadastrar(Produto produto) {
        return produtoService.cadastrar(produto);
    }

    public Produto buscarPorId(int id) {
        return produtoService.buscarPorId(id);
    }

    public List<Produto> buscarPorNome(String nome) {
        return produtoService.buscarPorNome(nome);
    }

    public List<Produto> listarTodos() {
        return produtoService.listarTodos();
    }

    public List<Produto> listarComEstoqueBaixo() {
        return produtoService.listarComEstoqueBaixo();
    }

    public Produto atualizar(Produto produto) {
        return produtoService.atualizar(produto);
    }

    public void remover(int id) {
        produtoService.remover(id);
    }

    public void reporEstoque(int produtoId, int quantidade) {
        produtoService.reporEstoque(produtoId, quantidade);
    }
}

package br.ceub.model;

import java.io.Serializable;

/**
 * Representa um produto vendido pela loja. O campo {@code quantidadeEstoque}
 * e a base do CONTROLE DE ESTOQUE: ele é reduzido quando uma venda é
 * registrada e aumentado quando uma venda é cancelada/devolvida.
 */
public class Produto implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String nome;
    private String descricao;
    private String categoria;
    private double preco;
    private int quantidadeEstoque;
    private int estoqueMinimo; // usado para alertar reposicao nos relatorios

    public Produto() {
    }

    public Produto(int id, String nome, String descricao, String categoria,
                    double preco, int quantidadeEstoque, int estoqueMinimo) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.categoria = categoria;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
        this.estoqueMinimo = estoqueMinimo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(int quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public int getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public void setEstoqueMinimo(int estoqueMinimo) {
        this.estoqueMinimo = estoqueMinimo;
    }

    /**
     * @return true se o estoque atual estiver no nivel minimo ou abaixo dele.
     */
    public boolean isEstoqueBaixo() {
        return quantidadeEstoque <= estoqueMinimo;
    }

    @Override
    public String toString() {
        return "Produto{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", categoria='" + categoria + '\'' +
                ", preco=" + preco +
                ", quantidadeEstoque=" + quantidadeEstoque +
                '}';
    }
}

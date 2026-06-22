package br.ceub.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidade JPA que representa um produto da loja. O campo
 * {@code quantidadeEstoque} e a base do CONTROLE DE ESTOQUE.
 *
 * O metodo {@link #isEstoqueBaixo()} e apenas uma regra calculada em
 * memoria (nao vira coluna no banco): como o JPA, nesta classe, usa
 * "acesso por campo" (as anotacoes ficam nos atributos, nao nos
 * getters), o Hibernate ignora metodos que nao correspondem a um campo
 * anotado, entao nao ha necessidade de marcar este metodo como
 * {@code @Transient}.
 */
@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nome;

    private String descricao;

    private String categoria;

    @Column(nullable = false)
    private Double preco;

    @Column(nullable = false)
    private Integer quantidadeEstoque;

    @Column(nullable = false)
    private Integer estoqueMinimo;

    public Produto() {
    }

    public Produto(Integer id, String nome, String descricao, String categoria,
                    Double preco, Integer quantidadeEstoque, Integer estoqueMinimo) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.categoria = categoria;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
        this.estoqueMinimo = estoqueMinimo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public Integer getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(Integer quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public Integer getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public void setEstoqueMinimo(Integer estoqueMinimo) {
        this.estoqueMinimo = estoqueMinimo;
    }

    /**
     * @return true se o estoque atual estiver no nivel minimo ou abaixo dele.
     */
    public boolean isEstoqueBaixo() {
        return quantidadeEstoque != null && estoqueMinimo != null && quantidadeEstoque <= estoqueMinimo;
    }
}

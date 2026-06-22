package br.ceub.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entidade JPA que representa um item (linha) dentro de uma {@link Venda}:
 * um produto, a quantidade vendida e o preco unitario praticado no
 * momento da venda.
 *
 * Guardamos {@code nomeProduto} e {@code precoUnitario} "congelados" no
 * momento da venda (em vez de so referenciar o {@link Produto} pelo id)
 * para que, se o preco do produto mudar depois, o historico de vendas
 * antigo nao seja alterado retroativamente.
 *
 * O campo {@code venda} e o lado "dono" do relacionamento com
 * {@link Venda} (por isso tem a coluna de chave estrangeira
 * {@code venda_id}). Ele e marcado com {@code @JsonIgnore} para evitar
 * um loop infinito na serializacao JSON: sem isso, ao devolver uma
 * Venda via API, o Jackson tentaria serializar
 * venda -> itens -> item.venda -> itens -> ... indefinidamente.
 */
@Entity
@Table(name = "itens_venda")
public class ItemVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "venda_id", nullable = false)
    @JsonIgnore
    private Venda venda;

    @Column(nullable = false)
    private Integer produtoId;

    @Column(nullable = false)
    private String nomeProduto;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false)
    private Double precoUnitario;

    public ItemVenda() {
    }

    public ItemVenda(Integer produtoId, String nomeProduto, Integer quantidade, Double precoUnitario) {
        this.produtoId = produtoId;
        this.nomeProduto = nomeProduto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    public Integer getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Integer produtoId) {
        this.produtoId = produtoId;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Double getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(Double precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    /**
     * @return quantidade * precoUnitario
     */
    public double getSubtotal() {
        return quantidade * precoUnitario;
    }
}

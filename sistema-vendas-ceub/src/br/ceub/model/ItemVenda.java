package br.ceub.model;

import java.io.Serializable;

/**
 * Representa um item (linha) dentro de uma {@link Venda}: um produto,
 * a quantidade vendida e o preco unitario praticado no momento da venda.
 *
 * Guardamos o preco e o nome do produto "congelados" no momento da venda
 * (e nao apenas o id do produto) para que, se o preco do produto mudar
 * depois, o historico de vendas antigo nao seja alterado retroativamente.
 */
public class ItemVenda implements Serializable {

    private static final long serialVersionUID = 1L;

    private int produtoId;
    private String nomeProduto;
    private int quantidade;
    private double precoUnitario;

    public ItemVenda() {
    }

    public ItemVenda(int produtoId, String nomeProduto, int quantidade, double precoUnitario) {
        this.produtoId = produtoId;
        this.nomeProduto = nomeProduto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    public int getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(int produtoId) {
        this.produtoId = produtoId;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public double getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(double precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    /**
     * @return quantidade * precoUnitario
     */
    public double getSubtotal() {
        return quantidade * precoUnitario;
    }

    @Override
    public String toString() {
        return "ItemVenda{" +
                "produtoId=" + produtoId +
                ", nomeProduto='" + nomeProduto + '\'' +
                ", quantidade=" + quantidade +
                ", precoUnitario=" + precoUnitario +
                '}';
    }
}

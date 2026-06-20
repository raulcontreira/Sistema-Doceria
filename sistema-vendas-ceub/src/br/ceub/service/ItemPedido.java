package br.ceub.service;

/**
 * Representa, na hora de REGISTRAR uma venda, o pedido de "quero X
 * unidades do produto Y". E convertido em {@link br.ceub.model.ItemVenda}
 * (com nome e preco "congelados") dentro do {@link VendaService}.
 */
public class ItemPedido {

    private final int produtoId;
    private final int quantidade;

    public ItemPedido(int produtoId, int quantidade) {
        this.produtoId = produtoId;
        this.quantidade = quantidade;
    }

    public int getProdutoId() {
        return produtoId;
    }

    public int getQuantidade() {
        return quantidade;
    }
}

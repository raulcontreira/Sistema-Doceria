package br.ceub.dto;

/**
 * Representa, dentro do corpo de {@code POST /api/vendas}, o pedido de
 * "quero X unidades do produto Y". E convertido em
 * {@link br.ceub.model.ItemVenda} (com nome e preco "congelados")
 * dentro do {@code VendaService}.
 */
public class ItemPedidoRequest {

    private Integer produtoId;
    private Integer quantidade;

    public ItemPedidoRequest() {
    }

    public Integer getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Integer produtoId) {
        this.produtoId = produtoId;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}

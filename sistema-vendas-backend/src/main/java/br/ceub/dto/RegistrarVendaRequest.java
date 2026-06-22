package br.ceub.dto;

import java.util.List;

/**
 * Corpo (JSON) esperado em {@code POST /api/vendas}:
 * <pre>
 * {
 *   "clienteId": 1,
 *   "itens": [
 *     { "produtoId": 1, "quantidade": 2 },
 *     { "produtoId": 3, "quantidade": 1 }
 *   ]
 * }
 * </pre>
 */
public class RegistrarVendaRequest {

    private Integer clienteId;
    private List<ItemPedidoRequest> itens;

    public RegistrarVendaRequest() {
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public List<ItemPedidoRequest> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedidoRequest> itens) {
        this.itens = itens;
    }
}

package br.ceub.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa uma venda registrada no sistema: quem comprou (cliente),
 * quando, quais produtos/quantidades (itens) e o valor total.
 *
 * E a entidade central do REGISTRO DE VENDAS e, indiretamente, dos
 * RELATORIOS DE VENDAS (que sao calculados a partir da lista de vendas).
 */
public class Venda implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Status {
        CONCLUIDA,
        CANCELADA
    }

    private int id;
    private int clienteId;
    private String nomeCliente; // snapshot do nome do cliente no momento da venda
    private LocalDateTime dataHora;
    private List<ItemVenda> itens;
    private double valorTotal;
    private Status status;

    public Venda() {
        this.itens = new ArrayList<>();
        this.status = Status.CONCLUIDA;
    }

    public Venda(int id, int clienteId, String nomeCliente, LocalDateTime dataHora, List<ItemVenda> itens) {
        this.id = id;
        this.clienteId = clienteId;
        this.nomeCliente = nomeCliente;
        this.dataHora = dataHora;
        this.itens = itens != null ? itens : new ArrayList<>();
        this.status = Status.CONCLUIDA;
        recalcularTotal();
    }

    /**
     * Soma o subtotal de cada item e atualiza {@code valorTotal}.
     * Deve ser chamado sempre que a lista de itens for alterada.
     */
    public void recalcularTotal() {
        double total = 0;
        for (ItemVenda item : itens) {
            total += item.getSubtotal();
        }
        this.valorTotal = total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public List<ItemVenda> getItens() {
        return itens;
    }

    public void setItens(List<ItemVenda> itens) {
        this.itens = itens;
        recalcularTotal();
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Venda{" +
                "id=" + id +
                ", clienteId=" + clienteId +
                ", nomeCliente='" + nomeCliente + '\'' +
                ", dataHora=" + dataHora +
                ", valorTotal=" + valorTotal +
                ", status=" + status +
                '}';
    }
}

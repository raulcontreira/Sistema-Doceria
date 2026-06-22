package br.ceub.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

/**
 * Entidade JPA que representa uma venda: quem comprou (cliente),
 * quando, quais produtos/quantidades (itens) e o valor total.
 *
 * E a entidade central do REGISTRO DE VENDAS e, indiretamente, dos
 * RELATORIOS DE VENDAS.
 *
 * Guardamos {@code clienteId} e {@code nomeCliente} como colunas
 * simples (em vez de um relacionamento {@code @ManyToOne} para
 * {@link Cliente}) de proposito: isso mantem o "retrato" do cliente no
 * momento da venda, evita qualquer problema de serializacao JSON
 * circular e deixa explicito, no codigo, que a venda nao depende mais
 * do cadastro do cliente depois de criada.
 *
 * Ja o relacionamento com {@link ItemVenda} e uma composicao real
 * ({@code @OneToMany} com {@code cascade = ALL} e
 * {@code orphanRemoval = true}): os itens nao fazem sentido sem a
 * venda, entao salvar/excluir a venda salva/exclui os itens junto,
 * automaticamente, sem precisar de codigo manual para isso.
 */
@Entity
@Table(name = "vendas")
public class Venda {

    public enum Status {
        CONCLUIDA,
        CANCELADA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer clienteId;

    @Column(nullable = false)
    private String nomeCliente;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "ordem_item")
    private List<ItemVenda> itens = new ArrayList<>();

    @Column(nullable = false)
    private Double valorTotal = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.CONCLUIDA;

    public Venda() {
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
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

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}

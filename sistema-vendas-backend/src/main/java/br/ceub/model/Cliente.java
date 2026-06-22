package br.ceub.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidade JPA que representa um cliente da loja.
 *
 * A anotacao {@code @Entity} diz ao Spring Data JPA/Hibernate que esta
 * classe deve virar uma tabela no banco (aqui, "clientes"), e que cada
 * instancia dela vira uma linha dessa tabela. O Hibernate cria/atualiza
 * essa tabela automaticamente (config {@code spring.jpa.hibernate.ddl-auto=update}
 * em application.properties), entao nao e preciso escrever nenhum SQL
 * de criacao de tabela na mao.
 */
@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nome;

    @Column(unique = true, length = 11)
    private String cpf;

    @Column(unique = true)
    private String email;

    private String telefone;

    /** Construtor vazio exigido pelo JPA/Hibernate (usado internamente ao ler do banco). */
    public Cliente() {
    }

    public Cliente(Integer id, String nome, String cpf, String email, String telefone) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
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

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}

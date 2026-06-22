package br.ceub.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidade JPA que representa um usuario do SISTEMA (funcionario/
 * administrador) que pode fazer login na API. E diferente de
 * {@link Cliente}, que e quem compra os produtos.
 *
 * A senha NUNCA e guardada em texto puro: o campo {@code senhaHash}
 * guarda o resultado de {@code PasswordEncoder.encode(senha)}, gerado
 * pelo {@code BCryptPasswordEncoder} do Spring Security (configurado em
 * {@code br.ceub.security.SecurityConfig}). O BCrypt ja embute um "salt"
 * aleatorio dentro do proprio hash gerado, entao nao e preciso guardar
 * um campo de salt separado (diferente da versao anterior do projeto,
 * que usava SHA-256 manual com salt em coluna separada).
 *
 * {@code @JsonIgnore} no getter de senha evita que o hash da senha seja
 * devolvido em alguma resposta JSON da API por engano.
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

    public enum Perfil {
        ADMIN,
        VENDEDOR
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(nullable = false)
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Perfil perfil;

    public Usuario() {
    }

    public Usuario(Integer id, String nome, String login, String senhaHash, Perfil perfil) {
        this.id = id;
        this.nome = nome;
        this.login = login;
        this.senhaHash = senhaHash;
        this.perfil = perfil;
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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @JsonIgnore
    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }
}

package br.ceub.model;

import java.io.Serializable;

/**
 * Representa um usuario do SISTEMA (funcionario/administrador) que pode
 * fazer login na aplicacao (tanto na tela Swing quanto na API REST).
 *
 * Importante: este "Usuario" e diferente de "Cliente". Usuario é quem
 * opera o sistema (vendedor, gerente). Cliente é quem compra os produtos.
 */
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Perfil {
        ADMIN,
        VENDEDOR
    }

    private int id;
    private String nome;
    private String login;
    private String senhaHash; // nunca guardamos a senha em texto puro
    private String salt;      // valor aleatorio usado para reforcar o hash da senha
    private Perfil perfil;

    public Usuario() {
    }

    public Usuario(int id, String nome, String login, String senhaHash, String salt, Perfil perfil) {
        this.id = id;
        this.nome = nome;
        this.login = login;
        this.senhaHash = senhaHash;
        this.salt = salt;
        this.perfil = perfil;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", login='" + login + '\'' +
                ", perfil=" + perfil +
                '}';
    }
}

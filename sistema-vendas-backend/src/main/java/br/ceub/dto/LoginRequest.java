package br.ceub.dto;

/**
 * Corpo (JSON) esperado em {@code POST /api/auth/login}:
 * {@code { "login": "...", "senha": "..." }}.
 */
public class LoginRequest {

    private String login;
    private String senha;

    public LoginRequest() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}

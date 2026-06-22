package br.ceub.dto;

/**
 * Corpo (JSON) devolvido por {@code POST /api/auth/login} em caso de
 * sucesso: {@code { "token": "...", "nome": "...", "perfil": "ADMIN" }}.
 */
public class LoginResponse {

    private String token;
    private String nome;
    private String perfil;

    public LoginResponse(String token, String nome, String perfil) {
        this.token = token;
        this.nome = nome;
        this.perfil = perfil;
    }

    public String getToken() {
        return token;
    }

    public String getNome() {
        return nome;
    }

    public String getPerfil() {
        return perfil;
    }
}

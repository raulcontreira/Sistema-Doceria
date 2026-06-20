package br.ceub.controller;

import br.ceub.security.JwtUtil;
import br.ceub.service.AutenticacaoService;

/**
 * Controller de autenticacao. E a "porta de entrada" tanto para a tela
 * de login (Swing) quanto para o endpoint REST {@code POST /api/auth/login}.
 *
 * Um Controller, nesta arquitetura, nao tem regra de negocio: ele apenas
 * recebe a chamada (de uma tela ou de um handler HTTP), delega para o
 * Service correspondente e devolve o resultado.
 */
public class AuthController {

    private final AutenticacaoService autenticacaoService;

    public AuthController() {
        this.autenticacaoService = new AutenticacaoService();
    }

    public AuthController(AutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    public AutenticacaoService.LoginResultado login(String login, String senha) {
        return autenticacaoService.login(login, senha);
    }

    public JwtUtil.TokenInfo validarToken(String token) {
        return autenticacaoService.validarToken(token);
    }
}

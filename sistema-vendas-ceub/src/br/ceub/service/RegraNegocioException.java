package br.ceub.service;

/**
 * Exececao lancada quando uma regra de negocio e violada
 * (ex.: estoque insuficiente, CPF duplicado, credenciais invalidas).
 *
 * Centralizar isso em um unico tipo de exececao facilita o tratamento
 * tanto na tela Swing (mostra um JOptionPane com a mensagem) quanto na
 * API REST (responde HTTP 400 com a mensagem em JSON).
 */
public class RegraNegocioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}

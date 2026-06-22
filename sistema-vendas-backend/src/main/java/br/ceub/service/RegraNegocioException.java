package br.ceub.service;

/**
 * Exececao lancada quando uma regra de negocio e violada
 * (ex.: estoque insuficiente, CPF duplicado, registro nao encontrado).
 *
 * Centralizar isso em um unico tipo de exececao permite que o
 * {@code GlobalExceptionHandler} (pacote {@code br.ceub.exception})
 * trate todas elas da mesma forma, devolvendo HTTP 400 com a mensagem
 * em JSON, sem precisar de tratamento especial em cada Controller.
 */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}

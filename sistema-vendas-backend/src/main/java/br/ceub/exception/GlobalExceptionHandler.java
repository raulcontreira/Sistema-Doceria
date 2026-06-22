package br.ceub.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.ceub.service.RegraNegocioException;

/**
 * Tratamento centralizado de excecoes para toda a API.
 *
 * Com {@code @RestControllerAdvice}, NENHUM controller precisa de
 * try/catch: se um {@code Service} lancar {@link RegraNegocioException}
 * em qualquer lugar (estoque insuficiente, CPF duplicado, registro nao
 * encontrado, etc.), o Spring intercepta automaticamente aqui e
 * devolve uma resposta HTTP padronizada, em JSON, no formato
 * {@code {"erro": "mensagem"}} — exatamente o mesmo contrato de erro
 * que a versao anterior do projeto (sem Spring) ja usava.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<Map<String, String>> tratarRegraNegocio(RegraNegocioException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpoDeErro(e.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> tratarAutenticacao(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(corpoDeErro("Usuario ou senha invalidos"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> tratarErroGenerico(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(corpoDeErro("Erro interno: " + e.getMessage()));
    }

    private Map<String, String> corpoDeErro(String mensagem) {
        Map<String, String> corpo = new LinkedHashMap<>();
        corpo.put("erro", mensagem);
        return corpo;
    }
}

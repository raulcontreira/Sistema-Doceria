package br.ceub.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Componente Spring responsavel por gerar e validar tokens JWT
 * (formato {@code header.payload.assinatura}, em Base64URL, assinados
 * com HMAC-SHA256 — padrao HS256).
 *
 * Diferente da versao anterior do projeto (Java puro, sem Spring), aqui
 * o segredo ({@code jwt.secret}) e a validade do token
 * ({@code jwt.expiration-ms}) NAO ficam fixos no codigo: eles vem do
 * arquivo {@code application.properties} atraves da anotacao
 * {@code @Value}, que e exatamente o motivo de existir esse arquivo de
 * configuracao em um projeto Spring Boot.
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String chaveSecreta;

    @Value("${jwt.expiration-ms}")
    private long validadeTokenMs;

    private static final String ALGORITMO_HMAC = "HmacSHA256";
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Gera um token JWT assinado contendo o login, o nome e o perfil do
     * usuario autenticado, alem da data de emissao (iat) e expiracao (exp).
     */
    public String gerarToken(String login, String nome, String perfil) {
        long agora = System.currentTimeMillis();
        long expira = agora + validadeTokenMs;

        Map<String, Object> header = new LinkedHashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", login);
        payload.put("nome", nome);
        payload.put("perfil", perfil);
        payload.put("iat", agora);
        payload.put("exp", expira);

        String headerCodificado = base64UrlEncode(escreverJson(header));
        String payloadCodificado = base64UrlEncode(escreverJson(payload));
        String dadosParaAssinar = headerCodificado + "." + payloadCodificado;
        String assinatura = base64UrlEncode(calcularHmac(dadosParaAssinar));

        return dadosParaAssinar + "." + assinatura;
    }

    /**
     * Resultado da validacao de um token: se e valido e, em caso positivo,
     * quais sao as informacoes (claims) nele contidas.
     */
    public static class TokenInfo {
        public final boolean valido;
        public final String motivoFalha;
        public final String login;
        public final String nome;
        public final String perfil;

        private TokenInfo(boolean valido, String motivoFalha, String login, String nome, String perfil) {
            this.valido = valido;
            this.motivoFalha = motivoFalha;
            this.login = login;
            this.nome = nome;
            this.perfil = perfil;
        }

        static TokenInfo falha(String motivo) {
            return new TokenInfo(false, motivo, null, null, null);
        }

        static TokenInfo sucesso(String login, String nome, String perfil) {
            return new TokenInfo(true, null, login, nome, perfil);
        }
    }

    /**
     * Valida a estrutura, a assinatura e a expiracao de um token JWT.
     * E este metodo que protege a API: o {@code JwtAuthenticationFilter}
     * chama ele antes de deixar qualquer requisicao passar.
     */
    @SuppressWarnings("unchecked")
    public TokenInfo validarToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return TokenInfo.falha("Token nao informado");
        }

        String[] partes = token.split("\\.");
        if (partes.length != 3) {
            return TokenInfo.falha("Formato de token invalido");
        }

        String dadosAssinados = partes[0] + "." + partes[1];
        String assinaturaEsperada = base64UrlEncode(calcularHmac(dadosAssinados));

        if (!assinaturaEsperada.equals(partes[2])) {
            return TokenInfo.falha("Assinatura invalida (token adulterado ou chave incorreta)");
        }

        Map<String, Object> payload;
        try {
            payload = objectMapper.readValue(base64UrlDecode(partes[1]), Map.class);
        } catch (Exception e) {
            return TokenInfo.falha("Payload do token corrompido");
        }

        Object expObj = payload.get("exp");
        long exp = expObj instanceof Number ? ((Number) expObj).longValue() : 0L;
        if (System.currentTimeMillis() > exp) {
            return TokenInfo.falha("Token expirado");
        }

        String login = (String) payload.get("sub");
        String nome = (String) payload.get("nome");
        String perfil = (String) payload.get("perfil");
        return TokenInfo.sucesso(login, nome, perfil);
    }

    // ---------------------------------------------------------------
    // metodos auxiliares de criptografia/codificacao
    // ---------------------------------------------------------------

    private String escreverJson(Map<String, Object> dados) {
        try {
            return objectMapper.writeValueAsString(dados);
        } catch (Exception e) {
            throw new IllegalStateException("Erro ao gerar JSON do token JWT", e);
        }
    }

    private byte[] calcularHmac(String dados) {
        try {
            Mac mac = Mac.getInstance(ALGORITMO_HMAC);
            SecretKeySpec chave = new SecretKeySpec(
                    chaveSecreta.getBytes(StandardCharsets.UTF_8), ALGORITMO_HMAC);
            mac.init(chave);
            return mac.doFinal(dados.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("Erro ao calcular assinatura HMAC do token", e);
        }
    }

    private String base64UrlEncode(String texto) {
        return base64UrlEncode(texto.getBytes(StandardCharsets.UTF_8));
    }

    private String base64UrlEncode(byte[] dados) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(dados);
    }

    private String base64UrlDecode(String texto) {
        byte[] dados = Base64.getUrlDecoder().decode(texto);
        return new String(dados, StandardCharsets.UTF_8);
    }
}

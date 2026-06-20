package br.ceub.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import br.ceub.util.JsonUtil;

/**
 * Implementacao manual e didatica do padrao JWT (JSON Web Token), no
 * formato HS256: {@code header.payload.assinatura}, todos em Base64URL.
 *
 * Por que implementar "na mao" em vez de usar uma biblioteca como a
 * io.jsonwebtoken (JJWT)? Porque este ambiente de desenvolvimento nao tem
 * acesso a um gerenciador de dependencias (Maven/Gradle) nem a internet
 * para baixar .jar's externos. Para que o requisito "Autenticacao com JWT"
 * seja cumprido de forma 100% funcional e compilavel apenas com o JDK,
 * implementamos aqui o algoritmo HMAC-SHA256 (classe javax.crypto.Mac, que
 * já vem com o Java) seguindo exatamente a especificacao do JWT (RFC 7519):
 *
 *   token = base64url(header) + "." + base64url(payload) + "." + base64url(assinatura)
 *   assinatura = HMAC-SHA256(base64url(header) + "." + base64url(payload), chaveSecreta)
 *
 * Caso o projeto seja migrado para um ambiente com Maven/Gradle, esta
 * classe pode ser substituida por uma biblioteca JWT consolidada sem que
 * o restante do sistema (services, controllers, telas) precise mudar,
 * pois todos eles dependem apenas dos metodos publicos abaixo.
 */
public final class JwtUtil {

    // Em um sistema real, esta chave NUNCA deveria ficar fixa no codigo-fonte:
    // o ideal e le-la de uma variavel de ambiente ou de um arquivo de configuracao
    // fora do controle de versao. Aqui ela e fixa apenas para fins didaticos.
    private static final String CHAVE_SECRETA = "ceub-sistema-vendas-chave-secreta-2026";
    private static final String ALGORITMO_HMAC = "HmacSHA256";
    private static final long VALIDADE_TOKEN_MS = 1000L * 60 * 60 * 8; // 8 horas

    private JwtUtil() {
    }

    /**
     * Gera um token JWT assinado contendo o login, o nome e o perfil do
     * usuario autenticado, alem da data de emissao (iat) e expiracao (exp).
     */
    public static String gerarToken(String login, String nome, String perfil) {
        long agora = System.currentTimeMillis();
        long expira = agora + VALIDADE_TOKEN_MS;

        Map<String, Object> header = new LinkedHashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", login);
        payload.put("nome", nome);
        payload.put("perfil", perfil);
        payload.put("iat", agora);
        payload.put("exp", expira);

        String headerCodificado = base64UrlEncode(JsonUtil.toJson(header));
        String payloadCodificado = base64UrlEncode(JsonUtil.toJson(payload));
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
     * E este metodo que protege a API (requisito "API protegida"):
     * toda rota protegida chama este metodo antes de atender a requisicao.
     */
    public static TokenInfo validarToken(String token) {
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
            payload = JsonUtil.parseObjeto(base64UrlDecode(partes[1]));
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

    private static byte[] calcularHmac(String dados) {
        try {
            Mac mac = Mac.getInstance(ALGORITMO_HMAC);
            SecretKeySpec chave = new SecretKeySpec(
                    CHAVE_SECRETA.getBytes(StandardCharsets.UTF_8), ALGORITMO_HMAC);
            mac.init(chave);
            return mac.doFinal(dados.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao calcular assinatura HMAC do token", e);
        }
    }

    private static String base64UrlEncode(String texto) {
        return base64UrlEncode(texto.getBytes(StandardCharsets.UTF_8));
    }

    private static String base64UrlEncode(byte[] dados) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(dados);
    }

    private static String base64UrlDecode(String texto) {
        byte[] dados = Base64.getUrlDecoder().decode(texto);
        return new String(dados, StandardCharsets.UTF_8);
    }
}

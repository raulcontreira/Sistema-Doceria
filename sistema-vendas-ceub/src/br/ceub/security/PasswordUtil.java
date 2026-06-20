package br.ceub.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Responsavel por gerar e validar o hash das senhas dos usuarios.
 *
 * Nunca guardamos a senha em texto puro: cada senha recebe um "salt"
 * aleatorio e e transformada em hash (SHA-256). Ao fazer login, refazemos
 * o mesmo calculo com o salt salvo e comparamos os hashes.
 *
 * Observacao didatica: esta classe usa apenas {@code java.security}, que
 * faz parte do JDK padrao, exatamente para que o projeto compile e rode
 * sem depender de bibliotecas externas (sem Maven/Gradle).
 */
public final class PasswordUtil {

    private static final String ALGORITMO = "SHA-256";
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {
        // classe utilitaria: nao deve ser instanciada
    }

    /**
     * Gera um salt aleatorio novo, codificado em Base64.
     */
    public static String gerarSalt() {
        byte[] saltBytes = new byte[16];
        RANDOM.nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    /**
     * Calcula o hash (SHA-256) da senha combinada com o salt informado.
     */
    public static String gerarHash(String senhaTextoPuro, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITMO);
            digest.update(Base64.getDecoder().decode(salt));
            byte[] hashBytes = digest.digest(senhaTextoPuro.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            throw new RuntimeException("Erro ao gerar hash da senha", e);
        }
    }

    /**
     * Verifica se a senha em texto puro informada corresponde ao hash/salt
     * armazenados para o usuario.
     */
    public static boolean verificar(String senhaTextoPuro, String salt, String hashEsperado) {
        String hashCalculado = gerarHash(senhaTextoPuro, salt);
        return hashCalculado.equals(hashEsperado);
    }
}

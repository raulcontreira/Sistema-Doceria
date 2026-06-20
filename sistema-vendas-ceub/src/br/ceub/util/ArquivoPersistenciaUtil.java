package br.ceub.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

/**
 * Utilitario genérico para salvar e carregar dados em disco usando a
 * serializacao nativa do Java (java.io.Serializable).
 *
 * Isso permite que o sistema seja "totalmente funcional" mesmo sem um
 * banco de dados externo (MySQL/PostgreSQL): os dados cadastrados pelo
 * usuario (clientes, produtos, vendas, usuarios do sistema) sao
 * persistidos em arquivos dentro da pasta {@code dados/} e recarregados
 * automaticamente na proxima vez que o sistema for iniciado.
 *
 * Em uma evolucao futura do projeto, esta classe poderia ser substituida
 * por uma camada JDBC/JPA conectada a um banco real, sem que as camadas
 * de service/controller/view precisassem ser alteradas (pois elas so
 * conhecem os Repositories, nao esta classe).
 */
public final class ArquivoPersistenciaUtil {

    private static final String PASTA_DADOS = "dados";

    private ArquivoPersistenciaUtil() {
    }

    /**
     * Salva (sobrescreve) um mapa de dados em um arquivo binario.
     */
    @SuppressWarnings("rawtypes")
    public static void salvar(String nomeArquivo, Map dados) {
        garantirPastaDados();
        File arquivo = new File(PASTA_DADOS, nomeArquivo);
        try (FileOutputStream fos = new FileOutputStream(arquivo);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(dados);
        } catch (IOException e) {
            System.err.println("Nao foi possivel salvar o arquivo " + nomeArquivo + ": " + e.getMessage());
        }
    }

    /**
     * Carrega um mapa de dados previamente salvo. Se o arquivo nao existir
     * ainda (primeira execucao do sistema), retorna null.
     */
    @SuppressWarnings("rawtypes")
    public static Map carregar(String nomeArquivo) {
        File arquivo = new File(PASTA_DADOS, nomeArquivo);
        if (!arquivo.exists()) {
            return null;
        }
        try (FileInputStream fis = new FileInputStream(arquivo);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (Map) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Nao foi possivel carregar o arquivo " + nomeArquivo + ": " + e.getMessage());
            return null;
        }
    }

    private static void garantirPastaDados() {
        File pasta = new File(PASTA_DADOS);
        if (!pasta.exists()) {
            pasta.mkdirs();
        }
    }
}

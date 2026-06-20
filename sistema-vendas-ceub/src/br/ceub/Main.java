package br.ceub;

import javax.swing.SwingUtilities;

import br.ceub.api.ApiServer;
import br.ceub.controller.ClienteController;
import br.ceub.controller.ProdutoController;
import br.ceub.model.Cliente;
import br.ceub.model.Produto;
import br.ceub.view.TelaLogin;

/**
 * Ponto de entrada (entry point) do Sistema de Vendas.
 *
 * Ao iniciar, este programa faz duas coisas:
 * <ol>
 *   <li>Sobe a API REST (porta 8080) em uma thread separada, protegida
 *       por JWT, para que sistemas externos (ou ferramentas como Postman/
 *       Insomnia) possam consumir os mesmos dados;</li>
 *   <li>Abre a interface grafica Swing (tela de login), que roda na
 *       "Event Dispatch Thread" (EDT), como recomenda a documentacao
 *       oficial do Swing.</li>
 * </ol>
 *
 * Tanto a API quanto a tela Swing usam os MESMOS Controllers/Services/
 * Repositories (Arquitetura em Camadas) e os MESMOS arquivos de dados
 * em disco (pasta {@code dados/}), entao um cadastro feito pela tela
 * aparece imediatamente para quem consultar a API, e vice-versa.
 */
public class Main {

    private static final int PORTA_API = 8080;

    public static void main(String[] args) {

        popularDadosDeExemploSeNecessario();

        iniciarApiRest();

        SwingUtilities.invokeLater(() -> new TelaLogin().setVisible(true));
    }

    /**
     * Sobe o servidor HTTP da API REST em uma thread separada, para nao
     * bloquear a abertura da interface Swing.
     */
    private static void iniciarApiRest() {
        Thread threadApi = new Thread(() -> {
            try {
                new ApiServer(PORTA_API).iniciar();
            } catch (Exception e) {
                System.err.println("Nao foi possivel iniciar a API REST: " + e.getMessage());
            }
        });
        threadApi.setDaemon(true);
        threadApi.setName("api-rest-thread");
        threadApi.start();
    }

    /**
     * Cadastra alguns clientes e produtos de exemplo na PRIMEIRA execucao
     * do sistema (quando os arquivos de dados ainda nao existem), apenas
     * para facilitar os testes da tela de vendas e da API.
     */
    private static void popularDadosDeExemploSeNecessario() {
        ClienteController clienteController = new ClienteController();
        ProdutoController produtoController = new ProdutoController();

        if (clienteController.listarTodos().isEmpty()) {
            clienteController.cadastrar(new Cliente(0, "Maria Silva", "11111111111", "maria@email.com", "61999990001"));
            clienteController.cadastrar(new Cliente(0, "Joao Souza", "22222222222", "joao@email.com", "61999990002"));
        }

        if (produtoController.listarTodos().isEmpty()) {
            produtoController.cadastrar(new Produto(0, "Caneta Esferografica", "Caneta azul", "Papelaria", 2.50, 100, 10));
            produtoController.cadastrar(new Produto(0, "Caderno Universitario", "200 folhas", "Papelaria", 25.90, 30, 5));
            produtoController.cadastrar(new Produto(0, "Mochila Escolar", "Mochila reforcada", "Acessorios", 89.90, 8, 3));
        }
    }
}

package br.ceub.api;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

import br.ceub.api.handlers.AuthHandler;
import br.ceub.api.handlers.ClienteHandler;
import br.ceub.api.handlers.ProdutoHandler;
import br.ceub.api.handlers.RelatorioHandler;
import br.ceub.api.handlers.VendaHandler;

/**
 * Ponto de entrada da API REST do sistema.
 *
 * Usa {@link com.sun.net.httpserver.HttpServer}, classe que ja vem
 * dentro do JDK (modulo {@code jdk.httpserver}), para que a API REST
 * funcione "de verdade" via HTTP sem exigir nenhum framework externo
 * (Spring, Javalin, etc) nem acesso a internet para baixar dependencias.
 *
 * Rotas registradas:
 * <ul>
 *   <li>POST /api/auth/login        -> publica (gera o token JWT)</li>
 *   <li>/api/clientes/*             -> protegida por JWT</li>
 *   <li>/api/produtos/*             -> protegida por JWT</li>
 *   <li>/api/vendas/*               -> protegida por JWT</li>
 *   <li>/api/relatorios/vendas      -> protegida por JWT</li>
 * </ul>
 */
public class ApiServer {

    private final int porta;
    private HttpServer servidor;

    public ApiServer(int porta) {
        this.porta = porta;
    }

    /**
     * Cria o servidor, registra as rotas e comeca a aceitar conexoes.
     */
    public void iniciar() throws IOException {
        servidor = HttpServer.create(new InetSocketAddress(porta), 0);

        servidor.createContext("/api/auth/login", new AuthHandler());
        servidor.createContext("/api/clientes", new ClienteHandler());
        servidor.createContext("/api/produtos", new ProdutoHandler());
        servidor.createContext("/api/vendas", new VendaHandler());
        servidor.createContext("/api/relatorios/vendas", new RelatorioHandler());

        servidor.setExecutor(Executors.newFixedThreadPool(8));
        servidor.start();

        System.out.println("API REST iniciada em http://localhost:" + porta);
        System.out.println("  POST   /api/auth/login            (publica)");
        System.out.println("  GET    /api/clientes               (JWT)");
        System.out.println("  POST   /api/clientes               (JWT)");
        System.out.println("  GET    /api/produtos                (JWT)");
        System.out.println("  POST   /api/produtos                (JWT)");
        System.out.println("  GET    /api/vendas                  (JWT)");
        System.out.println("  POST   /api/vendas                  (JWT)");
        System.out.println("  GET    /api/relatorios/vendas       (JWT)");
    }

    public void parar() {
        if (servidor != null) {
            servidor.stop(0);
        }
    }
}

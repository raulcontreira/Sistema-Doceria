package br.ceub.api.handlers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import br.ceub.api.HandlerProtegido;
import br.ceub.api.HttpResponder;
import br.ceub.controller.ClienteController;
import br.ceub.model.Cliente;
import br.ceub.security.JwtUtil;
import br.ceub.util.JsonUtil;

/**
 * Handler REST do CRUD de clientes, protegido por JWT (so atende
 * requisicoes que ja passaram pela validacao em {@link HandlerProtegido}).
 *
 * Rotas atendidas (todas sob {@code /api/clientes}):
 * <ul>
 *   <li>GET    /api/clientes        -> lista todos os clientes</li>
 *   <li>GET    /api/clientes/{id}   -> busca um cliente pelo id</li>
 *   <li>POST   /api/clientes        -> cadastra um novo cliente</li>
 *   <li>PUT    /api/clientes/{id}   -> atualiza um cliente existente</li>
 *   <li>DELETE /api/clientes/{id}   -> remove um cliente</li>
 * </ul>
 */
public class ClienteHandler extends HandlerProtegido {

    private static final String PREFIXO_ROTA = "/api/clientes";
    private final ClienteController clienteController = new ClienteController();

    @Override
    protected void tratar(HttpExchange exchange, JwtUtil.TokenInfo usuarioAutenticado) throws IOException {
        String metodo = exchange.getRequestMethod();
        Integer id = extrairIdDaUrl(exchange.getRequestURI().getPath());

        if ("GET".equalsIgnoreCase(metodo) && id == null) {
            List<Cliente> clientes = clienteController.listarTodos();
            HttpResponder.enviarJson(exchange, 200, clientes);

        } else if ("GET".equalsIgnoreCase(metodo)) {
            Cliente cliente = clienteController.buscarPorId(id);
            HttpResponder.enviarJson(exchange, 200, cliente);

        } else if ("POST".equalsIgnoreCase(metodo)) {
            Cliente cliente = converterParaCliente(HttpResponder.lerCorpo(exchange), 0);
            Cliente salvo = clienteController.cadastrar(cliente);
            HttpResponder.enviarJson(exchange, 201, salvo);

        } else if ("PUT".equalsIgnoreCase(metodo) && id != null) {
            Cliente cliente = converterParaCliente(HttpResponder.lerCorpo(exchange), id);
            Cliente atualizado = clienteController.atualizar(cliente);
            HttpResponder.enviarJson(exchange, 200, atualizado);

        } else if ("DELETE".equalsIgnoreCase(metodo) && id != null) {
            clienteController.remover(id);
            HttpResponder.enviarJson(exchange, 200, Map.of("mensagem", "Cliente removido com sucesso"));

        } else {
            HttpResponder.enviarErro(exchange, 405, "Metodo/rota nao suportado para /api/clientes");
        }
    }

    private Cliente converterParaCliente(String json, int idForcado) {
        Map<String, Object> dados = JsonUtil.parseObjeto(json);
        Cliente cliente = new Cliente();
        cliente.setId(idForcado);
        cliente.setNome((String) dados.get("nome"));
        cliente.setCpf((String) dados.get("cpf"));
        cliente.setEmail((String) dados.get("email"));
        cliente.setTelefone((String) dados.get("telefone"));
        return cliente;
    }

    private Integer extrairIdDaUrl(String caminho) {
        String resto = caminho.substring(PREFIXO_ROTA.length());
        resto = resto.replaceAll("^/+", "").trim();
        if (resto.isEmpty()) {
            return null;
        }
        return Integer.parseInt(resto);
    }
}

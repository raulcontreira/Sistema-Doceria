package br.ceub.api.handlers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import br.ceub.api.HandlerProtegido;
import br.ceub.api.HttpResponder;
import br.ceub.controller.ProdutoController;
import br.ceub.model.Produto;
import br.ceub.security.JwtUtil;
import br.ceub.util.JsonUtil;

/**
 * Handler REST do CRUD de produtos e consulta de estoque, protegido por JWT.
 *
 * Rotas atendidas (todas sob {@code /api/produtos}):
 * <ul>
 *   <li>GET    /api/produtos              -> lista todos os produtos</li>
 *   <li>GET    /api/produtos/{id}         -> busca um produto pelo id</li>
 *   <li>GET    /api/produtos/estoque-baixo -> lista produtos com estoque no minimo ou abaixo dele</li>
 *   <li>POST   /api/produtos              -> cadastra um novo produto</li>
 *   <li>PUT    /api/produtos/{id}         -> atualiza um produto existente</li>
 *   <li>DELETE /api/produtos/{id}         -> remove um produto</li>
 * </ul>
 */
public class ProdutoHandler extends HandlerProtegido {

    private static final String PREFIXO_ROTA = "/api/produtos";
    private final ProdutoController produtoController = new ProdutoController();

    @Override
    protected void tratar(HttpExchange exchange, JwtUtil.TokenInfo usuarioAutenticado) throws IOException {
        String metodo = exchange.getRequestMethod();
        String caminhoRestante = extrairCaminhoRestante(exchange.getRequestURI().getPath());

        if ("GET".equalsIgnoreCase(metodo) && caminhoRestante.isEmpty()) {
            List<Produto> produtos = produtoController.listarTodos();
            HttpResponder.enviarJson(exchange, 200, produtos);

        } else if ("GET".equalsIgnoreCase(metodo) && caminhoRestante.equals("estoque-baixo")) {
            List<Produto> produtos = produtoController.listarComEstoqueBaixo();
            HttpResponder.enviarJson(exchange, 200, produtos);

        } else if ("GET".equalsIgnoreCase(metodo)) {
            int id = Integer.parseInt(caminhoRestante);
            Produto produto = produtoController.buscarPorId(id);
            HttpResponder.enviarJson(exchange, 200, produto);

        } else if ("POST".equalsIgnoreCase(metodo)) {
            Produto produto = converterParaProduto(HttpResponder.lerCorpo(exchange), 0);
            Produto salvo = produtoController.cadastrar(produto);
            HttpResponder.enviarJson(exchange, 201, salvo);

        } else if ("PUT".equalsIgnoreCase(metodo) && !caminhoRestante.isEmpty()) {
            int id = Integer.parseInt(caminhoRestante);
            Produto produto = converterParaProduto(HttpResponder.lerCorpo(exchange), id);
            Produto atualizado = produtoController.atualizar(produto);
            HttpResponder.enviarJson(exchange, 200, atualizado);

        } else if ("DELETE".equalsIgnoreCase(metodo) && !caminhoRestante.isEmpty()) {
            int id = Integer.parseInt(caminhoRestante);
            produtoController.remover(id);
            HttpResponder.enviarJson(exchange, 200, Map.of("mensagem", "Produto removido com sucesso"));

        } else {
            HttpResponder.enviarErro(exchange, 405, "Metodo/rota nao suportado para /api/produtos");
        }
    }

    private Produto converterParaProduto(String json, int idForcado) {
        Map<String, Object> dados = JsonUtil.parseObjeto(json);
        Produto produto = new Produto();
        produto.setId(idForcado);
        produto.setNome((String) dados.get("nome"));
        produto.setDescricao((String) dados.get("descricao"));
        produto.setCategoria((String) dados.get("categoria"));
        produto.setPreco(numero(dados.get("preco")));
        produto.setQuantidadeEstoque((int) numero(dados.get("quantidadeEstoque")));
        produto.setEstoqueMinimo((int) numero(dados.get("estoqueMinimo")));
        return produto;
    }

    private double numero(Object valor) {
        if (valor == null) {
            return 0;
        }
        return ((Number) valor).doubleValue();
    }

    private String extrairCaminhoRestante(String caminho) {
        String resto = caminho.substring(PREFIXO_ROTA.length());
        return resto.replaceAll("^/+", "").trim();
    }
}

package br.ceub.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.ceub.model.Produto;
import br.ceub.service.ProdutoService;

/**
 * Controller REST do CRUD de produtos e consulta de estoque.
 *
 * Rotas:
 * <ul>
 *   <li>GET    /api/produtos                -> lista todos (ou filtra por ?nome=)</li>
 *   <li>GET    /api/produtos/estoque-baixo  -> lista produtos com estoque no minimo ou abaixo dele</li>
 *   <li>GET    /api/produtos/{id}           -> busca por id</li>
 *   <li>POST   /api/produtos                -> cadastra</li>
 *   <li>PUT    /api/produtos/{id}           -> atualiza</li>
 *   <li>DELETE /api/produtos/{id}           -> remove</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public List<Produto> listar(@RequestParam(required = false) String nome) {
        if (nome != null && !nome.isBlank()) {
            return produtoService.buscarPorNome(nome);
        }
        return produtoService.listarTodos();
    }

    // Nota: o Spring MVC resolve rotas por especificidade (um segmento
    // literal como "estoque-baixo" sempre "ganha" de um padrao com
    // variavel como "{id}"), entao a ordem de declaracao dos metodos
    // abaixo nao influencia o roteamento — mas mante-los proximos ajuda
    // na leitura.
    @GetMapping("/estoque-baixo")
    public List<Produto> listarComEstoqueBaixo() {
        return produtoService.listarComEstoqueBaixo();
    }

    @GetMapping("/{id}")
    public Produto buscarPorId(@PathVariable Integer id) {
        return produtoService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<Produto> cadastrar(@RequestBody Produto produto) {
        Produto salvo = produtoService.cadastrar(produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PutMapping("/{id}")
    public Produto atualizar(@PathVariable Integer id, @RequestBody Produto produto) {
        produto.setId(id);
        return produtoService.atualizar(produto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> remover(@PathVariable Integer id) {
        produtoService.remover(id);
        return ResponseEntity.ok(Map.of("mensagem", "Produto removido com sucesso"));
    }
}

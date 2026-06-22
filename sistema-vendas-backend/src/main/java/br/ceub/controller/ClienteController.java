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

import br.ceub.model.Cliente;
import br.ceub.service.ClienteService;

/**
 * Controller REST do CRUD de clientes.
 *
 * Todas as rotas abaixo de {@code /api/clientes} sao protegidas por JWT
 * automaticamente, gracas a configuracao {@code anyRequest().authenticated()}
 * em {@code SecurityConfig} — nao e preciso repetir nenhuma checagem de
 * token aqui dentro do controller, o {@code JwtAuthenticationFilter} ja
 * cuidou disso antes da requisicao chegar neste metodo.
 *
 * Rotas:
 * <ul>
 *   <li>GET    /api/clientes              -> lista todos (ou filtra por ?nome=)</li>
 *   <li>GET    /api/clientes/{id}         -> busca por id</li>
 *   <li>POST   /api/clientes              -> cadastra</li>
 *   <li>PUT    /api/clientes/{id}         -> atualiza</li>
 *   <li>DELETE /api/clientes/{id}         -> remove</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public List<Cliente> listar(@RequestParam(required = false) String nome) {
        if (nome != null && !nome.isBlank()) {
            return clienteService.buscarPorNome(nome);
        }
        return clienteService.listarTodos();
    }

    @GetMapping("/{id}")
    public Cliente buscarPorId(@PathVariable Integer id) {
        return clienteService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<Cliente> cadastrar(@RequestBody Cliente cliente) {
        Cliente salvo = clienteService.cadastrar(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PutMapping("/{id}")
    public Cliente atualizar(@PathVariable Integer id, @RequestBody Cliente cliente) {
        cliente.setId(id);
        return clienteService.atualizar(cliente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> remover(@PathVariable Integer id) {
        clienteService.remover(id);
        return ResponseEntity.ok(Map.of("mensagem", "Cliente removido com sucesso"));
    }
}

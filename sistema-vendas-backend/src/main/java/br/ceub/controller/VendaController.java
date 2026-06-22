package br.ceub.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ceub.dto.RegistrarVendaRequest;
import br.ceub.model.Venda;
import br.ceub.service.VendaService;

/**
 * Controller REST do registro de vendas.
 *
 * Rotas:
 * <ul>
 *   <li>GET  /api/vendas               -> lista todas as vendas</li>
 *   <li>GET  /api/vendas/{id}          -> busca uma venda pelo id</li>
 *   <li>POST /api/vendas               -> registra uma nova venda</li>
 *   <li>POST /api/vendas/{id}/cancelar -> cancela uma venda existente</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/vendas")
public class VendaController {

    private final VendaService vendaService;

    public VendaController(VendaService vendaService) {
        this.vendaService = vendaService;
    }

    @GetMapping
    public List<Venda> listar() {
        return vendaService.listarTodos();
    }

    @GetMapping("/{id}")
    public Venda buscarPorId(@PathVariable Integer id) {
        return vendaService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<Venda> registrar(@RequestBody RegistrarVendaRequest request) {
        Venda venda = vendaService.registrarVenda(request.getClienteId(), request.getItens());
        return ResponseEntity.status(HttpStatus.CREATED).body(venda);
    }

    @PostMapping("/{id}/cancelar")
    public Venda cancelar(@PathVariable Integer id) {
        return vendaService.cancelarVenda(id);
    }
}

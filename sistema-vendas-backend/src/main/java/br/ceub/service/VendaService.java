package br.ceub.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.ceub.dto.ItemPedidoRequest;
import br.ceub.model.Cliente;
import br.ceub.model.ItemVenda;
import br.ceub.model.Produto;
import br.ceub.model.Venda;
import br.ceub.repository.VendaRepository;

/**
 * Regras de negocio do REGISTRO DE VENDAS.
 *
 * O metodo {@link #registrarVenda(Integer, List)} e marcado com
 * {@code @Transactional}: se qualquer passo falhar no meio do caminho
 * (por exemplo, o estoque de um produto acabar entre a validacao e a
 * baixa), o Spring desfaz (ROLLBACK) automaticamente TUDO o que foi
 * feito na transacao — nenhuma baixa de estoque "pela metade" fica
 * salva no banco. Essa garantia de atomicidade e uma vantagem real de
 * usar um banco relacional de verdade (MySQL) em vez de arquivos.
 */
@Service
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ProdutoService produtoService;
    private final ClienteService clienteService;

    public VendaService(VendaRepository vendaRepository, ProdutoService produtoService, ClienteService clienteService) {
        this.vendaRepository = vendaRepository;
        this.produtoService = produtoService;
        this.clienteService = clienteService;
    }

    /**
     * Registra uma nova venda.
     * <ol>
     *   <li>Valida se o cliente existe;</li>
     *   <li>Valida se cada produto existe e tem estoque suficiente (antes
     *       de baixar qualquer estoque, para nao deixar a operacao "pela metade");</li>
     *   <li>Baixa o estoque de cada produto vendido;</li>
     *   <li>Monta e salva a venda com o valor total calculado.</li>
     * </ol>
     */
    @Transactional
    public Venda registrarVenda(Integer clienteId, List<ItemPedidoRequest> pedidos) {
        if (pedidos == null || pedidos.isEmpty()) {
            throw new RegraNegocioException("A venda precisa ter ao menos um item");
        }

        Cliente cliente = clienteService.buscarPorId(clienteId);

        // 1a passada: valida disponibilidade de TODOS os itens antes de baixar qualquer estoque
        List<Produto> produtosValidados = new ArrayList<>();
        for (ItemPedidoRequest pedido : pedidos) {
            if (pedido.getQuantidade() == null || pedido.getQuantidade() <= 0) {
                throw new RegraNegocioException("Quantidade do item deve ser maior que zero");
            }
            Produto produto = produtoService.buscarPorId(pedido.getProdutoId());
            if (produto.getQuantidadeEstoque() < pedido.getQuantidade()) {
                throw new RegraNegocioException(
                        "Estoque insuficiente para '" + produto.getNome() + "'. "
                                + "Disponivel: " + produto.getQuantidadeEstoque()
                                + ", solicitado: " + pedido.getQuantidade());
            }
            produtosValidados.add(produto);
        }

        Venda venda = new Venda();
        venda.setClienteId(cliente.getId());
        venda.setNomeCliente(cliente.getNome());
        venda.setDataHora(LocalDateTime.now());
        venda.setStatus(Venda.Status.CONCLUIDA);

        // 2a passada: baixa o estoque e monta os itens da venda
        List<ItemVenda> itensVenda = new ArrayList<>();
        for (int i = 0; i < pedidos.size(); i++) {
            ItemPedidoRequest pedido = pedidos.get(i);
            Produto produto = produtosValidados.get(i);

            produtoService.baixarEstoque(produto.getId(), pedido.getQuantidade());

            ItemVenda item = new ItemVenda(produto.getId(), produto.getNome(),
                    pedido.getQuantidade(), produto.getPreco());
            item.setVenda(venda);
            itensVenda.add(item);
        }

        venda.setItens(itensVenda); // tambem recalcula o valorTotal
        return vendaRepository.save(venda); // cascade=ALL salva os itens junto, automaticamente
    }

    /**
     * Cancela uma venda ja registrada, devolvendo as quantidades vendidas
     * para o estoque de cada produto.
     */
    @Transactional
    public Venda cancelarVenda(Integer vendaId) {
        Venda venda = buscarPorId(vendaId);
        if (venda.getStatus() == Venda.Status.CANCELADA) {
            throw new RegraNegocioException("Esta venda ja foi cancelada anteriormente");
        }

        for (ItemVenda item : venda.getItens()) {
            produtoService.reporEstoque(item.getProdutoId(), item.getQuantidade());
        }

        venda.setStatus(Venda.Status.CANCELADA);
        return vendaRepository.save(venda);
    }

    public Venda buscarPorId(Integer id) {
        return vendaRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Venda nao encontrada (id " + id + ")"));
    }

    public List<Venda> listarTodos() {
        return vendaRepository.findAll();
    }

    public List<Venda> buscarPorCliente(Integer clienteId) {
        return vendaRepository.findByClienteId(clienteId);
    }

    public List<Venda> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return vendaRepository.findByDataHoraBetween(inicio, fim);
    }
}

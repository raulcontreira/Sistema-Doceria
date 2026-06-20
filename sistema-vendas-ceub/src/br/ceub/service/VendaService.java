package br.ceub.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.ceub.model.Cliente;
import br.ceub.model.ItemVenda;
import br.ceub.model.Produto;
import br.ceub.model.Venda;
import br.ceub.repository.VendaRepository;

/**
 * Regras de negocio do REGISTRO DE VENDAS.
 *
 * Esta classe e o coracao do requisito "Registro de vendas funcionando":
 * ela valida o cliente e os produtos, confere se ha estoque suficiente,
 * registra a venda e aciona a baixa de estoque atraves do
 * {@link ProdutoService}. Tambem permite cancelar uma venda, devolvendo
 * as unidades ao estoque.
 */
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ProdutoService produtoService;
    private final ClienteService clienteService;

    public VendaService() {
        this.vendaRepository = new VendaRepository();
        this.produtoService = new ProdutoService();
        this.clienteService = new ClienteService();
    }

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
    public Venda registrarVenda(int clienteId, List<ItemPedido> pedidos) {
        if (pedidos == null || pedidos.isEmpty()) {
            throw new RegraNegocioException("A venda precisa ter ao menos um item");
        }

        Cliente cliente = clienteService.buscarPorId(clienteId);

        // 1a passada: valida disponibilidade de TODOS os itens antes de baixar qualquer estoque
        List<Produto> produtosValidados = new ArrayList<>();
        for (ItemPedido pedido : pedidos) {
            if (pedido.getQuantidade() <= 0) {
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

        // 2a passada: baixa o estoque e monta os itens da venda
        List<ItemVenda> itensVenda = new ArrayList<>();
        for (int i = 0; i < pedidos.size(); i++) {
            ItemPedido pedido = pedidos.get(i);
            Produto produto = produtosValidados.get(i);

            produtoService.baixarEstoque(produto.getId(), pedido.getQuantidade());

            itensVenda.add(new ItemVenda(produto.getId(), produto.getNome(),
                    pedido.getQuantidade(), produto.getPreco()));
        }

        Venda venda = new Venda(0, cliente.getId(), cliente.getNome(), LocalDateTime.now(), itensVenda);
        return vendaRepository.salvar(venda);
    }

    /**
     * Cancela uma venda ja registrada, devolvendo as quantidades vendidas
     * para o estoque de cada produto.
     */
    public Venda cancelarVenda(int vendaId) {
        Venda venda = buscarPorId(vendaId);
        if (venda.getStatus() == Venda.Status.CANCELADA) {
            throw new RegraNegocioException("Esta venda ja foi cancelada anteriormente");
        }

        for (ItemVenda item : venda.getItens()) {
            produtoService.reporEstoque(item.getProdutoId(), item.getQuantidade());
        }

        venda.setStatus(Venda.Status.CANCELADA);
        return vendaRepository.atualizar(venda);
    }

    public Venda buscarPorId(int id) {
        Venda venda = vendaRepository.buscarPorId(id);
        if (venda == null) {
            throw new RegraNegocioException("Venda nao encontrada (id " + id + ")");
        }
        return venda;
    }

    public List<Venda> listarTodos() {
        return vendaRepository.listarTodos();
    }

    public List<Venda> buscarPorCliente(int clienteId) {
        return vendaRepository.buscarPorCliente(clienteId);
    }

    public List<Venda> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return vendaRepository.buscarPorPeriodo(inicio, fim);
    }
}

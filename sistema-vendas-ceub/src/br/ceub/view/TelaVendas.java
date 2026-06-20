package br.ceub.view;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import br.ceub.controller.ClienteController;
import br.ceub.controller.ProdutoController;
import br.ceub.controller.VendaController;
import br.ceub.model.Cliente;
import br.ceub.model.ItemVenda;
import br.ceub.model.Produto;
import br.ceub.model.Venda;
import br.ceub.service.ItemPedido;
import br.ceub.service.RegraNegocioException;

/**
 * Tela Swing (JFrame independente) de REGISTRO DE VENDAS.
 *
 * Funcionamento:
 * <ol>
 *   <li>O operador informa o id do cliente e o id/quantidade de um produto,
 *       e clica em "Adicionar item" para monta o carrinho da venda atual;</li>
 *   <li>O carrinho aparece na tabela superior, com o subtotal de cada item;</li>
 *   <li>Ao clicar em "Finalizar venda", o {@link VendaController} (e, por
 *       baixo, o {@link br.ceub.service.VendaService}) valida estoque e
 *       grava a venda, dando baixa automatica no estoque de cada produto;</li>
 *   <li>A tabela inferior lista o historico de vendas ja registradas, com
 *       opcao de cancelar uma venda (devolve os itens ao estoque).</li>
 * </ol>
 */
public class TelaVendas extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ClienteController clienteController = new ClienteController();
    private final ProdutoController produtoController = new ProdutoController();
    private final VendaController vendaController = new VendaController();

    private JTextField campoClienteId;
    private JTextField campoProdutoId;
    private JTextField campoQuantidade;

    private JTable tabelaCarrinho;
    private DefaultTableModel modeloCarrinho;
    private final List<ItemPedido> carrinho = new ArrayList<>();

    private JTable tabelaVendas;
    private DefaultTableModel modeloVendas;

    public TelaVendas() {
        super("Vendas - Registro e Consulta");
        montarInterface();
        atualizarTabelaVendas();
    }

    private void montarInterface() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel painelSuperior = new JPanel(new BorderLayout(10, 10));
        painelSuperior.add(montarFormularioNovaVenda(), BorderLayout.NORTH);
        painelSuperior.add(montarTabelaCarrinho(), BorderLayout.CENTER);

        painelPrincipal.add(painelSuperior, BorderLayout.NORTH);
        painelPrincipal.add(montarTabelaVendas(), BorderLayout.CENTER);

        setContentPane(painelPrincipal);
    }

    private JPanel montarFormularioNovaVenda() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createTitledBorder("Nova venda"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        campoClienteId = new JTextField(5);
        campoProdutoId = new JTextField(5);
        campoQuantidade = new JTextField(5);

        gbc.gridx = 0; gbc.gridy = 0; painel.add(new JLabel("Id do Cliente:"), gbc);
        gbc.gridx = 1; painel.add(campoClienteId, gbc);

        gbc.gridx = 2; painel.add(new JLabel("Id do Produto:"), gbc);
        gbc.gridx = 3; painel.add(campoProdutoId, gbc);

        gbc.gridx = 4; painel.add(new JLabel("Quantidade:"), gbc);
        gbc.gridx = 5; painel.add(campoQuantidade, gbc);

        JButton botaoAdicionar = new JButton("Adicionar item");
        botaoAdicionar.addActionListener(e -> adicionarItemAoCarrinho());
        gbc.gridx = 6; painel.add(botaoAdicionar, gbc);

        JButton botaoFinalizar = new JButton("Finalizar venda");
        botaoFinalizar.addActionListener(e -> finalizarVenda());
        gbc.gridx = 7; painel.add(botaoFinalizar, gbc);

        JButton botaoLimparCarrinho = new JButton("Limpar carrinho");
        botaoLimparCarrinho.addActionListener(e -> limparCarrinho());
        gbc.gridx = 8; painel.add(botaoLimparCarrinho, gbc);

        return painel;
    }

    private JScrollPane montarTabelaCarrinho() {
        modeloCarrinho = new DefaultTableModel(
                new Object[]{"Produto Id", "Nome", "Quantidade", "Preco Unit.", "Subtotal"}, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaCarrinho = new JTable(modeloCarrinho);
        tabelaCarrinho.setPreferredScrollableViewportSize(new java.awt.Dimension(800, 120));
        JScrollPane scroll = new JScrollPane(tabelaCarrinho);
        scroll.setBorder(BorderFactory.createTitledBorder("Itens da venda atual (carrinho)"));
        return scroll;
    }

    private JScrollPane montarTabelaVendas() {
        modeloVendas = new DefaultTableModel(
                new Object[]{"Id", "Cliente", "Data/Hora", "Total (R$)", "Status"}, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaVendas = new JTable(modeloVendas);
        JScrollPane scroll = new JScrollPane(tabelaVendas);
        scroll.setBorder(BorderFactory.createTitledBorder("Historico de vendas (duplo clique para cancelar)"));

        tabelaVendas.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evento) {
                if (evento.getClickCount() == 2) {
                    cancelarVendaSelecionada();
                }
            }
        });

        return scroll;
    }

    private void adicionarItemAoCarrinho() {
        try {
            int produtoId = Integer.parseInt(campoProdutoId.getText().trim());
            int quantidade = Integer.parseInt(campoQuantidade.getText().trim());

            if (quantidade <= 0) {
                JOptionPane.showMessageDialog(this, "Quantidade deve ser maior que zero.");
                return;
            }

            Produto produto = produtoController.buscarPorId(produtoId);

            carrinho.add(new ItemPedido(produtoId, quantidade));
            modeloCarrinho.addRow(new Object[]{
                    produto.getId(), produto.getNome(), quantidade,
                    String.format("%.2f", produto.getPreco()),
                    String.format("%.2f", produto.getPreco() * quantidade)
            });

            campoProdutoId.setText("");
            campoQuantidade.setText("");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Informe o id do produto e a quantidade corretamente.",
                    "Dados invalidos", JOptionPane.WARNING_MESSAGE);
        } catch (RegraNegocioException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Produto invalido", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void finalizarVenda() {
        if (carrinho.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Adicione ao menos um item antes de finalizar a venda.");
            return;
        }
        try {
            int clienteId = Integer.parseInt(campoClienteId.getText().trim());
            Cliente cliente = clienteController.buscarPorId(clienteId); // valida que o cliente existe

            Venda venda = vendaController.registrarVenda(clienteId, carrinho);

            JOptionPane.showMessageDialog(this,
                    "Venda #" + venda.getId() + " registrada com sucesso para " + cliente.getNome()
                            + "!\nTotal: R$ " + String.format("%.2f", venda.getValorTotal()),
                    "Venda concluida", JOptionPane.INFORMATION_MESSAGE);

            limparCarrinho();
            atualizarTabelaVendas();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Informe o id do cliente corretamente.",
                    "Dados invalidos", JOptionPane.WARNING_MESSAGE);
        } catch (RegraNegocioException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Nao foi possivel registrar a venda",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void cancelarVendaSelecionada() {
        int linha = tabelaVendas.getSelectedRow();
        if (linha < 0) {
            return;
        }
        int vendaId = Integer.parseInt(modeloVendas.getValueAt(linha, 0).toString());
        String statusAtual = modeloVendas.getValueAt(linha, 4).toString();

        if ("CANCELADA".equals(statusAtual)) {
            JOptionPane.showMessageDialog(this, "Esta venda ja esta cancelada.");
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Cancelar a venda #" + vendaId + "? Os itens voltarao ao estoque.",
                "Confirmar cancelamento", JOptionPane.YES_NO_OPTION);
        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            vendaController.cancelarVenda(vendaId);
            JOptionPane.showMessageDialog(this, "Venda cancelada e estoque atualizado.");
            atualizarTabelaVendas();
        } catch (RegraNegocioException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Nao foi possivel cancelar", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void limparCarrinho() {
        carrinho.clear();
        modeloCarrinho.setRowCount(0);
    }

    private void atualizarTabelaVendas() {
        modeloVendas.setRowCount(0);
        List<Venda> vendas = vendaController.listarTodos();
        for (Venda venda : vendas) {
            modeloVendas.addRow(new Object[]{
                    venda.getId(),
                    venda.getNomeCliente(),
                    venda.getDataHora() != null ? venda.getDataHora().format(FORMATO_DATA) : "",
                    String.format("%.2f", venda.getValorTotal()),
                    venda.getStatus()
            });
        }
    }
}

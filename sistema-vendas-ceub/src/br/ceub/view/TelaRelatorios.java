package br.ceub.view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import br.ceub.controller.RelatorioController;
import br.ceub.service.RelatorioService;

/**
 * Tela Swing (JFrame independente) de RELATORIOS DE VENDAS.
 *
 * Mostra um resumo geral (quantidade de vendas, faturamento total e
 * ticket medio) e uma tabela com o ranking de produtos mais vendidos,
 * tudo calculado pelo {@link RelatorioService} a partir do historico
 * de vendas concluidas.
 */
public class TelaRelatorios extends JFrame {

    private static final long serialVersionUID = 1L;

    private final RelatorioController relatorioController = new RelatorioController();

    private JLabel labelQuantidadeVendas;
    private JLabel labelFaturamentoTotal;
    private JLabel labelTicketMedio;
    private DefaultTableModel modeloTabelaProdutos;

    public TelaRelatorios() {
        super("Relatorios de Vendas");
        montarInterface();
        gerarRelatorio();
    }

    private void montarInterface() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(640, 480);
        setLocationRelativeTo(null);

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        painelPrincipal.add(montarPainelResumo(), BorderLayout.NORTH);
        painelPrincipal.add(montarTabelaProdutos(), BorderLayout.CENTER);

        JButton botaoAtualizar = new JButton("Atualizar relatorio");
        botaoAtualizar.addActionListener(e -> gerarRelatorio());
        painelPrincipal.add(botaoAtualizar, BorderLayout.SOUTH);

        setContentPane(painelPrincipal);
    }

    private JPanel montarPainelResumo() {
        JPanel painel = new JPanel(new GridLayout(1, 3, 10, 10));
        painel.setBorder(BorderFactory.createTitledBorder("Resumo geral"));

        labelQuantidadeVendas = criarLabelResumo("Vendas: -");
        labelFaturamentoTotal = criarLabelResumo("Faturamento: -");
        labelTicketMedio = criarLabelResumo("Ticket medio: -");

        painel.add(labelQuantidadeVendas);
        painel.add(labelFaturamentoTotal);
        painel.add(labelTicketMedio);
        return painel;
    }

    private JLabel criarLabelResumo(String textoInicial) {
        JLabel label = new JLabel(textoInicial, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        return label;
    }

    private JScrollPane montarTabelaProdutos() {
        modeloTabelaProdutos = new DefaultTableModel(
                new Object[]{"Produto", "Quantidade vendida", "Faturamento (R$)"}, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tabela = new JTable(modeloTabelaProdutos);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createTitledBorder("Produtos mais vendidos"));
        return scroll;
    }

    private void gerarRelatorio() {
        RelatorioService.ResumoVendas resumo = relatorioController.gerarResumoGeral();

        labelQuantidadeVendas.setText("Vendas: " + resumo.quantidadeVendas);
        labelFaturamentoTotal.setText("Faturamento: R$ " + String.format("%.2f", resumo.faturamentoTotal));
        labelTicketMedio.setText("Ticket medio: R$ " + String.format("%.2f", resumo.ticketMedio));

        modeloTabelaProdutos.setRowCount(0);
        for (RelatorioService.ItemRelatorioProduto item : resumo.produtosMaisVendidos) {
            modeloTabelaProdutos.addRow(new Object[]{
                    item.nomeProduto, item.quantidadeVendida, String.format("%.2f", item.faturamento)
            });
        }
    }
}

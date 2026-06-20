package br.ceub.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import br.ceub.controller.ProdutoController;
import br.ceub.model.Produto;
import br.ceub.service.RegraNegocioException;

/**
 * Tela Swing (JFrame independente) do CRUD de produtos, que tambem serve
 * como tela de CONTROLE DE ESTOQUE: a coluna "Estoque" e destacada em
 * vermelho quando o produto esta no nivel minimo ou abaixo dele
 * ({@link Produto#isEstoqueBaixo()}).
 */
public class TelaProdutos extends JFrame {

    private static final long serialVersionUID = 1L;

    private final ProdutoController produtoController = new ProdutoController();

    private JTextField campoId;
    private JTextField campoNome;
    private JTextField campoDescricao;
    private JTextField campoCategoria;
    private JTextField campoPreco;
    private JTextField campoEstoque;
    private JTextField campoEstoqueMinimo;
    private JTable tabela;
    private DefaultTableModel modeloTabela;

    public TelaProdutos() {
        super("Produtos - Cadastro e Controle de Estoque (CRUD)");
        montarInterface();
        atualizarTabela();
    }

    private void montarInterface() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 540);
        setLocationRelativeTo(null);

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        painelPrincipal.add(montarFormulario(), BorderLayout.NORTH);
        painelPrincipal.add(montarTabela(), BorderLayout.CENTER);
        painelPrincipal.add(montarBotoes(), BorderLayout.SOUTH);

        setContentPane(painelPrincipal);
    }

    private JPanel montarFormulario() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createTitledBorder("Dados do produto"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        campoId = new JTextField(5);
        campoId.setEditable(false);
        campoNome = new JTextField(18);
        campoDescricao = new JTextField(18);
        campoCategoria = new JTextField(12);
        campoPreco = new JTextField(8);
        campoEstoque = new JTextField(6);
        campoEstoqueMinimo = new JTextField(6);

        int linha = 0;
        gbc.gridx = 0; gbc.gridy = linha; painel.add(new JLabel("Id:"), gbc);
        gbc.gridx = 1; painel.add(campoId, gbc);
        gbc.gridx = 2; painel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 3; painel.add(campoNome, gbc);

        linha++;
        gbc.gridx = 0; gbc.gridy = linha; painel.add(new JLabel("Descricao:"), gbc);
        gbc.gridx = 1; painel.add(campoDescricao, gbc);
        gbc.gridx = 2; painel.add(new JLabel("Categoria:"), gbc);
        gbc.gridx = 3; painel.add(campoCategoria, gbc);

        linha++;
        gbc.gridx = 0; gbc.gridy = linha; painel.add(new JLabel("Preco (R$):"), gbc);
        gbc.gridx = 1; painel.add(campoPreco, gbc);
        gbc.gridx = 2; painel.add(new JLabel("Estoque atual:"), gbc);
        gbc.gridx = 3; painel.add(campoEstoque, gbc);

        linha++;
        gbc.gridx = 0; gbc.gridy = linha; painel.add(new JLabel("Estoque minimo:"), gbc);
        gbc.gridx = 1; painel.add(campoEstoqueMinimo, gbc);

        return painel;
    }

    private JScrollPane montarTabela() {
        modeloTabela = new DefaultTableModel(
                new Object[]{"Id", "Nome", "Categoria", "Preco", "Estoque", "Estoque minimo"}, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabela = new JTable(modeloTabela);

        // Destaca em vermelho a linha cujo estoque esta baixo (coluna "Estoque", indice 4)
        tabela.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                int estoqueAtual = Integer.parseInt(modeloTabela.getValueAt(row, 4).toString());
                int estoqueMinimo = Integer.parseInt(modeloTabela.getValueAt(row, 5).toString());
                c.setForeground(estoqueAtual <= estoqueMinimo ? Color.RED : Color.BLACK);
                return c;
            }
        });

        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabela.getSelectedRow() >= 0) {
                preencherFormularioComLinhaSelecionada();
            }
        });
        return new JScrollPane(tabela);
    }

    private JPanel montarBotoes() {
        JPanel painel = new JPanel();

        JButton botaoSalvar = new JButton("Salvar");
        botaoSalvar.addActionListener(e -> salvar());

        JButton botaoExcluir = new JButton("Excluir");
        botaoExcluir.addActionListener(e -> excluir());

        JButton botaoLimpar = new JButton("Limpar / Novo");
        botaoLimpar.addActionListener(e -> limparFormulario());

        JButton botaoEstoqueBaixo = new JButton("Ver apenas estoque baixo");
        botaoEstoqueBaixo.addActionListener(e -> mostrarApenasEstoqueBaixo());

        JButton botaoVerTodos = new JButton("Ver todos");
        botaoVerTodos.addActionListener(e -> atualizarTabela());

        painel.add(botaoSalvar);
        painel.add(botaoExcluir);
        painel.add(botaoLimpar);
        painel.add(botaoEstoqueBaixo);
        painel.add(botaoVerTodos);
        return painel;
    }

    private void salvar() {
        try {
            Produto produto = new Produto();
            if (!campoId.getText().trim().isEmpty()) {
                produto.setId(Integer.parseInt(campoId.getText().trim()));
            }
            produto.setNome(campoNome.getText().trim());
            produto.setDescricao(campoDescricao.getText().trim());
            produto.setCategoria(campoCategoria.getText().trim());
            produto.setPreco(parseDoubleSeguro(campoPreco.getText()));
            produto.setQuantidadeEstoque(parseIntSeguro(campoEstoque.getText()));
            produto.setEstoqueMinimo(parseIntSeguro(campoEstoqueMinimo.getText()));

            if (produto.getId() == 0) {
                produtoController.cadastrar(produto);
                JOptionPane.showMessageDialog(this, "Produto cadastrado com sucesso!");
            } else {
                produtoController.atualizar(produto);
                JOptionPane.showMessageDialog(this, "Produto atualizado com sucesso!");
            }

            limparFormulario();
            atualizarTabela();

        } catch (RegraNegocioException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Nao foi possivel salvar", JOptionPane.WARNING_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Preco, estoque e estoque minimo devem ser numeros validos.",
                    "Dados invalidos", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void excluir() {
        if (campoId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um produto na tabela primeiro.");
            return;
        }
        int id = Integer.parseInt(campoId.getText().trim());

        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir este produto?", "Confirmar exclusao",
                JOptionPane.YES_NO_OPTION);
        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            produtoController.remover(id);
            JOptionPane.showMessageDialog(this, "Produto excluido com sucesso!");
            limparFormulario();
            atualizarTabela();
        } catch (RegraNegocioException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Nao foi possivel excluir", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void limparFormulario() {
        campoId.setText("");
        campoNome.setText("");
        campoDescricao.setText("");
        campoCategoria.setText("");
        campoPreco.setText("");
        campoEstoque.setText("");
        campoEstoqueMinimo.setText("");
        tabela.clearSelection();
    }

    private void preencherFormularioComLinhaSelecionada() {
        int linha = tabela.getSelectedRow();
        campoId.setText(modeloTabela.getValueAt(linha, 0).toString());
        campoNome.setText(modeloTabela.getValueAt(linha, 1).toString());
        campoCategoria.setText(String.valueOf(modeloTabela.getValueAt(linha, 2)));
        campoPreco.setText(modeloTabela.getValueAt(linha, 3).toString());
        campoEstoque.setText(modeloTabela.getValueAt(linha, 4).toString());
        campoEstoqueMinimo.setText(modeloTabela.getValueAt(linha, 5).toString());

        Produto produtoCompleto = produtoController.buscarPorId(Integer.parseInt(campoId.getText()));
        campoDescricao.setText(produtoCompleto.getDescricao());
    }

    private void atualizarTabela() {
        modeloTabela.setRowCount(0);
        List<Produto> produtos = produtoController.listarTodos();
        preencherTabelaComLista(produtos);
    }

    private void mostrarApenasEstoqueBaixo() {
        modeloTabela.setRowCount(0);
        List<Produto> produtos = produtoController.listarComEstoqueBaixo();
        preencherTabelaComLista(produtos);
        if (produtos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum produto com estoque baixo no momento.");
        }
    }

    private void preencherTabelaComLista(List<Produto> produtos) {
        for (Produto produto : produtos) {
            modeloTabela.addRow(new Object[]{
                    produto.getId(), produto.getNome(), produto.getCategoria(),
                    String.format("%.2f", produto.getPreco()),
                    produto.getQuantidadeEstoque(), produto.getEstoqueMinimo()
            });
        }
    }

    private double parseDoubleSeguro(String texto) {
        return texto == null || texto.trim().isEmpty() ? 0 : Double.parseDouble(texto.trim().replace(",", "."));
    }

    private int parseIntSeguro(String texto) {
        return texto == null || texto.trim().isEmpty() ? 0 : Integer.parseInt(texto.trim());
    }
}

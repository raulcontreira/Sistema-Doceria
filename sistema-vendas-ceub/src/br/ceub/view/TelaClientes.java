package br.ceub.view;

import java.awt.BorderLayout;
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
import javax.swing.table.DefaultTableModel;

import br.ceub.controller.ClienteController;
import br.ceub.model.Cliente;
import br.ceub.service.RegraNegocioException;

/**
 * Tela Swing (JFrame independente) do CRUD de clientes.
 *
 * Fluxo de uso:
 * <ol>
 *   <li>A tabela mostra todos os clientes cadastrados (metodo {@link #atualizarTabela()});</li>
 *   <li>Ao clicar em uma linha, os campos do formulario sao preenchidos (selecionarLinha);</li>
 *   <li>"Salvar" cadastra um cliente novo (se o campo Id estiver vazio) ou atualiza um existente;</li>
 *   <li>"Excluir" remove o cliente selecionado;</li>
 *   <li>"Limpar" esvazia o formulario para um novo cadastro.</li>
 * </ol>
 * Toda a logica de negocio (validacoes, CPF/e-mail duplicados etc.) fica
 * no {@link br.ceub.service.ClienteService}; esta tela so chama o
 * {@link ClienteController} e mostra os resultados/erros ao usuario.
 */
public class TelaClientes extends JFrame {

    private static final long serialVersionUID = 1L;

    private final ClienteController clienteController = new ClienteController();

    private JTextField campoId;
    private JTextField campoNome;
    private JTextField campoCpf;
    private JTextField campoEmail;
    private JTextField campoTelefone;
    private JTable tabela;
    private DefaultTableModel modeloTabela;

    public TelaClientes() {
        super("Clientes - Cadastro (CRUD)");
        montarInterface();
        atualizarTabela();
    }

    private void montarInterface() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(720, 500);
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
        painel.setBorder(BorderFactory.createTitledBorder("Dados do cliente"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        campoId = new JTextField(5);
        campoId.setEditable(false);
        campoNome = new JTextField(20);
        campoCpf = new JTextField(15);
        campoEmail = new JTextField(20);
        campoTelefone = new JTextField(15);

        int linha = 0;
        gbc.gridx = 0; gbc.gridy = linha; painel.add(new JLabel("Id:"), gbc);
        gbc.gridx = 1; painel.add(campoId, gbc);

        gbc.gridx = 2; painel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 3; painel.add(campoNome, gbc);

        linha++;
        gbc.gridx = 0; gbc.gridy = linha; painel.add(new JLabel("CPF:"), gbc);
        gbc.gridx = 1; painel.add(campoCpf, gbc);

        gbc.gridx = 2; painel.add(new JLabel("E-mail:"), gbc);
        gbc.gridx = 3; painel.add(campoEmail, gbc);

        linha++;
        gbc.gridx = 0; gbc.gridy = linha; painel.add(new JLabel("Telefone:"), gbc);
        gbc.gridx = 1; painel.add(campoTelefone, gbc);

        return painel;
    }

    private JScrollPane montarTabela() {
        modeloTabela = new DefaultTableModel(
                new Object[]{"Id", "Nome", "CPF", "E-mail", "Telefone"}, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabela = new JTable(modeloTabela);
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

        painel.add(botaoSalvar);
        painel.add(botaoExcluir);
        painel.add(botaoLimpar);
        return painel;
    }

    private void salvar() {
        try {
            Cliente cliente = new Cliente();
            if (!campoId.getText().trim().isEmpty()) {
                cliente.setId(Integer.parseInt(campoId.getText().trim()));
            }
            cliente.setNome(campoNome.getText().trim());
            cliente.setCpf(campoCpf.getText().trim());
            cliente.setEmail(campoEmail.getText().trim());
            cliente.setTelefone(campoTelefone.getText().trim());

            if (cliente.getId() == 0) {
                clienteController.cadastrar(cliente);
                JOptionPane.showMessageDialog(this, "Cliente cadastrado com sucesso!");
            } else {
                clienteController.atualizar(cliente);
                JOptionPane.showMessageDialog(this, "Cliente atualizado com sucesso!");
            }

            limparFormulario();
            atualizarTabela();

        } catch (RegraNegocioException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Nao foi possivel salvar", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void excluir() {
        if (campoId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela primeiro.");
            return;
        }
        int id = Integer.parseInt(campoId.getText().trim());

        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir este cliente?", "Confirmar exclusao",
                JOptionPane.YES_NO_OPTION);
        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            clienteController.remover(id);
            JOptionPane.showMessageDialog(this, "Cliente excluido com sucesso!");
            limparFormulario();
            atualizarTabela();
        } catch (RegraNegocioException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Nao foi possivel excluir", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void limparFormulario() {
        campoId.setText("");
        campoNome.setText("");
        campoCpf.setText("");
        campoEmail.setText("");
        campoTelefone.setText("");
        tabela.clearSelection();
    }

    private void preencherFormularioComLinhaSelecionada() {
        int linha = tabela.getSelectedRow();
        campoId.setText(modeloTabela.getValueAt(linha, 0).toString());
        campoNome.setText(modeloTabela.getValueAt(linha, 1).toString());
        campoCpf.setText(String.valueOf(modeloTabela.getValueAt(linha, 2)));
        campoEmail.setText(String.valueOf(modeloTabela.getValueAt(linha, 3)));
        campoTelefone.setText(String.valueOf(modeloTabela.getValueAt(linha, 4)));
    }

    private void atualizarTabela() {
        modeloTabela.setRowCount(0);
        List<Cliente> clientes = clienteController.listarTodos();
        for (Cliente cliente : clientes) {
            modeloTabela.addRow(new Object[]{
                    cliente.getId(), cliente.getNome(), cliente.getCpf(),
                    cliente.getEmail(), cliente.getTelefone()
            });
        }
    }
}

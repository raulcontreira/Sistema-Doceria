package br.ceub.view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import br.ceub.model.Usuario;

/**
 * Tela principal (menu) do sistema, exibida apos o login. Cada botao
 * abre uma tela independente (JFrame separado) com a funcionalidade
 * correspondente: clientes, produtos/estoque, vendas e relatorios.
 */
public class TelaPrincipal extends JFrame {

    private static final long serialVersionUID = 1L;

    private final Usuario usuarioLogado;
    private final String tokenSessao;

    public TelaPrincipal(Usuario usuarioLogado, String tokenSessao) {
        super("Sistema de Vendas - Menu Principal");
        this.usuarioLogado = usuarioLogado;
        this.tokenSessao = tokenSessao;
        montarInterface();
    }

    private void montarInterface() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 380);
        setLocationRelativeTo(null);

        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel boasVindas = new JLabel(
                "<html><center>Ola, <b>" + usuarioLogado.getNome() + "</b><br>"
                        + "Perfil: " + usuarioLogado.getPerfil() + "</center></html>",
                SwingConstants.CENTER);
        boasVindas.setFont(new Font("SansSerif", Font.PLAIN, 14));
        boasVindas.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        painelPrincipal.add(boasVindas, BorderLayout.NORTH);

        JPanel painelBotoes = new JPanel(new GridLayout(4, 1, 10, 10));

        JButton botaoClientes = new JButton("Clientes (CRUD)");
        botaoClientes.addActionListener(e -> new TelaClientes().setVisible(true));

        JButton botaoProdutos = new JButton("Produtos e Estoque (CRUD)");
        botaoProdutos.addActionListener(e -> new TelaProdutos().setVisible(true));

        JButton botaoVendas = new JButton("Registrar / Consultar Vendas");
        botaoVendas.addActionListener(e -> new TelaVendas().setVisible(true));

        JButton botaoRelatorios = new JButton("Relatorios de Vendas");
        botaoRelatorios.addActionListener(e -> new TelaRelatorios().setVisible(true));

        painelBotoes.add(botaoClientes);
        painelBotoes.add(botaoProdutos);
        painelBotoes.add(botaoVendas);
        painelBotoes.add(botaoRelatorios);

        painelPrincipal.add(painelBotoes, BorderLayout.CENTER);

        JButton botaoSair = new JButton("Sair (logout)");
        botaoSair.addActionListener(e -> {
            new TelaLogin().setVisible(true);
            dispose();
        });
        painelPrincipal.add(botaoSair, BorderLayout.SOUTH);

        setContentPane(painelPrincipal);
    }

    /**
     * Token JWT da sessao atual. Disponivel para qualquer tela que, no
     * futuro, queira chamar a API REST em vez dos Controllers locais
     * (por exemplo, se o cliente Swing for separado do servidor).
     */
    public String getTokenSessao() {
        return tokenSessao;
    }
}

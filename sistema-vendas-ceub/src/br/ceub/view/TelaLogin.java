package br.ceub.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import br.ceub.controller.AuthController;
import br.ceub.model.Usuario;
import br.ceub.service.AutenticacaoService;
import br.ceub.service.RegraNegocioException;

/**
 * Tela de login do sistema (interface Swing). E o JFrame independente
 * por onde o usuario entra no sistema; ao autenticar com sucesso, abre
 * a {@link TelaPrincipal} (menu) e fecha a propria janela de login.
 *
 * Usa o mesmo {@link AuthController} (e, por baixo, o mesmo
 * {@link br.ceub.service.AutenticacaoService}/JWT) usado pela API REST,
 * cumprindo o requisito de a tela Swing e a API compartilharem a mesma
 * regra de autenticacao.
 */
public class TelaLogin extends JFrame {

    private static final long serialVersionUID = 1L;

    private final AuthController authController = new AuthController();

    private JTextField campoLogin;
    private JPasswordField campoSenha;

    public TelaLogin() {
        super("Sistema de Vendas - Login");
        montarInterface();
    }

    private void montarInterface() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 320);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Sistema de Vendas", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        painelPrincipal.add(titulo, BorderLayout.NORTH);

        JPanel painelFormulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        painelFormulario.add(new JLabel("Login:"), gbc);

        campoLogin = new JTextField("admin", 18);
        gbc.gridx = 1;
        painelFormulario.add(campoLogin, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        painelFormulario.add(new JLabel("Senha:"), gbc);

        campoSenha = new JPasswordField("admin123", 18);
        gbc.gridx = 1;
        painelFormulario.add(campoSenha, gbc);

        painelPrincipal.add(painelFormulario, BorderLayout.CENTER);

        JButton botaoEntrar = new JButton("Entrar");
        botaoEntrar.addActionListener(e -> autenticar());

        JLabel dica = new JLabel("<html><center>Usuario padrao: <b>admin</b> / senha: <b>admin123</b></center></html>",
                SwingConstants.CENTER);
        dica.setForeground(Color.GRAY);

        JPanel painelInferior = new JPanel(new BorderLayout());
        painelInferior.add(botaoEntrar, BorderLayout.NORTH);
        painelInferior.add(dica, BorderLayout.SOUTH);
        painelInferior.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        painelPrincipal.add(painelInferior, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(botaoEntrar);
        setContentPane(painelPrincipal);
    }

    /**
     * Chama o AuthController (mesma logica usada pela API REST) para
     * validar login/senha e gerar o token JWT da sessao.
     */
    private void autenticar() {
        String login = campoLogin.getText().trim();
        String senha = new String(campoSenha.getPassword());

        try {
            AutenticacaoService.LoginResultado resultado = authController.login(login, senha);
            Usuario usuario = resultado.usuario;

            JOptionPane.showMessageDialog(this,
                    "Bem-vindo(a), " + usuario.getNome() + "!",
                    "Login realizado", JOptionPane.INFORMATION_MESSAGE);

            new TelaPrincipal(usuario, resultado.token).setVisible(true);
            dispose();

        } catch (RegraNegocioException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Falha no login", JOptionPane.ERROR_MESSAGE);
        }
    }
}

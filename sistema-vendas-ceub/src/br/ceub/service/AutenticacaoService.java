package br.ceub.service;

import br.ceub.model.Usuario;
import br.ceub.repository.UsuarioRepository;
import br.ceub.security.JwtUtil;
import br.ceub.security.PasswordUtil;

/**
 * Regras de negocio relacionadas a autenticacao: login (gera token JWT),
 * validacao de token e cadastro de novos usuarios do sistema.
 *
 * Esta classe e usada tanto pela tela de login (Swing) quanto pelo
 * endpoint REST {@code POST /api/auth/login}, o que garante que as
 * duas "portas de entrada" do sistema (Swing e API) compartilham a
 * MESMA logica de autenticacao (Arquitetura em Camadas).
 */
public class AutenticacaoService {

    private final UsuarioRepository usuarioRepository;

    public AutenticacaoService() {
        this.usuarioRepository = new UsuarioRepository();
        garantirUsuarioAdminPadrao();
    }

    public AutenticacaoService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Resultado de um login bem sucedido: o token JWT e os dados basicos
     * do usuario autenticado.
     */
    public static class LoginResultado {
        public final String token;
        public final Usuario usuario;

        public LoginResultado(String token, Usuario usuario) {
            this.token = token;
            this.usuario = usuario;
        }
    }

    /**
     * Autentica um usuario pelo login/senha e, se as credenciais forem
     * validas, devolve um token JWT assinado.
     *
     * @throws RegraNegocioException se o login nao existir ou a senha estiver incorreta
     */
    public LoginResultado login(String login, String senha) {
        if (login == null || login.trim().isEmpty() || senha == null || senha.isEmpty()) {
            throw new RegraNegocioException("Informe login e senha");
        }

        Usuario usuario = usuarioRepository.buscarPorLogin(login.trim());
        if (usuario == null) {
            throw new RegraNegocioException("Usuario ou senha invalidos");
        }

        boolean senhaCorreta = PasswordUtil.verificar(senha, usuario.getSalt(), usuario.getSenhaHash());
        if (!senhaCorreta) {
            throw new RegraNegocioException("Usuario ou senha invalidos");
        }

        String token = JwtUtil.gerarToken(usuario.getLogin(), usuario.getNome(), usuario.getPerfil().name());
        return new LoginResultado(token, usuario);
    }

    /**
     * Cadastra um novo usuario do sistema, gerando salt e hash de senha.
     */
    public Usuario cadastrarUsuario(String nome, String login, String senha, Usuario.Perfil perfil) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new RegraNegocioException("Nome e obrigatorio");
        }
        if (login == null || login.trim().isEmpty()) {
            throw new RegraNegocioException("Login e obrigatorio");
        }
        if (senha == null || senha.length() < 3) {
            throw new RegraNegocioException("Senha deve ter ao menos 3 caracteres");
        }
        if (usuarioRepository.buscarPorLogin(login) != null) {
            throw new RegraNegocioException("Ja existe um usuario com este login");
        }

        String salt = PasswordUtil.gerarSalt();
        String hash = PasswordUtil.gerarHash(senha, salt);
        Usuario usuario = new Usuario(0, nome, login, hash, salt, perfil);
        return usuarioRepository.salvar(usuario);
    }

    /**
     * Valida um token JWT recebido na API REST.
     */
    public JwtUtil.TokenInfo validarToken(String token) {
        return JwtUtil.validarToken(token);
    }

    /**
     * Garante que exista pelo menos um usuario administrador para que o
     * sistema possa ser acessado na primeira execucao (login: admin / senha: admin123).
     */
    private void garantirUsuarioAdminPadrao() {
        if (usuarioRepository.buscarPorLogin("admin") == null) {
            String salt = PasswordUtil.gerarSalt();
            String hash = PasswordUtil.gerarHash("admin123", salt);
            Usuario admin = new Usuario(0, "Administrador", "admin", hash, salt, Usuario.Perfil.ADMIN);
            usuarioRepository.salvar(admin);
        }
    }
}

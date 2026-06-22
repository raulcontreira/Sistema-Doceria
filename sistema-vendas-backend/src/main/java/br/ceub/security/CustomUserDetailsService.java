package br.ceub.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.ceub.model.Usuario;
import br.ceub.repository.UsuarioRepository;

/**
 * Implementacao da interface {@code UserDetailsService} do Spring
 * Security: dado um login, busca o {@link Usuario} correspondente no
 * banco (via {@link UsuarioRepository}) e o converte para o formato
 * {@link UserDetails} que o Spring Security entende.
 *
 * Usada em dois momentos:
 * <ol>
 *   <li>No login ({@code AuthController}): o {@code AuthenticationManager}
 *       chama esta classe para conferir login/senha;</li>
 *   <li>No {@code JwtAuthenticationFilter}: depois de validar o token
 *       JWT, ela e usada para recarregar os dados/permissoes do usuario
 *       autenticado e colocar no contexto de seguranca da requisicao.</li>
 * </ol>
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado: " + login));

        return User.withUsername(usuario.getLogin())
                .password(usuario.getSenhaHash())
                .authorities("ROLE_" + usuario.getPerfil().name())
                .build();
    }
}

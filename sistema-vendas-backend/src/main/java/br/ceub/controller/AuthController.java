package br.ceub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ceub.dto.LoginRequest;
import br.ceub.dto.LoginResponse;
import br.ceub.model.Usuario;
import br.ceub.repository.UsuarioRepository;
import br.ceub.security.JwtUtil;
import br.ceub.service.RegraNegocioException;

/**
 * Controller de autenticacao. Esta e a UNICA rota publica da API
 * ({@code POST /api/auth/login}) — todas as outras exigem o token JWT
 * gerado aqui (ver {@code br.ceub.security.SecurityConfig}, que libera
 * especificamente {@code /api/auth/**}).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager,
                           UsuarioRepository usuarioRepository,
                           JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Exemplo de requisicao:
     * <pre>
     * POST /api/auth/login
     * Content-Type: application/json
     *
     * { "login": "admin", "senha": "admin123" }
     * </pre>
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getLogin(), request.getSenha()));
        } catch (BadCredentialsException e) {
            throw new RegraNegocioException("Usuario ou senha invalidos");
        }

        Usuario usuario = usuarioRepository.findByLogin(request.getLogin())
                .orElseThrow(() -> new RegraNegocioException("Usuario ou senha invalidos"));

        String token = jwtUtil.gerarToken(usuario.getLogin(), usuario.getNome(), usuario.getPerfil().name());

        return ResponseEntity.ok(new LoginResponse(token, usuario.getNome(), usuario.getPerfil().name()));
    }
}

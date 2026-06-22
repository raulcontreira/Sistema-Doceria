package br.ceub.security;

import java.io.IOException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro HTTP que roda em TODA requisicao recebida pela API (exceto as
 * rotas de login, que sao publicas) e que cumpre o requisito
 * "API protegida" + "Autenticacao baseada em Token":
 *
 * <ol>
 *   <li>Le o cabecalho {@code Authorization: Bearer <token>};</li>
 *   <li>Valida o token usando {@link JwtUtil#validarToken(String)};</li>
 *   <li>Se for valido, registra o usuario autenticado no
 *       {@code SecurityContextHolder} do Spring Security e deixa a
 *       requisicao seguir para o Controller;</li>
 *   <li>Se for invalido/ausente, responde HTTP 401 imediatamente e
 *       interrompe a cadeia de filtros — o Controller nunca chega a
 *       ser executado.</li>
 * </ol>
 *
 * Este filtro e registrado em {@link SecurityConfig}, antes do filtro
 * padrao de autenticacao por usuario/senha do Spring Security.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // A rota de login e publica: nao exige token, pois e ela quem o fornece.
        if (request.getServletPath().startsWith("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        String cabecalhoAuth = request.getHeader("Authorization");
        String token = extrairToken(cabecalhoAuth);

        JwtUtil.TokenInfo tokenInfo = jwtUtil.validarToken(token);

        if (!tokenInfo.valido) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"erro\":\"Acesso negado: " + tokenInfo.motivoFalha + "\"}");
            return; // nao chama filterChain.doFilter -> bloqueia a requisicao aqui
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(tokenInfo.login);
        UsernamePasswordAuthenticationToken autenticacao =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(autenticacao);

        filterChain.doFilter(request, response);
    }

    private String extrairToken(String cabecalhoAuth) {
        if (cabecalhoAuth == null) {
            return null;
        }
        if (cabecalhoAuth.startsWith("Bearer ")) {
            return cabecalhoAuth.substring("Bearer ".length()).trim();
        }
        return cabecalhoAuth.trim();
    }
}

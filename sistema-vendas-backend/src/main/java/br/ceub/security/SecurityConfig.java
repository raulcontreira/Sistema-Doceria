package br.ceub.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuracao central do Spring Security. E aqui que se define:
 * <ul>
 *   <li>Quais rotas sao publicas ({@code /api/auth/**}) e quais exigem
 *       token JWT (todas as outras sob {@code /api/**}) — o requisito
 *       "API protegida";</li>
 *   <li>Que a aplicacao e {@code STATELESS} (sem sessao HTTP guardada no
 *       servidor): cada requisicao se autentica sozinha, via token, o
 *       que e o padrao correto para uma API REST consumida por um
 *       cliente externo (como o app Swing separado);</li>
 *   <li>O {@link JwtAuthenticationFilter}, que roda ANTES do filtro
 *       padrao de login do Spring Security;</li>
 *   <li>O {@link PasswordEncoder} (BCrypt) usado para gerar e conferir
 *       o hash das senhas dos usuarios.</li>
 * </ul>
 *
 * Nota tecnica: nao declaramos aqui um {@code DaoAuthenticationProvider}
 * manualmente. O Spring Security 6 detecta sozinho, no contexto da
 * aplicacao, que existe um unico Bean {@code UserDetailsService}
 * ({@link CustomUserDetailsService}) e um unico Bean {@code PasswordEncoder}
 * (definido logo abaixo) e os conecta automaticamente ao
 * {@code AuthenticationManager} quando ele e solicitado. Isso evita
 * declarar esse Bean na mao (o que exigiria injetar o
 * {@code PasswordEncoder} no construtor desta mesma classe que o
 * declara — uma dependencia circular).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // API REST sem formulario/sessao: nao precisa de protecao CSRF baseada em cookie
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sessao -> sessao.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(autorizacao -> autorizacao
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * BCrypt e o algoritmo recomendado pelo proprio Spring Security para
     * hash de senha: ele gera um "salt" aleatorio automaticamente e o
     * embute dentro do proprio hash resultante, entao nao e preciso
     * guardar um campo de salt separado (como na versao anterior do
     * projeto, que usava SHA-256 manual).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposto como Bean para que o {@code AuthController} possa chamar
     * {@code authenticationManager.authenticate(...)} ao processar o login.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

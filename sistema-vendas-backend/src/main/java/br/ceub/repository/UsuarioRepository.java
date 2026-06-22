package br.ceub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.ceub.model.Usuario;

/**
 * Repositorio Spring Data JPA do {@link Usuario}.
 * Usado tanto pelo login (AuthController) quanto pelo
 * {@code CustomUserDetailsService} do Spring Security.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByLogin(String login);
}

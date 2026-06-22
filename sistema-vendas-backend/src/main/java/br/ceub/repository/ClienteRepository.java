package br.ceub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.ceub.model.Cliente;

/**
 * Repositorio Spring Data JPA do {@link Cliente}.
 *
 * Ao estender {@link JpaRepository}, esta interface ja ganha de graca
 * (sem nenhuma implementacao manual) os metodos basicos de CRUD:
 * {@code save}, {@code findById}, {@code findAll}, {@code deleteById},
 * etc. Os metodos abaixo sao "queries derivadas": o Spring Data le o
 * NOME do metodo e gera a consulta SQL correspondente automaticamente
 * (ex.: {@code findByCpf} vira {@code SELECT * FROM clientes WHERE cpf = ?}).
 */
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    Optional<Cliente> findByCpf(String cpf);

    Optional<Cliente> findByEmail(String email);

    List<Cliente> findByNomeContainingIgnoreCase(String nome);
}

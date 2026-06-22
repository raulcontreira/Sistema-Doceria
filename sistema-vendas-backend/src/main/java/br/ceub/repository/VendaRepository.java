package br.ceub.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.ceub.model.Venda;

/**
 * Repositorio Spring Data JPA da {@link Venda}.
 * Base do "Registro de Vendas" e dos "Relatorios de Vendas".
 */
public interface VendaRepository extends JpaRepository<Venda, Integer> {

    List<Venda> findByClienteId(Integer clienteId);

    List<Venda> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);
}

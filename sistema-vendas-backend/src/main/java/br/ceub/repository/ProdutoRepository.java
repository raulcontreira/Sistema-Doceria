package br.ceub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.ceub.model.Produto;

/**
 * Repositorio Spring Data JPA do {@link Produto}.
 *
 * O metodo {@link #buscarComEstoqueBaixo()} usa {@code @Query} (JPQL)
 * em vez de uma "query derivada" pelo nome do metodo, porque ele
 * compara DOIS CAMPOS da mesma entidade entre si
 * ({@code quantidadeEstoque <= estoqueMinimo}) — esse tipo de
 * comparacao nao tem como ser expressa so pelo nome do metodo (que so
 * sabe comparar um campo contra um PARAMETRO recebido), entao
 * precisamos escrever a consulta explicitamente.
 */
public interface ProdutoRepository extends JpaRepository<Produto, Integer> {

    List<Produto> findByNomeContainingIgnoreCase(String nome);

    @Query("SELECT p FROM Produto p WHERE p.quantidadeEstoque <= p.estoqueMinimo")
    List<Produto> buscarComEstoqueBaixo();
}

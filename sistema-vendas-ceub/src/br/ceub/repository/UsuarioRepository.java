package br.ceub.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import br.ceub.model.Usuario;
import br.ceub.util.ArquivoPersistenciaUtil;

/**
 * Camada de acesso a dados (Repository) do {@link Usuario}.
 *
 * Esta classe e a UNICA do sistema que sabe "onde" os usuarios estao
 * guardados (hoje, em memoria + arquivo em disco). As camadas de service,
 * controller e view nunca acessam o mapa de dados diretamente: elas so
 * conversam com os metodos publicos abaixo. Isso e a base da
 * "Arquitetura em Camadas" pedida no projeto.
 *
 * Os dados sao {@code static} (compartilhados por todas as instancias
 * desta classe no mesmo processo), pelo mesmo motivo explicado em
 * {@link br.ceub.repository.ProdutoRepository}.
 */
public class UsuarioRepository {

    private static final String ARQUIVO = "usuarios.dat";

    private static final Map<Integer, Usuario> dados = carregarDoDisco();
    private static final AtomicInteger proximoId = new AtomicInteger(calcularProximoId());

    @SuppressWarnings("unchecked")
    private static Map<Integer, Usuario> carregarDoDisco() {
        Map<Integer, Usuario> carregados = ArquivoPersistenciaUtil.carregar(ARQUIVO);
        return carregados != null ? carregados : new LinkedHashMap<>();
    }

    private static int calcularProximoId() {
        int maiorId = 0;
        for (Integer id : dados.keySet()) {
            maiorId = Math.max(maiorId, id);
        }
        return maiorId + 1;
    }

    public Usuario salvar(Usuario usuario) {
        if (usuario.getId() == 0) {
            usuario.setId(proximoId.getAndIncrement());
        }
        dados.put(usuario.getId(), usuario);
        persistir();
        return usuario;
    }

    public Usuario buscarPorId(int id) {
        return dados.get(id);
    }

    public Usuario buscarPorLogin(String login) {
        if (login == null) {
            return null;
        }
        for (Usuario usuario : dados.values()) {
            if (login.equalsIgnoreCase(usuario.getLogin())) {
                return usuario;
            }
        }
        return null;
    }

    public List<Usuario> listarTodos() {
        return new ArrayList<>(dados.values());
    }

    public Usuario atualizar(Usuario usuario) {
        if (!dados.containsKey(usuario.getId())) {
            return null;
        }
        dados.put(usuario.getId(), usuario);
        persistir();
        return usuario;
    }

    public boolean deletar(int id) {
        boolean removido = dados.remove(id) != null;
        if (removido) {
            persistir();
        }
        return removido;
    }

    private void persistir() {
        ArquivoPersistenciaUtil.salvar(ARQUIVO, dados);
    }
}

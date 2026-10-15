package farmasmart.repositorio;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public abstract class RepositorioMemoria<T, ID> implements Repositorio<T, ID> {

    protected final Map<ID, T> dados = new LinkedHashMap<>();

    protected abstract ID obterId(T entidade);

    @Override
    public T salvar(T entidade) {
        ID id = obterId(entidade);
        if (dados.containsKey(id)) {
            throw new IllegalStateException("Já existe registro com id " + id + ".");
        }
        dados.put(id, entidade);
        return entidade;
    }

    @Override
    public Optional<T> buscarPorId(ID id) {
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public List<T> listarTodos() {
        return new ArrayList<>(dados.values());
    }

    @Override
    public T atualizar(T entidade) {
        ID id = obterId(entidade);
        if (!dados.containsKey(id)) {
            throw new IllegalStateException("Não existe registro com id " + id + ".");
        }
        dados.put(id, entidade);
        return entidade;
    }

    @Override
    public boolean remover(ID id) {
        return dados.remove(id) != null;
    }

    @Override
    public boolean existe(ID id) {
        return dados.containsKey(id);
    }
}

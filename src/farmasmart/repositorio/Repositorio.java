package farmasmart.repositorio;

import java.util.List;
import java.util.Optional;


public interface Repositorio<T, ID> {

    /** CREATE */
    T salvar(T entidade);

    /** READ (um) */
    Optional<T> buscarPorId(ID id);

    /** READ (todos) */
    List<T> listarTodos();

    /** UPDATE */
    T atualizar(T entidade);

    /** DELETE */
    boolean remover(ID id);

    boolean existe(ID id);
}

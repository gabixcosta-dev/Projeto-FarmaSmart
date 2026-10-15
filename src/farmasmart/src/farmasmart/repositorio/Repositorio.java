package farmasmart.repositorio;

import java.util.List;
import java.util.Optional;

/**
 * Contrato genérico de persistência (CRUD). A camada de serviço só conhece
 * esta interface, nunca a implementação concreta (memória, arquivo, banco...).
 *
 * @param <T>  tipo da entidade
 * @param <ID> tipo do identificador
 */
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

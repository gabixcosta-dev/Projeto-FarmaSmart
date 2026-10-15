package farmasmart.servico;

import java.util.List;

/**
 * Contrato da camada de serviço. A camada de visão só conhece esta interface.
 */
public interface Servico<T, ID> {

    T cadastrar(T entidade);

    T buscarPorId(ID id);

    List<T> listar();

    T atualizar(T entidade);

    void excluir(ID id);
}

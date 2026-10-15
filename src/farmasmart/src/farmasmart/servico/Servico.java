package farmasmart.servico;

import java.util.List;

public interface Servico<T, ID> {

    T cadastrar(T entidade);

    T buscarPorId(ID id);

    List<T> listar();

    T atualizar(T entidade);

    void excluir(ID id);
}

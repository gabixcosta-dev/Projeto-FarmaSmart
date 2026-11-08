package farmasmart.repositorio;

import java.util.List;

import farmasmart.Receita;

public interface ReceitaRepositorio extends Repositorio<Receita, Long> {

    List<Receita> buscarPorCliente(String clienteId);

    List<Receita> buscarPorMedico(String medicoId);
}

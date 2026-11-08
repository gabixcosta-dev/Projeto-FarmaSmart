package farmasmart.repositorio;

import java.util.List;

import farmasmart.Medicamento;

public interface MedicamentoRepositorio extends Repositorio<Medicamento, String> {

    List<Medicamento> buscarPorPrincipioAtivo(String principioAtivo);
}

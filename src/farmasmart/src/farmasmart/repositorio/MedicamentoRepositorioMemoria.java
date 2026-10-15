package farmasmart.repositorio;

import java.util.List;
import java.util.stream.Collectors;

import farmasmart.Medicamento;

public class MedicamentoRepositorioMemoria extends RepositorioMemoria<Medicamento, String>
        implements MedicamentoRepositorio {

    @Override
    protected String obterId(Medicamento m) {
        return m.getId();
    }

    @Override
    public List<Medicamento> buscarPorPrincipioAtivo(String principioAtivo) {
        return dados.values().stream()
                .filter(m -> m.getPrincipioAtivo().equalsIgnoreCase(principioAtivo))
                .collect(Collectors.toList());
    }
}

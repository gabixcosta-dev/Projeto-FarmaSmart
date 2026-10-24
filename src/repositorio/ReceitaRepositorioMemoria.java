package farmasmart.repositorio;

import java.util.List;
import java.util.stream.Collectors;

import farmasmart.Receita;

public class ReceitaRepositorioMemoria extends RepositorioMemoria<Receita, Long>
        implements ReceitaRepositorio {

    @Override
    protected Long obterId(Receita r) {
        return r.getId();
    }

    @Override
    public List<Receita> buscarPorCliente(String clienteId) {
        return dados.values().stream()
                .filter(r -> r.getCliente().getId().equals(clienteId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Receita> buscarPorMedico(String medicoId) {
        return dados.values().stream()
                .filter(r -> r.getMedico().getId().equals(medicoId))
                .collect(Collectors.toList());
    }
}

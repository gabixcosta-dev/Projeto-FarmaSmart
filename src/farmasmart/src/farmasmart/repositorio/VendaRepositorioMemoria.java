package farmasmart.repositorio;

import java.util.List;
import java.util.stream.Collectors;

import farmasmart.Medicamento;
import farmasmart.Venda;

public class VendaRepositorioMemoria extends RepositorioMemoria<Venda, String>
        implements VendaRepositorio {

    @Override
    protected String obterId(Venda v) {
        return v.getId();
    }

    @Override
    public List<Venda> buscarPorCliente(String clienteId) {
        return dados.values().stream()
                .filter(v -> v.getCliente() != null && v.getCliente().getId().equals(clienteId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Venda> buscarPorFarmaceutico(String farmaceuticoId) {
        return dados.values().stream()
                .filter(v -> v.getFarmaceutico().getId().equals(farmaceuticoId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Venda> buscarPorReceita(Long receitaId) {
        return dados.values().stream()
                .filter(v -> v.getReceita() != null && v.getReceita().getId().equals(receitaId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Venda> buscarPorMedicamento(Medicamento medicamento) {
        return dados.values().stream()
                .filter(v -> v.contemMedicamento(medicamento))
                .collect(Collectors.toList());
    }
}

package farmasmart.repositorio;

import java.util.List;

import farmasmart.Medicamento;
import farmasmart.Venda;

public interface VendaRepositorio extends Repositorio<Venda, String> {

    List<Venda> buscarPorCliente(String clienteId);

    List<Venda> buscarPorFarmaceutico(String farmaceuticoId);

    List<Venda> buscarPorReceita(Long receitaId);

    List<Venda> buscarPorMedicamento(Medicamento medicamento);
}

package farmasmart.servico;

import java.util.List;

import farmasmart.Medicamento;
import farmasmart.StatusValidacao;

public interface MedicamentoServico extends Servico<Medicamento, String> {

    void registrarValidacao(String medicamentoId, StatusValidacao status, String orgao);

    List<Medicamento> proximosDoVencimento(int dias);
}

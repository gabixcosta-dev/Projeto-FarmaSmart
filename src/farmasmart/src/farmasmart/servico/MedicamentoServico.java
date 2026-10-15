package farmasmart.servico;

import java.util.List;

import farmasmart.Medicamento;
import farmasmart.StatusValidacao;

public interface MedicamentoServico extends Servico<Medicamento, String> {

    /** Registra o resultado da validação sanitária (ex.: consulta à openFDA). */
    void registrarValidacao(String medicamentoId, StatusValidacao status, String orgao);

    /** REQ07: medicamentos que vencem dentro de N dias (e ainda não venceram). */
    List<Medicamento> proximosDoVencimento(int dias);
}

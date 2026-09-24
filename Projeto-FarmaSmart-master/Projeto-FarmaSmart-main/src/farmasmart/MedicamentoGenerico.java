package farmasmart;

import java.time.LocalDate;

public class MedicamentoGenerico extends Medicamento {
  
  private MedicamentoComum medicamentoReferencia;
  
  public MedicamentoGenerico(String id, String nome, String principioAtivo, double preco,
                             int quantidadeEstoque, LocalDate dataValidade,
                             MedicamentoComum medicamentoReferencia) {
    super(id, nome, principioAtivo, preco, quantidadeEstoque, dataValidade, "GENERICO");
    this.medicamentoReferencia = medicamentoReferencia;
  }

  public MedicamentoComum getMedicamentoReferencia() {
    return medicamentoReferencia;
  }
}

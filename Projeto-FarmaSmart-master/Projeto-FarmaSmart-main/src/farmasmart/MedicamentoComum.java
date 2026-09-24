package farmasmart;

import java.time.LocalDate;

public class MedicamentoComum extends Medicamento {

  public MedicamentoComum(String id, String nome, String principioAtivo, double preco,
                          int quantidadeEstoque, LocalDate dataValidade) {
    super(id, nome, principioAtivo, preco, quantidadeEstoque, dataValidade, "COMUM");
  }
}

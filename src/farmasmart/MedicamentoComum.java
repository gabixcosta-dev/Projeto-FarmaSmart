package farmasmart;
 
import java.time.LocalDate;
 
public class MedicamentoComum extends Medicamento {

    private static final long serialVersionUID = 1L;

    public MedicamentoComum(String id, String nome, String principioAtivo, double preco,
                             int quantidadeEstoque, LocalDate dataValidade) {
        super(id, nome, principioAtivo, preco, quantidadeEstoque, dataValidade);
    }
}
 

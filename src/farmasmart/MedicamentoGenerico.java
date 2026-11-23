package farmasmart;
 
import java.time.LocalDate;
 
public class MedicamentoGenerico extends Medicamento {

    private static final long serialVersionUID = 1L;

    private MedicamentoComum medicamentoReferencia;
 
    public MedicamentoGenerico(String id, String nome, String principioAtivo, double preco,
                                int quantidadeEstoque, LocalDate dataValidade,
                                MedicamentoComum medicamentoReferencia) {
        super(id, nome, principioAtivo, preco, quantidadeEstoque, dataValidade);
        this.medicamentoReferencia = medicamentoReferencia;
    }
 
    public MedicamentoComum getMedicamentoReferencia() {
        return medicamentoReferencia;
    }
}
 

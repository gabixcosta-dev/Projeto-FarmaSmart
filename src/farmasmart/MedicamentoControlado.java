package farmasmart;
 
import java.time.LocalDate;
 
public class MedicamentoControlado extends Medicamento {
 
    private ListaControle listaControle;
 
    public MedicamentoControlado(String id, String nome, String principioAtivo, double preco,
                                  int quantidadeEstoque, LocalDate dataValidade,
                                  ListaControle listaControle) {
        super(id, nome, principioAtivo, preco, quantidadeEstoque, dataValidade);
        this.listaControle = listaControle;
    }
 
    public ListaControle getListaControle() {
        return listaControle;
    }
 
    @Override
    public boolean exigeReceita() {
        return true;
    }
 
    @Override
    public boolean aceitaTipoReceita(TipoReceita tipo) {
        switch (listaControle) {
            case A1:
            case A2:
            case A3:
                return tipo == TipoReceita.RECEITA_A_AMARELA;
            case B1:
            case B2:
                return tipo == TipoReceita.RECEITA_B_AZUL;
            case C1:
            case C2:
                return tipo == TipoReceita.RECEITA_C_BRANCA;
            default:
                return false;
        }
    }
}
 

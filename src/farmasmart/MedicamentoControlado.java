package farmasmart;

import java.time.LocalDate;

public class MedicamentoControlado extends Medicamento {

    private ListaControle listaControle;

    public MedicamentoControlado(String id, String nome, String principioAtivo, double preco,
                                 int quantidadeEstoque, LocalDate dataValidade,
                                 ListaControle listaControle) {
        super(id, nome, principioAtivo, preco, quantidadeEstoque, dataValidade, "CONTROLADO");
        this.listaControle = listaControle;
    }

    public ListaControle getListaControle() {
        return listaControle;
    }

}

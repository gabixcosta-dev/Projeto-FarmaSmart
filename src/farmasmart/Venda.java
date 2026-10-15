package modelo;
 
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
 
/**
 * Venda no balcão.
 *
 * ASSOCIAÇÕES: Venda - Farmaceutico (1), Venda - Cliente (0..1), Venda - Receita (0..1)
 * COMPOSIÇÃO: Venda cria e possui os seus ItemVenda.
 *
 * Regras aplicadas (versão simplificada, sem StatusVenda/FormaPagamento):
 *   1) medicamento vencido ou sem validação confirmada não entra na venda;
 *   2) controlado exige cliente + receita válida que cubra aquele item;
 *   3) não deixa vender mais do que o estoque disponível.
 */
public class Venda {
 
    private String id;
    private LocalDate data;
    private Cliente cliente;
    private Farmaceutico farmaceutico;
    private Receita receita;
    private List<ItemVenda> itens = new ArrayList<>();
 
    public Venda(String id, Farmaceutico farmaceutico, Cliente cliente) {
        if (id == null || farmaceutico == null) {
            throw new IllegalArgumentException("Id e farmacêutico são obrigatórios.");
        }
        this.id = id;
        this.farmaceutico = farmaceutico;
        this.cliente = cliente;
        this.data = LocalDate.now();
    }
 
    public void anexarReceita(Receita receita) {
        this.receita = receita;
    }
 
    public void adicionarItem(Medicamento medicamento, int quantidade) {
        if (medicamento == null) {
            throw new IllegalArgumentException("Medicamento é obrigatório.");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva.");
        }
        if (!medicamento.isValidoParaVenda()) {
            throw new IllegalStateException(medicamento.getNome()
                    + " não pode ser vendido (vencido, sem validação ou sem estoque).");
        }
        if (quantidade > medicamento.getQuantidadeEstoque()) {
            throw new IllegalStateException("Estoque insuficiente de " + medicamento.getNome() + ".");
        }
        if (medicamento instanceof MedicamentoControlado) {
            if (cliente == null) {
                throw new IllegalStateException("Controlado exige cliente identificado.");
            }
            if (receita == null || !receita.isValidaEm(data) || !receita.cobre(medicamento)) {
                throw new IllegalStateException(medicamento.getNome()
                        + " exige receita válida que cubra este item.");
            }
        }
        itens.add(new ItemVenda(medicamento, quantidade));
    }
 
    public double calcularTotal() {
        double total = 0;
        for (ItemVenda item : itens) {
            total += item.getSubtotal();
        }
        return total;
    }
 
    /** Efetiva a venda: baixa estoque de cada item e registra a dispensação na receita. */
    public void finalizar() {
        if (itens.isEmpty()) {
            throw new IllegalStateException("Venda sem itens.");
        }
        for (ItemVenda item : itens) {
            item.getMedicamento().baixaEstoque(item.getQuantidade());
            if (item.getMedicamento() instanceof MedicamentoControlado) {
                receita.registrarDispensacao(item.getMedicamento());
            }
        }
    }
 
    public String getId() {
        return id;
    }
 
    public LocalDate getData() {
        return data;
    }
 
    public Cliente getCliente() {
        return cliente;
    }
 
    public Farmaceutico getFarmaceutico() {
        return farmaceutico;
    }
 
    public Receita getReceita() {
        return receita;
    }
 
    public List<ItemVenda> getItens() {
        return itens;
    }
}

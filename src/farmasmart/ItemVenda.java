package farmasmart;
 
import java.math.BigDecimal;

class ItemVenda {
 
    private Medicamento medicamento;
    private int quantidade;
    private BigDecimal precoUnitario;
 
    ItemVenda(Medicamento medicamento, int quantidade) {
        if (medicamento == null) {
            throw new IllegalArgumentException("Medicamento é obrigatório.");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva.");
        }
        this.medicamento = medicamento;
        this.quantidade = quantidade;
        this.precoUnitario = BigDecimal.valueOf(medicamento.getPreco());
    }
 
    void somarQuantidade(int adicional) {
        this.quantidade += adicional;
    }
 
    BigDecimal getSubtotal() {
        BigDecimal subtotal = precoUnitario.multiply(BigDecimal.valueOf(quantidade));
        if (medicamento instanceof MedicamentoGenerico) {
            subtotal = subtotal.multiply(BigDecimal.valueOf(0.9)); // desconto de genérico, REQ10
        }
        return subtotal;
    }
 
    Medicamento getMedicamento() {
        return medicamento;
    }
 
    int getQuantidade() {
        return quantidade;
    }
 
    BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }
}
 

package modelo;
 
class ItemReceita {
 
    private String principioAtivo;
    private int quantidadePrescrita;
    private int quantidadeDispensada;
 
    ItemReceita(String principioAtivo, int quantidadePrescrita) {
        if (principioAtivo == null || principioAtivo.isBlank()) {
            throw new IllegalArgumentException("Princípio ativo é obrigatório.");
        }
        if (quantidadePrescrita <= 0) {
            throw new IllegalArgumentException("Quantidade prescrita deve ser positiva.");
        }
        this.principioAtivo = principioAtivo;
        this.quantidadePrescrita = quantidadePrescrita;
    }
 
    void registrarDispensacao(int quantidade) {
        if (quantidade <= 0 || quantidade > getQuantidadeRestante()) {
            throw new IllegalStateException("Quantidade excede o saldo da receita para " + principioAtivo + ".");
        }
        this.quantidadeDispensada += quantidade;
    }
 
    int getQuantidadeRestante() {
        return quantidadePrescrita - quantidadeDispensada;
    }
 
    String getPrincipioAtivo() {
        return principioAtivo;
    }
 
    int getQuantidadePrescrita() {
        return quantidadePrescrita;
    }
 
    int getQuantidadeDispensada() {
        return quantidadeDispensada;
    }
}
 

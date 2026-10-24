package farmasmart.servico;

import java.math.BigDecimal;

import farmasmart.FormaPagamento;
import farmasmart.Venda;


public interface VendaServico extends Servico<Venda, String> {

    void adicionarItem(String vendaId, String medicamentoId, int quantidade);

    void anexarReceita(String vendaId, Long receitaId);

 
    BigDecimal finalizar(String vendaId, FormaPagamento forma);

    void cancelar(String vendaId);
}

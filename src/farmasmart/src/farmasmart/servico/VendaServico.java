package farmasmart.servico;

import java.math.BigDecimal;

import farmasmart.FormaPagamento;
import farmasmart.Venda;

/**
 * Venda não é um CRUD puro: depois de aberta (cadastrar) ela evolui por
 * operações, não por "atualizar".
 */
public interface VendaServico extends Servico<Venda, String> {

    void adicionarItem(String vendaId, String medicamentoId, int quantidade);

    void anexarReceita(String vendaId, Long receitaId);

    /** Finaliza a venda e devolve o valor total cobrado. */
    BigDecimal finalizar(String vendaId, FormaPagamento forma);

    void cancelar(String vendaId);
}

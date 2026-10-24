package farmasmart.servico;

import java.math.BigDecimal;

import farmasmart.Cliente;
import farmasmart.Farmaceutico;
import farmasmart.FormaPagamento;
import farmasmart.Medicamento;
import farmasmart.Receita;
import farmasmart.StatusVenda;
import farmasmart.Venda;
import farmasmart.repositorio.Repositorio;
import farmasmart.repositorio.VendaRepositorio;

public class VendaServicoImpl extends ServicoBase<Venda, String> implements VendaServico {

    private final VendaRepositorio vendas;
    private final Repositorio<Farmaceutico, String> farmaceuticos;
    private final Repositorio<Cliente, String> clientes;
    private final Repositorio<Medicamento, String> medicamentos;
    private final Repositorio<Receita, Long> receitas;

    public VendaServicoImpl(VendaRepositorio vendas, Repositorio<Farmaceutico, String> farmaceuticos,
                            Repositorio<Cliente, String> clientes, Repositorio<Medicamento, String> medicamentos,
                            Repositorio<Receita, Long> receitas) {
        super(vendas, "Venda");
        this.vendas = vendas;
        this.farmaceuticos = farmaceuticos;
        this.clientes = clientes;
        this.medicamentos = medicamentos;
        this.receitas = receitas;
    }

    @Override
    protected String obterId(Venda v) {
        return v.getId();
    }

    @Override
    protected void validar(Venda v) {
        exigirNaoNulo(v, "venda");
        exigirTexto(v.getId(), "id");
        if (!farmaceuticos.existe(v.getFarmaceutico().getId())) {
            throw new RegraDeNegocioException("Farmacêutico da venda não está cadastrado.");
        }
        if (v.getCliente() != null && !clientes.existe(v.getCliente().getId())) {
            throw new RegraDeNegocioException("Cliente da venda não está cadastrado.");
        }
    }

    /** Venda não é editada por substituição: use adicionarItem / anexarReceita / finalizar / cancelar. */
    @Override
    public Venda atualizar(Venda venda) {
        throw new RegraDeNegocioException(
                "Venda não pode ser editada diretamente. Use adicionar item, anexar receita, finalizar ou cancelar.");
    }

    /** Venda finalizada é registro histórico (estoque já foi baixado): não pode ser excluída. */
    @Override
    protected void validarExclusao(Venda v) {
        if (v.getStatus() == StatusVenda.FINALIZADA) {
            throw new RegraDeNegocioException("Venda finalizada não pode ser excluída.");
        }
    }

    @Override
    public void adicionarItem(String vendaId, String medicamentoId, int quantidade) {
        Venda venda = buscarPorId(vendaId);
        Medicamento medicamento = medicamentos.buscarPorId(medicamentoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Medicamento", medicamentoId));
        venda.adicionarItem(medicamento, quantidade); // regras REQ14/15/16/18 estão no domínio
        vendas.atualizar(venda);
    }

    @Override
    public void anexarReceita(String vendaId, Long receitaId) {
        Venda venda = buscarPorId(vendaId);
        Receita receita = receitas.buscarPorId(receitaId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Receita", receitaId));
        venda.anexarReceita(receita);
        vendas.atualizar(venda);
    }

    @Override
    public BigDecimal finalizar(String vendaId, FormaPagamento forma) {
        Venda venda = buscarPorId(vendaId);
        venda.finalizar(forma); // baixa estoque e saldo da receita (objetos compartilhados em memória)
        vendas.atualizar(venda);
        return venda.calcularTotal();
    }

    @Override
    public void cancelar(String vendaId) {
        Venda venda = buscarPorId(vendaId);
        venda.cancelar();
        vendas.atualizar(venda);
    }
}

package farmasmart.servico;

import farmasmart.Cliente;
import farmasmart.Medico;
import farmasmart.Receita;
import farmasmart.repositorio.ReceitaRepositorio;
import farmasmart.repositorio.Repositorio;
import farmasmart.repositorio.VendaRepositorio;

public class ReceitaServicoImpl extends ServicoBase<Receita, Long> {

    private final Repositorio<Cliente, String> clientes;
    private final Repositorio<Medico, String> medicos;
    private final VendaRepositorio vendas;

    public ReceitaServicoImpl(ReceitaRepositorio receitas, Repositorio<Cliente, String> clientes,
                              Repositorio<Medico, String> medicos, VendaRepositorio vendas) {
        super(receitas, "Receita");
        this.clientes = clientes;
        this.medicos = medicos;
        this.vendas = vendas;
    }

    @Override
    protected Long obterId(Receita r) {
        return r.getId();
    }

    @Override
    protected void validar(Receita r) {
        exigirNaoNulo(r, "receita");
        exigirNaoNulo(r.getId(), "id");
        if (!clientes.existe(r.getCliente().getId())) {
            throw new RegraDeNegocioException("Cliente da receita não está cadastrado.");
        }
        if (!medicos.existe(r.getMedico().getId())) {
            throw new RegraDeNegocioException("Médico da receita não está cadastrado.");
        }
        if (r.getQuantidadeItens() == 0) {
            throw new RegraDeNegocioException("A receita precisa ter pelo menos um item prescrito.");
        }
    }

    @Override
    protected void validarExclusao(Receita r) {
        if (!vendas.buscarPorReceita(r.getId()).isEmpty()) {
            throw new RegraDeNegocioException("Receita está vinculada a vendas e não pode ser excluída.");
        }
    }
}

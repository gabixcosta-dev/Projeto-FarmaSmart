package farmasmart.servico;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import farmasmart.Medicamento;
import farmasmart.MedicamentoGenerico;
import farmasmart.StatusValidacao;
import farmasmart.repositorio.MedicamentoRepositorio;
import farmasmart.repositorio.VendaRepositorio;

public class MedicamentoServicoImpl extends ServicoBase<Medicamento, String> implements MedicamentoServico {

    private final MedicamentoRepositorio medicamentos;
    private final VendaRepositorio vendas;

    public MedicamentoServicoImpl(MedicamentoRepositorio medicamentos, VendaRepositorio vendas) {
        super(medicamentos, "Medicamento");
        this.medicamentos = medicamentos;
        this.vendas = vendas;
    }

    @Override
    protected String obterId(Medicamento m) {
        return m.getId();
    }

    @Override
    protected void validar(Medicamento m) {
        exigirNaoNulo(m, "medicamento");
        exigirTexto(m.getId(), "id");
        exigirTexto(m.getNome(), "nome");
        exigirTexto(m.getPrincipioAtivo(), "princípio ativo");
        if (m.getPreco() <= 0) {
            throw new RegraDeNegocioException("Preço deve ser maior que zero.");
        }
        if (m.getQuantidadeEstoque() < 0) {
            throw new RegraDeNegocioException("Estoque não pode ser negativo.");
        }
        exigirNaoNulo(m.getDataValidade(), "data de validade");
        if (m instanceof MedicamentoGenerico
                && ((MedicamentoGenerico) m).getMedicamentoReferencia() == null) {
            throw new RegraDeNegocioException("Genérico precisa de um medicamento de referência.");
        }
    }

    /** Se o princípio ativo não mudou, mantém a validação sanitária já registrada. */
    @Override
    protected Medicamento aoAtualizar(Medicamento antigo, Medicamento novo) {
        boolean mesmoPrincipio = antigo.getPrincipioAtivo().equalsIgnoreCase(novo.getPrincipioAtivo());
        StatusValidacao status = antigo.getValidacao().getStatus();
        if (mesmoPrincipio && status != StatusValidacao.PENDENTE) {
            novo.registrarValidacao(status, antigo.getValidacao().getOrgaoValidador());
        }
        return novo;
    }

    /** REQ20: medicamento que já tem venda registrada não pode ser excluído. */
    @Override
    protected void validarExclusao(Medicamento m) {
        if (!vendas.buscarPorMedicamento(m).isEmpty()) {
            throw new RegraDeNegocioException("Medicamento já possui vendas registradas e não pode ser excluído.");
        }
    }

    @Override
    public void registrarValidacao(String medicamentoId, StatusValidacao status, String orgao) {
        exigirNaoNulo(status, "status");
        Medicamento m = buscarPorId(medicamentoId);
        m.registrarValidacao(status, orgao);
        medicamentos.atualizar(m);
    }

    @Override
    public List<Medicamento> proximosDoVencimento(int dias) {
        LocalDate limite = LocalDate.now().plusDays(dias);
        return medicamentos.listarTodos().stream()
                .filter(m -> !m.isVencido() && !m.getDataValidade().isAfter(limite))
                .sorted(Comparator.comparing(Medicamento::getDataValidade))
                .collect(Collectors.toList());
    }
}

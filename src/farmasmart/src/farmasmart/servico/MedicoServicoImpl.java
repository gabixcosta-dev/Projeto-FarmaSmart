package farmasmart.servico;

import farmasmart.Medico;
import farmasmart.repositorio.ReceitaRepositorio;
import farmasmart.repositorio.Repositorio;

public class MedicoServicoImpl extends ServicoBase<Medico, String> {

    private final ReceitaRepositorio receitas;

    public MedicoServicoImpl(Repositorio<Medico, String> medicos, ReceitaRepositorio receitas) {
        super(medicos, "Médico");
        this.receitas = receitas;
    }

    @Override
    protected String obterId(Medico m) {
        return m.getId();
    }

    @Override
    protected void validar(Medico m) {
        exigirNaoNulo(m, "médico");
        exigirTexto(m.getId(), "id");
        exigirTexto(m.getNome(), "nome");
        exigirCpf(m.getCpf());
        exigirTexto(m.getCrm(), "CRM");
    }

    @Override
    protected void validarExclusao(Medico m) {
        if (!receitas.buscarPorMedico(m.getId()).isEmpty()) {
            throw new RegraDeNegocioException("Médico possui receitas emitidas e não pode ser excluído.");
        }
    }
}

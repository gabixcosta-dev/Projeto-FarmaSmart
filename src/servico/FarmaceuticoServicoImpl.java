package farmasmart.servico;

import farmasmart.Farmaceutico;
import farmasmart.repositorio.Repositorio;
import farmasmart.repositorio.VendaRepositorio;

public class FarmaceuticoServicoImpl extends ServicoBase<Farmaceutico, String> {

    private final VendaRepositorio vendas;

    public FarmaceuticoServicoImpl(Repositorio<Farmaceutico, String> farmaceuticos, VendaRepositorio vendas) {
        super(farmaceuticos, "Farmacêutico");
        this.vendas = vendas;
    }

    @Override
    protected String obterId(Farmaceutico f) {
        return f.getId();
    }

    @Override
    protected void validar(Farmaceutico f) {
        exigirNaoNulo(f, "farmacêutico");
        exigirTexto(f.getId(), "id");
        exigirTexto(f.getNome(), "nome");
        exigirCpf(f.getCpf());
        exigirTexto(f.getCrf(), "CRF");
    }

    @Override
    protected void validarExclusao(Farmaceutico f) {
        if (!vendas.buscarPorFarmaceutico(f.getId()).isEmpty()) {
            throw new RegraDeNegocioException("Farmacêutico possui vendas registradas e não pode ser excluído.");
        }
    }
}

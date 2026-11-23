package farmasmart.repositorio;

import farmasmart.Farmaceutico;

public class FarmaceuticoRepositorioMemoriaImpl extends RepositorioMemoriaGenerico<Farmaceutico, String> {

    private static final long serialVersionUID = 1L;

    @Override
    protected String obterId(Farmaceutico f) {
        return f.getId();
    }
}

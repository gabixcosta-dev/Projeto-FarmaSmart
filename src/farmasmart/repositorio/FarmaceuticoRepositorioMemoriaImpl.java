package farmasmart.repositorio;

import farmasmart.Farmaceutico;

public class FarmaceuticoRepositorioMemoriaImpl extends RepositorioMemoriaGenerico<Farmaceutico, String> {

    @Override
    protected String obterId(Farmaceutico f) {
        return f.getId();
    }
}

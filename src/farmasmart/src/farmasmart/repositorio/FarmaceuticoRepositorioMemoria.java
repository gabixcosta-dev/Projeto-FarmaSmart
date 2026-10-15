package farmasmart.repositorio;

import farmasmart.Farmaceutico;

public class FarmaceuticoRepositorioMemoria extends RepositorioMemoria<Farmaceutico, String> {

    @Override
    protected String obterId(Farmaceutico f) {
        return f.getId();
    }
}

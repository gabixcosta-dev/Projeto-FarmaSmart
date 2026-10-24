package farmasmart.repositorio;

import farmasmart.Medico;

public class MedicoRepositorioMemoria extends RepositorioMemoria<Medico, String> {

    @Override
    protected String obterId(Medico m) {
        return m.getId();
    }
}

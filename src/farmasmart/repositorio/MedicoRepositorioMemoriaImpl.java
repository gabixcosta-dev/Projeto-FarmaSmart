package farmasmart.repositorio;

import farmasmart.Medico;

public class MedicoRepositorioMemoriaImpl extends RepositorioMemoriaGenerico<Medico, String> {

    @Override
    protected String obterId(Medico m) {
        return m.getId();
    }
}

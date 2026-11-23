package farmasmart.repositorio;

import farmasmart.Medico;

public class MedicoRepositorioMemoriaImpl extends RepositorioMemoriaGenerico<Medico, String> {

    private static final long serialVersionUID = 1L;

    @Override
    protected String obterId(Medico m) {
        return m.getId();
    }
}

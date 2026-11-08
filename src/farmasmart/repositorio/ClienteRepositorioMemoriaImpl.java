package farmasmart.repositorio;

import java.util.Optional;

import farmasmart.Cliente;

public class ClienteRepositorioMemoriaImpl extends RepositorioMemoriaGenerico<Cliente, String>
        implements ClienteRepositorio {

    @Override
    protected String obterId(Cliente c) {
        return c.getId();
    }

    @Override
    public Optional<Cliente> buscarPorCpf(String cpf) {
        String procurado = somenteDigitos(cpf);
        return dados.values().stream()
                .filter(c -> somenteDigitos(c.getCpf()).equals(procurado))
                .findFirst();
    }

    private static String somenteDigitos(String texto) {
        return texto == null ? "" : texto.replaceAll("\\D", "");
    }
}

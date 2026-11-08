package farmasmart.repositorio;

import java.util.Optional;

import farmasmart.Cliente;

public interface ClienteRepositorio extends Repositorio<Cliente, String> {

    Optional<Cliente> buscarPorCpf(String cpf);
}

package farmasmart.servico;

import java.time.LocalDate;

import farmasmart.Cliente;
import farmasmart.repositorio.ClienteRepositorio;
import farmasmart.repositorio.ReceitaRepositorio;
import farmasmart.repositorio.VendaRepositorio;

public class ClienteServicoImpl extends ServicoBase<Cliente, String> {

    private final ClienteRepositorio clientes;
    private final VendaRepositorio vendas;
    private final ReceitaRepositorio receitas;

    public ClienteServicoImpl(ClienteRepositorio clientes, VendaRepositorio vendas, ReceitaRepositorio receitas) {
        super(clientes, "Cliente");
        this.clientes = clientes;
        this.vendas = vendas;
        this.receitas = receitas;
    }

    @Override
    protected String obterId(Cliente c) {
        return c.getId();
    }

    @Override
    protected void validar(Cliente c) {
        exigirNaoNulo(c, "cliente");
        exigirTexto(c.getId(), "id");
        exigirTexto(c.getNome(), "nome");
        exigirCpf(c.getCpf());
        exigirNaoNulo(c.getDataNascimento(), "data de nascimento");
        if (c.getDataNascimento().isAfter(LocalDate.now())) {
            throw new RegraDeNegocioException("Data de nascimento não pode estar no futuro.");
        }
        // CPF único: ignora o próprio cliente (caso de atualização)
        clientes.buscarPorCpf(c.getCpf())
                .filter(outro -> !outro.getId().equals(c.getId()))
                .ifPresent(outro -> {
                    throw new RegraDeNegocioException("Já existe cliente com este CPF (id " + outro.getId() + ").");
                });
    }

    @Override
    protected void validarExclusao(Cliente c) {
        if (!vendas.buscarPorCliente(c.getId()).isEmpty()) {
            throw new RegraDeNegocioException("Cliente possui vendas registradas e não pode ser excluído.");
        }
        if (!receitas.buscarPorCliente(c.getId()).isEmpty()) {
            throw new RegraDeNegocioException("Cliente possui receitas cadastradas e não pode ser excluído.");
        }
    }
}

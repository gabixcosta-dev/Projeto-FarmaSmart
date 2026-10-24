package farmasmart.visao;

import farmasmart.Cliente;
import farmasmart.servico.Servico;

public class TelaCliente extends TelaCrud<Cliente, String> {

    public TelaCliente(Servico<Cliente, String> servico, Entrada entrada) {
        super(servico, entrada);
    }

    @Override
    public String titulo() {
        return "Clientes";
    }

    @Override
    protected String lerId() {
        return entrada.lerTexto("Id do cliente");
    }

    @Override
    protected Cliente lerNovo() {
        return ler(entrada.lerTexto("Id"));
    }

    @Override
    protected Cliente lerAtualizado(Cliente atual) {
        System.out.println("Digite os novos dados (o id não muda).");
        return ler(atual.getId());
    }

    private Cliente ler(String id) {
        String nome = entrada.lerTexto("Nome");
        String cpf = entrada.lerTexto("CPF (11 dígitos)");
        return new Cliente(id, nome, cpf, entrada.lerData("Data de nascimento"));
    }

    @Override
    protected String descrever(Cliente c) {
        return String.format("%s | %s | CPF: %s | nascimento: %s",
                c.getId(), c.getNome(), c.getCpf(), c.getDataNascimento());
    }
}

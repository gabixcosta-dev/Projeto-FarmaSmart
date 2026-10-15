package farmasmart.visao;

import java.time.LocalDate;

import farmasmart.Cliente;
import farmasmart.Medico;
import farmasmart.Receita;
import farmasmart.TipoReceita;
import farmasmart.servico.Servico;

public class TelaReceita extends TelaCrud<Receita, Long> {

    private final Servico<Cliente, String> clientes;
    private final Servico<Medico, String> medicos;

    public TelaReceita(Servico<Receita, Long> receitas, Servico<Cliente, String> clientes,
                       Servico<Medico, String> medicos, Entrada entrada) {
        super(receitas, entrada);
        this.clientes = clientes;
        this.medicos = medicos;
    }

    @Override
    public String titulo() {
        return "Receitas";
    }

    @Override
    protected Long lerId() {
        return entrada.lerLong("Id da receita");
    }

    @Override
    protected Receita lerNovo() {
        return ler(entrada.lerLong("Id"));
    }

    @Override
    protected Receita lerAtualizado(Receita atual) {
        System.out.println("Digite os novos dados, inclusive os itens (o id não muda).");
        return ler(atual.getId());
    }

    private Receita ler(Long id) {
        Cliente cliente = clientes.buscarPorId(entrada.lerTexto("Id do cliente (paciente)"));
        Medico medico = medicos.buscarPorId(entrada.lerTexto("Id do médico"));
        LocalDate emissao = entrada.lerData("Data de emissão");
        TipoReceita tipo = entrada.lerEnum("Tipo da receita", TipoReceita.values());

        Receita receita = new Receita(id, cliente, medico, emissao, tipo);
        System.out.println("Itens prescritos (deixe o princípio ativo em branco para terminar):");
        while (true) {
            String principio = entrada.lerTexto("  Princípio ativo");
            if (principio.isEmpty()) {
                break;
            }
            receita.adicionarItem(principio, entrada.lerInt("  Quantidade prescrita"));
        }
        return receita;
    }

    @Override
    protected String descrever(Receita r) {
        return String.format("Receita %d | paciente: %s | médico: %s (CRM %s) | emissão: %s | limite: %s | tipo: %s | itens: %d | válida hoje: %s",
                r.getId(), r.getCliente().getNome(), r.getMedico().getNome(), r.getMedico().getCrm(),
                r.getDataEmissao(), r.getDataLimite(), r.getTipo(), r.getQuantidadeItens(),
                r.isValida(LocalDate.now()) ? "sim" : "não");
    }
}

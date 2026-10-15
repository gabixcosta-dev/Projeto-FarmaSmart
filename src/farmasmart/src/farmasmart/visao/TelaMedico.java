package farmasmart.visao;

import farmasmart.Medico;
import farmasmart.servico.Servico;

public class TelaMedico extends TelaCrud<Medico, String> {

    public TelaMedico(Servico<Medico, String> servico, Entrada entrada) {
        super(servico, entrada);
    }

    @Override
    public String titulo() {
        return "Médicos";
    }

    @Override
    protected String lerId() {
        return entrada.lerTexto("Id do médico");
    }

    @Override
    protected Medico lerNovo() {
        return ler(entrada.lerTexto("Id"));
    }

    @Override
    protected Medico lerAtualizado(Medico atual) {
        System.out.println("Digite os novos dados (o id não muda).");
        return ler(atual.getId());
    }

    private Medico ler(String id) {
        String nome = entrada.lerTexto("Nome");
        String cpf = entrada.lerTexto("CPF (11 dígitos)");
        Medico medico = new Medico(id, nome, cpf);
        medico.setCrm(entrada.lerTexto("CRM"));
        return medico;
    }

    @Override
    protected String descrever(Medico m) {
        return String.format("%s | %s | CPF: %s | CRM: %s", m.getId(), m.getNome(), m.getCpf(), m.getCrm());
    }
}

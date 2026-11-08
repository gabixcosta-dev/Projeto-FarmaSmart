package farmasmart.visao;

import farmasmart.Farmaceutico;
import farmasmart.servico.Servico;

public class TelaFarmaceutico extends TelaCrud<Farmaceutico, String> {

    public TelaFarmaceutico(Servico<Farmaceutico, String> servico, Entrada entrada) {
        super(servico, entrada);
    }

    @Override
    public String titulo() {
        return "Farmacêuticos";
    }

    @Override
    protected String lerId() {
        return entrada.lerTexto("Id do farmacêutico");
    }

    @Override
    protected Farmaceutico lerNovo() {
        return ler(entrada.lerTexto("Id"));
    }

    @Override
    protected Farmaceutico lerAtualizado(Farmaceutico atual) {
        System.out.println("Digite os novos dados (o id não muda).");
        return ler(atual.getId());
    }

    private Farmaceutico ler(String id) {
        String nome = entrada.lerTexto("Nome");
        String cpf = entrada.lerTexto("CPF (11 dígitos)");
        Farmaceutico f = new Farmaceutico(id, nome, cpf);
        f.setCrf(entrada.lerTexto("CRF"));
        return f;
    }

    @Override
    protected String descrever(Farmaceutico f) {
        return String.format("%s | %s | CPF: %s | CRF: %s", f.getId(), f.getNome(), f.getCpf(), f.getCrf());
    }
}

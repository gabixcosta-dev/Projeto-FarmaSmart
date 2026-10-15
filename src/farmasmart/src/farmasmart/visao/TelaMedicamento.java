package farmasmart.visao;

import java.time.LocalDate;

import farmasmart.Medicamento;
import farmasmart.MedicamentoComum;
import farmasmart.MedicamentoControlado;
import farmasmart.MedicamentoGenerico;
import farmasmart.NivelRisco;
import farmasmart.StatusValidacao;
import farmasmart.servico.MedicamentoServico;
import farmasmart.servico.RegraDeNegocioException;

public class TelaMedicamento extends TelaCrud<Medicamento, String> {

    private final MedicamentoServico medicamentos;

    public TelaMedicamento(MedicamentoServico medicamentos, Entrada entrada) {
        super(medicamentos, entrada);
        this.medicamentos = medicamentos;
    }

    @Override
    public String titulo() {
        return "Medicamentos";
    }

    @Override
    protected String lerId() {
        return entrada.lerTexto("Id do medicamento");
    }

    @Override
    protected Medicamento lerNovo() {
        int tipo = lerTipo();
        return ler(entrada.lerTexto("Id"), tipo);
    }

    @Override
    protected Medicamento lerAtualizado(Medicamento atual) {
        System.out.println("Digite os novos dados (o tipo e o id não mudam).");
        int tipo = atual instanceof MedicamentoControlado ? 3 : atual instanceof MedicamentoGenerico ? 2 : 1;
        return ler(atual.getId(), tipo);
    }

    private int lerTipo() {
        while (true) {
            int tipo = entrada.lerInt("Tipo: 1) Comum  2) Genérico  3) Controlado");
            if (tipo >= 1 && tipo <= 3) {
                return tipo;
            }
            System.out.println("Tipo inválido.");
        }
    }

    private Medicamento ler(String id, int tipo) {
        String nome = entrada.lerTexto("Nome comercial");
        String principio = entrada.lerTexto("Princípio ativo");
        double preco = entrada.lerDouble("Preço");
        int estoque = entrada.lerInt("Quantidade em estoque");
        LocalDate validade = entrada.lerData("Data de validade");

        if (tipo == 2) {
            String idRef = entrada.lerTexto("Id do medicamento COMUM de referência");
            Medicamento ref = medicamentos.buscarPorId(idRef);
            if (!(ref instanceof MedicamentoComum)) {
                throw new RegraDeNegocioException("O medicamento de referência precisa ser do tipo Comum.");
            }
            return new MedicamentoGenerico(id, nome, principio, preco, estoque, validade, (MedicamentoComum) ref);
        }
        if (tipo == 3) {
            NivelRisco nivel = entrada.lerEnum("Nível de risco / lista de controle", NivelRisco.values());
            return new MedicamentoControlado(id, nome, principio, preco, estoque, validade, nivel);
        }
        return new MedicamentoComum(id, nome, principio, preco, estoque, validade);
    }

    @Override
    protected String descrever(Medicamento m) {
        String tipo = m instanceof MedicamentoControlado ? "CONTROLADO"
                : m instanceof MedicamentoGenerico ? "GENERICO" : "COMUM";
        return String.format("[%s] %s | %s | PA: %s | R$ %.2f | estoque: %d | validade: %s | validação: %s",
                tipo, m.getId(), m.getNome(), m.getPrincipioAtivo(), m.getPreco(),
                m.getQuantidadeEstoque(), m.getDataValidade(), m.getValidacao().getStatus());
    }

    @Override
    protected void imprimirExtras() {
        System.out.println("6) Registrar validação sanitária   7) Próximos do vencimento (60 dias)");
    }

    @Override
    protected boolean tratarExtra(int opcao) {
        if (opcao == 6) {
            String id = lerId();
            StatusValidacao status = entrada.lerEnum("Resultado da validação", StatusValidacao.values());
            String orgao = entrada.lerTexto("Órgão validador (ex.: openFDA)");
            medicamentos.registrarValidacao(id, status, orgao);
            System.out.println("Validação registrada.");
            return true;
        }
        if (opcao == 7) {
            var lista = medicamentos.proximosDoVencimento(60);
            if (lista.isEmpty()) {
                System.out.println("(nenhum medicamento vence nos próximos 60 dias)");
            }
            lista.forEach(m -> System.out.println(descrever(m)));
            return true;
        }
        return false;
    }
}

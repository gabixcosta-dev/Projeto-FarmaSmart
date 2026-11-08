package farmasmart.visao;

import java.math.BigDecimal;
import java.math.RoundingMode;

import farmasmart.Cliente;
import farmasmart.Farmaceutico;
import farmasmart.FormaPagamento;
import farmasmart.Venda;
import farmasmart.servico.RegraDeNegocioException;
import farmasmart.servico.Servico;
import farmasmart.servico.VendaServico;

public class TelaVenda extends TelaCrud<Venda, String> {

    private final VendaServico vendas;
    private final Servico<Farmaceutico, String> farmaceuticos;
    private final Servico<Cliente, String> clientes;

    public TelaVenda(VendaServico vendas, Servico<Farmaceutico, String> farmaceuticos,
                     Servico<Cliente, String> clientes, Entrada entrada) {
        super(vendas, entrada);
        this.vendas = vendas;
        this.farmaceuticos = farmaceuticos;
        this.clientes = clientes;
    }

    @Override
    public String titulo() {
        return "Vendas";
    }

    @Override
    protected String lerId() {
        return entrada.lerTexto("Id da venda");
    }

    /** "Cadastrar" aqui significa ABRIR a venda. */
    @Override
    protected Venda lerNovo() {
        String id = entrada.lerTexto("Id da venda");
        Farmaceutico farmaceutico = farmaceuticos.buscarPorId(entrada.lerTexto("Id do farmacêutico"));
        String idCliente = entrada.lerTexto("Id do cliente (vazio = venda sem cliente)");
        Cliente cliente = idCliente.isEmpty() ? null : clientes.buscarPorId(idCliente);
        return new Venda(id, farmaceutico, cliente);
    }

    @Override
    protected Venda lerAtualizado(Venda atual) {
        throw new RegraDeNegocioException(
                "Venda não pode ser editada diretamente. Use as opções 6 a 9.");
    }

    @Override
    protected String descrever(Venda v) {
        return String.format("Venda %s | %s | %s | cliente: %s | farmacêutico: %s | itens: %d | receita: %s | total: R$ %s | pagamento: %s",
                v.getId(), v.getData(), v.getStatus(),
                v.getCliente() == null ? "(balcão)" : v.getCliente().getNome(),
                v.getFarmaceutico().getNome(), v.getQuantidadeItens(),
                v.getReceita() == null ? "-" : String.valueOf(v.getReceita().getId()),
                v.calcularTotal().setScale(2, RoundingMode.HALF_UP),
                v.getFormaPagamento() == null ? "-" : v.getFormaPagamento());
    }

    @Override
    protected void imprimirExtras() {
        System.out.println("6) Adicionar item   7) Anexar receita   8) Finalizar   9) Cancelar");
    }

    @Override
    protected boolean tratarExtra(int opcao) {
        switch (opcao) {
            case 6:
                vendas.adicionarItem(lerId(), entrada.lerTexto("Id do medicamento"), entrada.lerInt("Quantidade"));
                System.out.println("Item adicionado.");
                return true;
            case 7:
                vendas.anexarReceita(lerId(), entrada.lerLong("Id da receita"));
                System.out.println("Receita anexada.");
                return true;
            case 8:
                String id = lerId();
                FormaPagamento forma = entrada.lerEnum("Forma de pagamento", FormaPagamento.values());
                BigDecimal total = vendas.finalizar(id, forma);
                System.out.println("Venda finalizada. Total: R$ " + total.setScale(2, RoundingMode.HALF_UP));
                return true;
            case 9:
                vendas.cancelar(lerId());
                System.out.println("Venda cancelada.");
                return true;
            default:
                return false;
        }
    }
}

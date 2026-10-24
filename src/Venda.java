package farmasmart;
 
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
 
public class Venda {
 
    private String id;
    private LocalDate data;
    private StatusVenda status;
    private FormaPagamento formaPagamento;
    private Cliente cliente;
    private Farmaceutico farmaceutico;
    private Receita receita;
    private List<ItemVenda> itens = new ArrayList<>();
 
    public Venda(String id, Farmaceutico farmaceutico, Cliente cliente) {
        if (id == null || farmaceutico == null) {
            throw new IllegalArgumentException("Id e farmacêutico são obrigatórios.");
        }
        this.id = id;
        this.farmaceutico = farmaceutico;
        this.cliente = cliente;
        this.data = LocalDate.now();
        this.status = StatusVenda.ABERTA;
    }
 
    public void anexarReceita(Receita receita) {
        exigirAberta();
        if (receita == null) {
            throw new IllegalArgumentException("Receita é obrigatória.");
        }
        if (cliente == null || !cliente.equals(receita.getCliente())) {
            throw new IllegalStateException("A receita não pertence ao cliente desta venda.");
        }
        this.receita = receita;
    }
 
    public void adicionarItem(Medicamento medicamento, int quantidade) {
        exigirAberta();
        if (medicamento == null) {
            throw new IllegalArgumentException("Medicamento é obrigatório.");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva.");
        }
 
        ItemVenda existente = buscarItem(medicamento);
        int quantidadeDoMedicamento = (existente == null ? 0 : existente.getQuantidade()) + quantidade;
        int quantidadeDoPrincipio = quantidadePorPrincipioAtivo(medicamento.getPrincipioAtivo()) + quantidade;
 
        validarElegibilidade(medicamento, quantidadeDoMedicamento, quantidadeDoPrincipio);
 
        if (existente != null) {
            existente.somarQuantidade(quantidade);
        } else {
            itens.add(new ItemVenda(medicamento, quantidade)); // a venda cria o item (composição)
        }
    }
 
    public BigDecimal calcularTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemVenda item : itens) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }
 
    public void finalizar(FormaPagamento forma) {
        exigirAberta();
        if (itens.isEmpty()) {
            throw new IllegalStateException("Venda sem itens.");
        }
        if (forma == null) {
            throw new IllegalArgumentException("Forma de pagamento é obrigatória.");
        }
 
        for (ItemVenda i : itens) {
            validarElegibilidade(i.getMedicamento(), i.getQuantidade(),
                    quantidadePorPrincipioAtivo(i.getMedicamento().getPrincipioAtivo()));
        }
        for (ItemVenda i : itens) {
            i.getMedicamento().baixaEstoque(i.getQuantidade());
            if (i.getMedicamento().exigeReceita()) {
                receita.registrarDispensacao(i.getMedicamento(), i.getQuantidade());
            }
        }
        this.formaPagamento = forma;
        this.status = StatusVenda.FINALIZADA;
    }
 
    public void cancelar() {
        exigirAberta();
        this.status = StatusVenda.CANCELADA;
    }
 
    private void validarElegibilidade(Medicamento m, int quantidadeDoMedicamento, int quantidadeDoPrincipio) {
        LocalDate hoje = LocalDate.now();
 
        if (m.isVencido()) {
            throw new IllegalStateException("Venda bloqueada: " + m.getNome() + " está vencido.");
        }
        if (!m.isValidoParaVenda()) {
            throw new IllegalStateException("Venda bloqueada: " + m.getNome()
                    + " sem validação sanitária confirmada ou sem estoque.");
        }
        if (quantidadeDoMedicamento > m.getQuantidadeEstoque()) {
            throw new IllegalStateException("Estoque insuficiente de " + m.getNome() + ".");
        }
        if (m.exigeReceita()) {
            if (cliente == null) {
                throw new IllegalStateException("Controlado exige cliente identificado.");
            }
            if (receita == null) {
                throw new IllegalStateException("Controlado exige receita.");
            }
            if (!receita.cobre(m, quantidadeDoPrincipio, hoje)) {
                throw new IllegalStateException("Receita inválida, vencida, de tipo incorreto ou sem saldo para "
                        + m.getNome() + ".");
            }
        }
    }
 
    private void exigirAberta() {
        if (status != StatusVenda.ABERTA) {
            throw new IllegalStateException("Venda não está aberta.");
        }
    }
 
    private ItemVenda buscarItem(Medicamento m) {
        for (ItemVenda i : itens) {
            if (i.getMedicamento().equals(m)) {
                return i;
            }
        }
        return null;
    }
 
    private int quantidadePorPrincipioAtivo(String principioAtivo) {
        int total = 0;
        for (ItemVenda i : itens) {
            if (i.getMedicamento().getPrincipioAtivo().equalsIgnoreCase(principioAtivo)) {
                total += i.getQuantidade();
            }
        }
        return total;
    }
 
    public String getId() { return id; }
    public LocalDate getData() { return data; }
    public StatusVenda getStatus() { return status; }
    public FormaPagamento getFormaPagamento() { return formaPagamento; }
    public Cliente getCliente() { return cliente; }
    public Farmaceutico getFarmaceutico() { return farmaceutico; }
    public Receita getReceita() { return receita; }
    public int getQuantidadeItens() { return itens.size(); }
}
 

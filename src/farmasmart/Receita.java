package farmasmart;
 
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Receita {
 
    private static final int PRAZO_PADRAO_DIAS = 30;
 
    private Long id;
    private Cliente cliente;
    private Medico medico;
    private LocalDate dataEmissao;
    private int prazoValidadeDias;
    private TipoReceita tipo;
    private List<ItemReceita> itens = new ArrayList<>();
 
    public Receita(Long id, Cliente cliente, Medico medico, LocalDate dataEmissao, TipoReceita tipo) {
        this(id, cliente, medico, dataEmissao, tipo, PRAZO_PADRAO_DIAS);
    }
 
    public Receita(Long id, Cliente cliente, Medico medico, LocalDate dataEmissao,
                    TipoReceita tipo, int prazoValidadeDias) {
        if (id == null || cliente == null || medico == null || dataEmissao == null || tipo == null) {
            throw new IllegalArgumentException("Id, cliente, médico, data de emissão e tipo são obrigatórios.");
        }
        if (prazoValidadeDias <= 0) {
            throw new IllegalArgumentException("Prazo de validade deve ser positivo.");
        }
        this.id = id;
        this.cliente = cliente;
        this.medico = medico;
        this.dataEmissao = dataEmissao;
        this.tipo = tipo;
        this.prazoValidadeDias = prazoValidadeDias;
    }
 
    /** A receita cria o próprio item (composição) — informe o princípio ativo prescrito. */
    public void adicionarItem(String principioAtivo, int quantidade) {
        itens.add(new ItemReceita(principioAtivo, quantidade));
    }
 
    public LocalDate getDataLimite() {
        return dataEmissao.plusDays(prazoValidadeDias);
    }
 
    /** REQ15: válida dentro do prazo, com pelo menos um item e algum saldo restante. */
    public boolean isValida(LocalDate hoje) {
        boolean dentroDoPrazo = !hoje.isBefore(dataEmissao) && !hoje.isAfter(getDataLimite());
        boolean temSaldo = false;
        for (ItemReceita item : itens) {
            if (item.getQuantidadeRestante() > 0) {
                temSaldo = true;
                break;
            }
        }
        return dentroDoPrazo && !itens.isEmpty() && temSaldo;
    }
 
    /**
     * A receita cobre a compra se: está válida, o tipo de receita é aceito
     * pelo medicamento, o princípio ativo consta nela e há saldo suficiente.
     */
    public boolean cobre(Medicamento medicamento, int quantidade, LocalDate hoje) {
        if (!isValida(hoje)) {
            return false;
        }
        if (!medicamento.aceitaTipoReceita(tipo)) {
            return false;
        }
        ItemReceita item = buscarItem(medicamento.getPrincipioAtivo());
        return item != null && quantidade <= item.getQuantidadeRestante();
    }
 
    /** Package-private: só Venda (mesmo pacote) baixa o saldo da receita. */
    void registrarDispensacao(Medicamento medicamento, int quantidade) {
        ItemReceita item = buscarItem(medicamento.getPrincipioAtivo());
        if (item == null) {
            throw new IllegalStateException("Princípio ativo não consta na receita.");
        }
        item.registrarDispensacao(quantidade);
    }
 
    private ItemReceita buscarItem(String principioAtivo) {
        for (ItemReceita i : itens) {
            if (i.getPrincipioAtivo().equalsIgnoreCase(principioAtivo)) {
                return i;
            }
        }
        return null;
    }
 
    public Long getId() { return id; }
    public LocalDate getDataEmissao() { return dataEmissao; }
    public int getPrazoValidadeDias() { return prazoValidadeDias; }
    public TipoReceita getTipo() { return tipo; }
    public Cliente getCliente() { return cliente; }
    public Medico getMedico() { return medico; }
    public int getQuantidadeItens() { return itens.size(); }
    // ItemReceita é package-private, então a lista de itens não é exposta publicamente.
}
 

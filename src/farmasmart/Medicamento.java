package farmasmart;

import java.time.LocalDate;

public abstract class Medicamento {
  
  protected String id;
  protected String nome;
  protected String principioAtivo;
  protected double preco;
  protected int quantidadeEstoque;
  protected LocalDate dataValidade;
  protected String tipo;
  private ValidacaoSanitaria validacao;

  protected Medicamento(String id, String nome, String principioAtivo, double preco,
                        int quantidadeEstoque, LocalDate dataValidade, String tipo) {
    this.id = id;
    this.nome = nome;
    this.principioAtivo = principioAtivo;
    this.preco = preco;
    this.quantidadeEstoque = quantidadeEstoque;
    this.dataValidade = dataValidade;
    this.tipo = tipo;
    this.validacao = new ValidacaoSanitaria();
  }

  public void registrarValidacao(StatusValidacao status, String orgao) {
    this.validacao.atualizarStatus(status, orgao);
  }
  
  public boolean isVencido() {
    return this.dataValidade.isBefore(LocalDate.now());
  }

  public boolean isValidoParaVenda() {
    return !isVencido() && this.validacao.isConfirmado() && this.quantidadeEstoque > 0;
  }

  public void baixaEstoque(int qtd) {
    if (qtd <= 0) {
      throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
    }
    if (qtd > this.quantidadeEstoque) {
      throw new IllegalStateException("Estoque insuficiente para " + this.nome + ".");
    }
    this.quantidadeEstoque -= qtd;
  }

  public String getId() {
    return id;
  }

  public String getNome() {
    return nome;
  }

  public String getPrincipioAtivo() {
    return principioAtivo;
  }

  public double getPreco() {
    return preco;
  }

  public int getQuantidadeEstoque() {
    return quantidadeEstoque;
  }

  public LocalDate getDataValidade() {
    return dataValidade;
  }

  public String getTipo() {
    return tipo;
  }

  public ValidacaoSanitaria getValidacao() {
    return validacao;
  }
}
  
  
  

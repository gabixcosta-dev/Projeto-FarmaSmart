package farmasmart;

import java.io.Serializable;

public abstract class Pessoa implements Serializable {

  private static final long serialVersionUID = 1L;

  protected String id;
  protected String nome;
  protected String cpf;

  protected Pessoa(String id, String nome, String cpf) {
    this.id = id;
    this.nome = nome;
    this.cpf = cpf;
    
  }
  
  public String getId() {
    return id;
  }

  public String getNome() {
    return nome;
  }

  public String getCpf() {
    return cpf;
  }
}

    

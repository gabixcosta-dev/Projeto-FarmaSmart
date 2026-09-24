package farmasmart;

import java.time.LocalDate;

public class Cliente extends Pessoa {
  
  private LocalDate dataNascimento;
  
  public Cliente(String id, String nome, String cpf, LocalDate dataNascimento) {
    super(id, nome, cpf);
    this.dataNascimento = dataNascimento;
  }

  public LocalDate getDataNascimento() {
    return dataNascimento;
  }
}

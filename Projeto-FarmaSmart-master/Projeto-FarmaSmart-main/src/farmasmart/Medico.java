package farmasmart;

public class Medico extends Pessoa {
  private String crm;

  public Medico(String id, String nome, String cpf) {
    super(id, nome, cpf);
  }

  public String getCrm() {
    return crm;
  }

  public void setCrm(String crm) {
    this.crm = crm;
  }
}

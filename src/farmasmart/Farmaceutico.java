package farmasmart;

public class Farmaceutico extends Pessoa {

  private String crf;

  public Farmaceutico(String id, String nome, String cpf) {
    super(id, nome, cpf);
  }

  public String getCrf() {
    return crf;
  }

  public void setCrf(String crf) {
    this.crf = crf;
  }
}

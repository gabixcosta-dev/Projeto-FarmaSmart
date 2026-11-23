package farmasmart;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ValidacaoSanitaria implements Serializable {

  private static final long serialVersionUID = 1L;

  StatusValidacao status;
  String orgaoValidador;
  LocalDateTime dataValidacao;

  ValidacaoSanitaria() {
    this.status = StatusValidacao.PENDENTE;
    this.orgaoValidador = null;
    this.dataValidacao = null;
  }

  void atualizarStatus(StatusValidacao status, String orgao) {
    this.status = status;
    this.orgaoValidador = orgao;
    this.dataValidacao = LocalDateTime.now();
  }

  boolean isConfirmado() {
    return this.status == StatusValidacao.CONFIRMADO;
  }

  public StatusValidacao getStatus() {
    return status;
  }

  public String getOrgaoValidador() {
    return orgaoValidador;
  }

  public LocalDateTime getDataValidacao() {
    return dataValidacao;
  }
}
      

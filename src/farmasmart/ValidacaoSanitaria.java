package farmasmart;

import java.time.LocalDateTime;

public class ValidacaoSanitaria {
  
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
      

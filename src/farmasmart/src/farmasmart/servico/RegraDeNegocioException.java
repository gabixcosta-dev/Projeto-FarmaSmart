package farmasmart.servico;

/** Violação de uma regra de negócio (ex.: excluir medicamento que já foi vendido). */
public class RegraDeNegocioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RegraDeNegocioException(String mensagem) {
        super(mensagem);
    }
}

package farmasmart.servico;

public class EntidadeNaoEncontradaException extends RegraDeNegocioException {

    private static final long serialVersionUID = 1L;

    public EntidadeNaoEncontradaException(String entidade, Object id) {
        super(entidade + " não encontrado(a) com id " + id + ".");
    }
}

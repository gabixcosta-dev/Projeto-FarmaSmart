package farmasmart.servico;

import java.util.List;

import farmasmart.repositorio.Repositorio;

public abstract class ServicoBase<T, ID> implements Servico<T, ID> {

    protected final Repositorio<T, ID> repositorio;
    private final String nomeEntidade;

    protected ServicoBase(Repositorio<T, ID> repositorio, String nomeEntidade) {
        this.repositorio = repositorio;
        this.nomeEntidade = nomeEntidade;
    }


    protected abstract ID obterId(T entidade);

    /** Valida os dados da entidade; lança RegraDeNegocioException se inválida. */
    protected abstract void validar(T entidade);

    /** Regras que impedem a exclusão. */
    protected void validarExclusao(T entidade) {
    }

    /** Permite ajustar o objeto novo antes de substituir o antigo. */
    protected T aoAtualizar(T antigo, T novo) {
        return novo;
    }

    // CRUD pronto 

    @Override
    public T cadastrar(T entidade) {
        validar(entidade);
        ID id = obterId(entidade);
        if (repositorio.existe(id)) {
            throw new RegraDeNegocioException(nomeEntidade + " já cadastrado(a) com id " + id + ".");
        }
        return repositorio.salvar(entidade);
    }

    @Override
    public T buscarPorId(ID id) {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException(nomeEntidade, id));
    }

    @Override
    public List<T> listar() {
        return repositorio.listarTodos();
    }

    @Override
    public T atualizar(T entidade) {
        validar(entidade);
        T antigo = buscarPorId(obterId(entidade));
        return repositorio.atualizar(aoAtualizar(antigo, entidade));
    }

    @Override
    public void excluir(ID id) {
        T entidade = buscarPorId(id);
        validarExclusao(entidade);
        repositorio.remover(id);
    }

    // utilitários de validação

    protected static void exigirTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new RegraDeNegocioException("O campo '" + campo + "' é obrigatório.");
        }
    }

    protected static void exigirCpf(String cpf) {
        exigirTexto(cpf, "CPF");
        if (cpf.replaceAll("\\D", "").length() != 11) {
            throw new RegraDeNegocioException("CPF deve ter 11 dígitos.");
        }
    }

    protected static void exigirNaoNulo(Object valor, String campo) {
        if (valor == null) {
            throw new RegraDeNegocioException("O campo '" + campo + "' é obrigatório.");
        }
    }
}

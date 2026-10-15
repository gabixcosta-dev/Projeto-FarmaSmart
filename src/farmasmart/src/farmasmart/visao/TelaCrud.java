package farmasmart.visao;

import java.util.List;

import farmasmart.servico.RegraDeNegocioException;
import farmasmart.servico.Servico;

/**
 * Classe ABSTRATA com o menu de CRUD completo (listar, buscar, cadastrar,
 * atualizar, excluir). Cada tela concreta só diz COMO ler e COMO mostrar
 * a sua entidade. A tela depende apenas da interface Servico.
 */
public abstract class TelaCrud<T, ID> {

    protected final Servico<T, ID> servico;
    protected final Entrada entrada;

    protected TelaCrud(Servico<T, ID> servico, Entrada entrada) {
        this.servico = servico;
        this.entrada = entrada;
    }

    // ------------------ ganchos que cada tela implementa ------------------

    public abstract String titulo();

    protected abstract T lerNovo();

    protected abstract T lerAtualizado(T atual);

    protected abstract String descrever(T entidade);

    protected abstract ID lerId();

    /** Opções extras do menu (numeradas a partir de 6). */
    protected void imprimirExtras() {
    }

    /** Devolve true se tratou a opção extra. */
    protected boolean tratarExtra(int opcao) {
        return false;
    }

    // ------------------------------ menu pronto ------------------------------

    public final void executar() {
        while (true) {
            System.out.println("\n=== " + titulo() + " ===");
            System.out.println("1) Listar   2) Buscar por id   3) Cadastrar   4) Atualizar   5) Excluir");
            imprimirExtras();
            System.out.println("0) Voltar");
            int opcao = entrada.lerInt("Opção");
            if (opcao == 0) {
                return;
            }
            try {
                switch (opcao) {
                    case 1:
                        listar();
                        break;
                    case 2:
                        System.out.println(descrever(servico.buscarPorId(lerId())));
                        break;
                    case 3:
                        servico.cadastrar(lerNovo());
                        System.out.println("Cadastrado com sucesso.");
                        break;
                    case 4:
                        atualizar();
                        break;
                    case 5:
                        servico.excluir(lerId());
                        System.out.println("Excluído com sucesso.");
                        break;
                    default:
                        if (!tratarExtra(opcao)) {
                            System.out.println("Opção inválida.");
                        }
                }
            } catch (RegraDeNegocioException | IllegalArgumentException | IllegalStateException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private void listar() {
        List<T> todos = servico.listar();
        if (todos.isEmpty()) {
            System.out.println("(nenhum registro)");
            return;
        }
        todos.forEach(e -> System.out.println(descrever(e)));
    }

    private void atualizar() {
        T atual = servico.buscarPorId(lerId());
        System.out.println("Atual: " + descrever(atual));
        servico.atualizar(lerAtualizado(atual));
        System.out.println("Atualizado com sucesso.");
    }
}

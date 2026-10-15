package farmasmart.visao;

import java.util.NoSuchElementException;

public class MenuPrincipal {

    private final Entrada entrada;
    private final TelaCrud<?, ?>[] telas;

    public MenuPrincipal(Entrada entrada, TelaCrud<?, ?>... telas) {
        this.entrada = entrada;
        this.telas = telas;
    }

    public void executar() {
        try {
            while (true) {
                System.out.println("\n===== FARMASMART =====");
                for (int i = 0; i < telas.length; i++) {
                    System.out.println((i + 1) + ") " + telas[i].titulo());
                }
                System.out.println("0) Sair");
                int opcao = entrada.lerInt("Opção");
                if (opcao == 0) {
                    break;
                }
                if (opcao >= 1 && opcao <= telas.length) {
                    telas[opcao - 1].executar();
                } else {
                    System.out.println("Opção inválida.");
                }
            }
        } catch (NoSuchElementException fimDaEntrada) {
            // entrada encerrada (Ctrl+D / fim do arquivo): sai normalmente
        }
        System.out.println("Até logo!");
    }
}

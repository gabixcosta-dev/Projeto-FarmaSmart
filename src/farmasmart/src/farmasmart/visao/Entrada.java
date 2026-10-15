package farmasmart.visao;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/** Leitura segura do teclado: repete a pergunta enquanto o valor digitado for inválido. */
public class Entrada {

    private final Scanner scanner = new Scanner(System.in);

    public String lerTexto(String rotulo) {
        System.out.print(rotulo + ": ");
        return scanner.nextLine().trim();
    }

    public int lerInt(String rotulo) {
        while (true) {
            try {
                return Integer.parseInt(lerTexto(rotulo));
            } catch (NumberFormatException e) {
                System.out.println("Digite um número inteiro válido.");
            }
        }
    }

    public long lerLong(String rotulo) {
        while (true) {
            try {
                return Long.parseLong(lerTexto(rotulo));
            } catch (NumberFormatException e) {
                System.out.println("Digite um número inteiro válido.");
            }
        }
    }

    public double lerDouble(String rotulo) {
        while (true) {
            try {
                return Double.parseDouble(lerTexto(rotulo).replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Digite um número válido (ex.: 12.50).");
            }
        }
    }

    public LocalDate lerData(String rotulo) {
        while (true) {
            try {
                return LocalDate.parse(lerTexto(rotulo + " (aaaa-mm-dd)"));
            } catch (DateTimeParseException e) {
                System.out.println("Data inválida. Use o formato aaaa-mm-dd.");
            }
        }
    }

    public <E extends Enum<E>> E lerEnum(String rotulo, E[] valores) {
        System.out.println(rotulo + ":");
        for (int i = 0; i < valores.length; i++) {
            System.out.println("  " + (i + 1) + ") " + valores[i]);
        }
        while (true) {
            int escolha = lerInt("Escolha");
            if (escolha >= 1 && escolha <= valores.length) {
                return valores[escolha - 1];
            }
            System.out.println("Opção inválida.");
        }
    }

    public boolean confirmar(String pergunta) {
        return lerTexto(pergunta + " (s/n)").equalsIgnoreCase("s");
    }
}

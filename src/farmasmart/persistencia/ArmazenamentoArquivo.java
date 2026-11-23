package farmasmart.persistencia;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ArmazenamentoArquivo {

    private final Path arquivo;

    public ArmazenamentoArquivo(Path arquivo) {
        this.arquivo = arquivo;
    }

    public Dados carregar() {
        if (!Files.exists(arquivo)) {
            return new Dados();
        }
        try (ObjectInputStream entrada = new ObjectInputStream(
                new BufferedInputStream(Files.newInputStream(arquivo)))) {
            return (Dados) entrada.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException erro) {
            Path guardado = guardarArquivoInvalido();
            throw new PersistenciaException("Não foi possível ler o arquivo de dados (" + erro
                    + "). O arquivo antigo foi guardado em " + guardado + ".", erro);
        }
    }

    public void salvar(Dados dados) {
        Path temporario = arquivo.resolveSibling(arquivo.getFileName() + ".tmp");
        try {
            Path pasta = arquivo.toAbsolutePath().getParent();
            Files.createDirectories(pasta);
            try (ObjectOutputStream saida = new ObjectOutputStream(
                    new BufferedOutputStream(Files.newOutputStream(temporario)))) {
                saida.writeObject(dados);
            }
            Files.move(temporario, arquivo, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException erro) {
            throw new PersistenciaException("Não foi possível salvar os dados em " + arquivo.toAbsolutePath()
                    + " (" + erro + "). As alterações feitas agora podem se perder ao fechar o programa.",
                    erro);
        }
    }

    private Path guardarArquivoInvalido() {
        Path guardado = arquivo.resolveSibling(arquivo.getFileName() + ".invalido");
        try {
            Files.move(arquivo, guardado, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException erro) {
            throw new PersistenciaException("O arquivo de dados está ilegível e não foi possível movê-lo: "
                    + erro.getMessage(), erro);
        }
        return guardado;
    }
}

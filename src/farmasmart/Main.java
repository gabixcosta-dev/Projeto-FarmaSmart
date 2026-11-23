package farmasmart;

import farmasmart.persistencia.ArmazenamentoArquivo;
import farmasmart.persistencia.Dados;
import farmasmart.persistencia.PersistenciaException;
import farmasmart.repositorio.ClienteRepositorioMemoriaImpl;
import farmasmart.repositorio.FarmaceuticoRepositorioMemoriaImpl;
import farmasmart.repositorio.MedicamentoRepositorioMemoriaImpl;
import farmasmart.repositorio.MedicoRepositorioMemoriaImpl;
import farmasmart.repositorio.ReceitaRepositorioMemoriaImpl;
import farmasmart.repositorio.VendaRepositorioMemoriaImpl;
import farmasmart.servico.ClienteServicoImpl;
import farmasmart.servico.FarmaceuticoServicoImpl;
import farmasmart.servico.MedicamentoServicoImpl;
import farmasmart.servico.MedicoServicoImpl;
import farmasmart.servico.ReceitaServicoImpl;
import farmasmart.servico.VendaServicoImpl;
import farmasmart.visao.FarmaSmartController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.nio.file.Path;

public class Main extends Application {

    private static final Path ARQUIVO_DADOS = Path.of("dados", "farmasmart.dat");

    @Override
    public void start(Stage stage) throws Exception {
        ArmazenamentoArquivo armazenamento = new ArmazenamentoArquivo(ARQUIVO_DADOS);
        Dados dados = carregarDados(armazenamento);

        MedicamentoRepositorioMemoriaImpl repMedicamentos = dados.getMedicamentos();
        ClienteRepositorioMemoriaImpl repClientes = dados.getClientes();
        MedicoRepositorioMemoriaImpl repMedicos = dados.getMedicos();
        FarmaceuticoRepositorioMemoriaImpl repFarmaceuticos = dados.getFarmaceuticos();
        ReceitaRepositorioMemoriaImpl repReceitas = dados.getReceitas();
        VendaRepositorioMemoriaImpl repVendas = dados.getVendas();

        MedicamentoServicoImpl srvMedicamentos = new MedicamentoServicoImpl(repMedicamentos, repVendas);
        ClienteServicoImpl srvClientes = new ClienteServicoImpl(repClientes, repVendas, repReceitas);
        MedicoServicoImpl srvMedicos = new MedicoServicoImpl(repMedicos, repReceitas);
        FarmaceuticoServicoImpl srvFarmaceuticos = new FarmaceuticoServicoImpl(repFarmaceuticos, repVendas);
        ReceitaServicoImpl srvReceitas = new ReceitaServicoImpl(repReceitas, repClientes, repMedicos, repVendas);
        VendaServicoImpl srvVendas = new VendaServicoImpl(repVendas, repFarmaceuticos, repClientes,
                repMedicamentos, repReceitas);

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/farmasmart/visao/principal.fxml"));
        Parent root = loader.load();
        FarmaSmartController controller = loader.getController();
        controller.configurar(srvClientes, srvFarmaceuticos, srvMedicos, srvMedicamentos, srvReceitas, srvVendas,
                dados.getGerador(), () -> armazenamento.salvar(dados));

        Scene scene = new Scene(root, 1280, 800);
        stage.setTitle("FarmaSmart | Gestão farmacêutica");
        stage.setMinWidth(980);
        stage.setMinHeight(680);
        stage.setScene(scene);
        stage.show();
    }

    private Dados carregarDados(ArmazenamentoArquivo armazenamento) {
        try {
            return armazenamento.carregar();
        } catch (PersistenciaException erro) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Dados não carregados");
            alerta.setHeaderText("Não foi possível ler os dados salvos. O sistema começará vazio.");
            alerta.setContentText(erro.getMessage());
            alerta.showAndWait();
            return new Dados();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

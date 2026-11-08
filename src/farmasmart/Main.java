package farmasmart;

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
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        MedicamentoRepositorioMemoriaImpl repMedicamentos = new MedicamentoRepositorioMemoriaImpl();
        ClienteRepositorioMemoriaImpl repClientes = new ClienteRepositorioMemoriaImpl();
        MedicoRepositorioMemoriaImpl repMedicos = new MedicoRepositorioMemoriaImpl();
        FarmaceuticoRepositorioMemoriaImpl repFarmaceuticos = new FarmaceuticoRepositorioMemoriaImpl();
        ReceitaRepositorioMemoriaImpl repReceitas = new ReceitaRepositorioMemoriaImpl();
        VendaRepositorioMemoriaImpl repVendas = new VendaRepositorioMemoriaImpl();

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
        controller.configurar(srvClientes, srvFarmaceuticos, srvMedicos, srvMedicamentos, srvReceitas, srvVendas);

        Scene scene = new Scene(root, 1280, 800);
        stage.setTitle("FarmaSmart | Gestão farmacêutica");
        stage.setMinWidth(980);
        stage.setMinHeight(680);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

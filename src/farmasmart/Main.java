package farmasmart;

import farmasmart.repositorio.ClienteRepositorioMemoria;
import farmasmart.repositorio.FarmaceuticoRepositorioMemoria;
import farmasmart.repositorio.MedicamentoRepositorioMemoria;
import farmasmart.repositorio.MedicoRepositorioMemoria;
import farmasmart.repositorio.ReceitaRepositorioMemoria;
import farmasmart.repositorio.VendaRepositorioMemoria;
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
        MedicamentoRepositorioMemoria repMedicamentos = new MedicamentoRepositorioMemoria();
        ClienteRepositorioMemoria repClientes = new ClienteRepositorioMemoria();
        MedicoRepositorioMemoria repMedicos = new MedicoRepositorioMemoria();
        FarmaceuticoRepositorioMemoria repFarmaceuticos = new FarmaceuticoRepositorioMemoria();
        ReceitaRepositorioMemoria repReceitas = new ReceitaRepositorioMemoria();
        VendaRepositorioMemoria repVendas = new VendaRepositorioMemoria();

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

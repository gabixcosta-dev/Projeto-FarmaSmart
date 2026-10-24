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
import farmasmart.visao.Entrada;
import farmasmart.visao.MenuPrincipal;
import farmasmart.visao.TelaCliente;
import farmasmart.visao.TelaFarmaceutico;
import farmasmart.visao.TelaMedicamento;
import farmasmart.visao.TelaMedico;
import farmasmart.visao.TelaReceita;
import farmasmart.visao.TelaVenda;

public class Main {

    public static void main(String[] args) {
        // Camada de repositório
        MedicamentoRepositorioMemoria repMedicamentos = new MedicamentoRepositorioMemoria();
        ClienteRepositorioMemoria repClientes = new ClienteRepositorioMemoria();
        MedicoRepositorioMemoria repMedicos = new MedicoRepositorioMemoria();
        FarmaceuticoRepositorioMemoria repFarmaceuticos = new FarmaceuticoRepositorioMemoria();
        ReceitaRepositorioMemoria repReceitas = new ReceitaRepositorioMemoria();
        VendaRepositorioMemoria repVendas = new VendaRepositorioMemoria();

        // Camada de serviço
        MedicamentoServicoImpl srvMedicamentos = new MedicamentoServicoImpl(repMedicamentos, repVendas);
        ClienteServicoImpl srvClientes = new ClienteServicoImpl(repClientes, repVendas, repReceitas);
        MedicoServicoImpl srvMedicos = new MedicoServicoImpl(repMedicos, repReceitas);
        FarmaceuticoServicoImpl srvFarmaceuticos = new FarmaceuticoServicoImpl(repFarmaceuticos, repVendas);
        ReceitaServicoImpl srvReceitas = new ReceitaServicoImpl(repReceitas, repClientes, repMedicos, repVendas);
        VendaServicoImpl srvVendas = new VendaServicoImpl(repVendas, repFarmaceuticos, repClientes,
                repMedicamentos, repReceitas);

        // Camada de visão
        Entrada entrada = new Entrada();
        new MenuPrincipal(entrada,
                new TelaMedicamento(srvMedicamentos, entrada),
                new TelaCliente(srvClientes, entrada),
                new TelaMedico(srvMedicos, entrada),
                new TelaFarmaceutico(srvFarmaceuticos, entrada),
                new TelaReceita(srvReceitas, srvClientes, srvMedicos, entrada),
                new TelaVenda(srvVendas, srvFarmaceuticos, srvClientes, entrada)
        ).executar();
    }
}

package farmasmart.persistencia;

import java.io.Serializable;

import farmasmart.repositorio.ClienteRepositorioMemoriaImpl;
import farmasmart.repositorio.FarmaceuticoRepositorioMemoriaImpl;
import farmasmart.repositorio.MedicamentoRepositorioMemoriaImpl;
import farmasmart.repositorio.MedicoRepositorioMemoriaImpl;
import farmasmart.repositorio.ReceitaRepositorioMemoriaImpl;
import farmasmart.repositorio.VendaRepositorioMemoriaImpl;

public class Dados implements Serializable {

    private static final long serialVersionUID = 1L;

    private final ClienteRepositorioMemoriaImpl clientes = new ClienteRepositorioMemoriaImpl();
    private final FarmaceuticoRepositorioMemoriaImpl farmaceuticos = new FarmaceuticoRepositorioMemoriaImpl();
    private final MedicoRepositorioMemoriaImpl medicos = new MedicoRepositorioMemoriaImpl();
    private final MedicamentoRepositorioMemoriaImpl medicamentos = new MedicamentoRepositorioMemoriaImpl();
    private final ReceitaRepositorioMemoriaImpl receitas = new ReceitaRepositorioMemoriaImpl();
    private final VendaRepositorioMemoriaImpl vendas = new VendaRepositorioMemoriaImpl();
    private final GeradorId gerador = new GeradorId();

    public ClienteRepositorioMemoriaImpl getClientes() {
        return clientes;
    }

    public FarmaceuticoRepositorioMemoriaImpl getFarmaceuticos() {
        return farmaceuticos;
    }

    public MedicoRepositorioMemoriaImpl getMedicos() {
        return medicos;
    }

    public MedicamentoRepositorioMemoriaImpl getMedicamentos() {
        return medicamentos;
    }

    public ReceitaRepositorioMemoriaImpl getReceitas() {
        return receitas;
    }

    public VendaRepositorioMemoriaImpl getVendas() {
        return vendas;
    }

    public GeradorId getGerador() {
        return gerador;
    }
}

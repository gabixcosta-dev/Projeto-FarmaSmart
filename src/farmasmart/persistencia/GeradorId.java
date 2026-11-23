package farmasmart.persistencia;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class GeradorId implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Map<String, Long> ultimos = new HashMap<>();

    public long proximoNumero(String tipo) {
        long proximo = ultimos.getOrDefault(tipo, 0L) + 1;
        ultimos.put(tipo, proximo);
        return proximo;
    }

    public String proximoCodigo(String prefixo) {
        return String.format("%s-%04d", prefixo, proximoNumero(prefixo));
    }
}

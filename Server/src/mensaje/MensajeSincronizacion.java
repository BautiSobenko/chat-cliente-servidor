package mensaje;

import java.util.ArrayList;

public class MensajeSincronizacion extends Mensaje {

    private final String msg = "SINCRONIZACION";
    private ArrayList<clienteConectado> registros = new ArrayList<clienteConectado>();
    private ArrayList<clienteConectado> conexiones = new ArrayList<clienteConectado>();

    public MensajeSincronizacion(ArrayList<clienteConectado> registros, ArrayList<clienteConectado> conexiones) {
        this.registros = registros;
        this.conexiones = conexiones;
    }

    public ArrayList<clienteConectado> getRegistros() {
        return registros;
    }

    public ArrayList<clienteConectado> getConexiones() {
        return conexiones;
    }
}

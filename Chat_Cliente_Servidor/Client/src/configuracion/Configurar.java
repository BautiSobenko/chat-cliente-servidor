package configuracion;

import java.net.UnknownHostException;

public interface Configurar {

    boolean validarConfiguracion(Object... args)throws UnknownHostException;
    void escribirArchivoConfiguracion(Object... args)throws Exception;
    void leerArchivoConfiguracion(Object... args);
    String[] getParametros();
}

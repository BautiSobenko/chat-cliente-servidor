package conexion;

import mensaje.Mensaje;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface IConexion {

    public void establecerConexion(Object... args);

    public void aceptarConexion();

    public void cerrarConexion();

    public void cerrarServer();

    public void crearConexionEnvio(Object... args) throws IOException;

    public ObjectInputStream getInputStreamConexion();

    public ObjectOutputStream getOutputStreamConexion();

    public void enviaMensaje(Mensaje mensaje);

}

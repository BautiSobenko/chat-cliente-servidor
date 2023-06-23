package servidor;

import mensaje.Mensaje;

import java.io.IOException;

public interface Emision {

    public void enviaMensaje(String ip, int puerto, Mensaje msg) throws IOException;
}

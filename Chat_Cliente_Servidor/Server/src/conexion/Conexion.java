package conexion;

import mensaje.Mensaje;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Conexion implements IConexion {

    ServerSocket serverSocket;
    Socket socket;
    int puertoOrigen;
    int puertoDestino;

    @Override
    public void establecerConexion(Object... args) {
        try {
            int puertoOrigen = (int) args[0];
            this.serverSocket = new ServerSocket(puertoOrigen);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ObjectInputStream getInputStreamConexion() {
        try {
            return new ObjectInputStream( this.socket.getInputStream() );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void aceptarConexion() {
        try {
            this.socket = this.serverSocket.accept();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cerrarConexion() {
        try {
            this.socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cerrarServer() {
        try {
            this.serverSocket.close();
        } catch (IOException e) {
        	e.printStackTrace();
        }
    }

    @Override
    public void crearConexionEnvio(Object... args) throws IOException {
        String ipServer = (String) args[0];
        int puertoServidor = (int) args[1];
        this.socket = new Socket(ipServer,puertoServidor);

    }

    @Override
    public void enviaMensaje(Mensaje mensaje) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(this.getSocket().getOutputStream());

        out.writeObject(mensaje);
    }

    public int getPuertoOrigen() {
        return puertoOrigen;
    }

    public int getPuertoDestino() {
        return puertoDestino;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public Socket getSocket() {
        return socket;
    }

}

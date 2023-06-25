package conexion;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import mensaje.Mensaje;

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
            throw new RuntimeException(e);
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
    public ObjectOutputStream getOutputStreamConexion() {
        try {
            return new ObjectOutputStream( this.socket.getOutputStream() );
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
            throw new RuntimeException(e);
        }
    }

    @Override
    public void crearConexionEnvio(Object... args) throws IOException {
        String ipServer = (String) args[0];
        int puertoServidor = (int) args[1];
        this.socket = new Socket(ipServer,puertoServidor);

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
    
    public void enviaMensaje(Mensaje mensaje) {
        try {
        	ObjectOutputStream out = getOutputStreamConexion();//new ObjectOutputStream(sCliente.getOutputStream());
			out.writeObject(mensaje);
			out.close();
			cerrarConexion();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }

}

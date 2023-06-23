package servidor;

import java.io.ObjectOutputStream;
import java.util.Timer;
import java.util.TimerTask;

import conexion.Conexion;
import mensaje.Mensaje;

public class Heartbeat implements Runnable{
    private static final int INTERVALO_HEARTBEAT = 1000; // Intervalo de tiempo entre cada heartbeat en milisegundos
    private final Servidor server;
    
    public Heartbeat(Servidor server) {
		super();
		this.server = server;
	}

	@Override
	public void run() {
		Timer timer = new Timer();
		HeartbeatTask task = new HeartbeatTask();
		task.server = this.server;
        timer.schedule(task, 0, INTERVALO_HEARTBEAT);
    }
	
	
	static class HeartbeatTask extends TimerTask {
        ObjectOutputStream out;
        private final int puertoMonitor = 5555; ///Actualizar el puerto del monitor con el que elijamos
        public Servidor server;
        public Conexion conexion;
        
        public void run() {
            // Le mando mensaje al monitor
        	conexion = new Conexion();
        	try {
	        	this.conexion.crearConexionEnvio("localhost", this.puertoMonitor);
	            out = new ObjectOutputStream(this.conexion.getSocket().getOutputStream());
	            Mensaje mensajeClienteServidor = new Mensaje();
	            mensajeClienteServidor.setMensaje(Integer.toString(server.getPuertoServer()));
	            out.writeObject(mensajeClienteServidor);
	            this.conexion.cerrarConexion();
        	}
        	catch(Exception ignored) {}
        }
    }
}

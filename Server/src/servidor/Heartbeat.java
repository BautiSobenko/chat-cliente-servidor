package servidor;

import java.io.ObjectOutputStream;
import java.util.Timer;
import java.util.TimerTask;

import conexion.Conexion;
import mensaje.Mensaje;

public class Heartbeat implements Runnable{
    private static final int INTERVALO_HEARTBEAT = 3000; // Intervalo de tiempo entre cada heartbeat en milisegundos
    private int puerto;
    private Servidor server;
    
    public Heartbeat(Servidor server,int puerto) {
		super();
		this.server = server;
		this.puerto = puerto;
	}

	@Override
	public void run() {
		Timer timer = new Timer();
		HeartbeatTask task = new HeartbeatTask();
		task.server = this.server;
		task.puerto = this.puerto;
        timer.schedule(task, 0, INTERVALO_HEARTBEAT);
    }
	
	
	static class HeartbeatTask extends TimerTask {
        ObjectOutputStream out;
        private int puertoMonitor = 5555; ///Actualizar el puerto del monitor con el que elijamos
        public Servidor server;
        public Conexion conexion;
        public int puerto;
        
        public void run() {
            // Le mando mensaje al monitor
        	conexion = server.getConexion();
        	try {
	        	this.conexion.crearConexionEnvio("localhost", this.puertoMonitor);
	            out = new ObjectOutputStream(this.conexion.getSocket().getOutputStream());
	            Mensaje mensajeClienteServidor = new Mensaje();
	            mensajeClienteServidor.setMensaje(Integer.toString(puerto));
	            out.writeObject(mensajeClienteServidor);
	            out.close();
	            this.conexion.cerrarConexion();
        	}
        	catch(Exception ex) {
        		//e.printStackTrace();
        	}
        }
    }
}

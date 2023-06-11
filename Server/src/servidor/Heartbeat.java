package servidor;

import java.io.ObjectOutputStream;
import java.util.Timer;
import java.util.TimerTask;

import conexion.Conexion;
import mensaje.Mensaje;

public class Heartbeat implements Runnable{
    private static final int INTERVALO_HEARTBEAT = 5000; // Intervalo de tiempo entre cada heartbeat en milisegundos
    private Conexion conexion;
    private int puerto;
    
    public Heartbeat(Conexion conexion,int puerto) {
		super();
		this.conexion = conexion;
		this.puerto = puerto;
	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		Timer timer = new Timer();
		HeartbeatTask task = new HeartbeatTask();
		task.conexion = this.conexion;
		task.puerto = this.puerto;
        timer.schedule(task, 0, INTERVALO_HEARTBEAT);
    }
	
	
	static class HeartbeatTask extends TimerTask {
        ObjectOutputStream out;
        private int puertoMonitor = 3333; ///Actualizar el puerto del monitor con el que elijamos
        public Conexion conexion;
        public int puerto;
        
        public void run() {
            // Le mando mensaje al monitor
        	try {
	        	this.conexion.crearConexionEnvio("localhost", this.puertoMonitor);
	            out = new ObjectOutputStream(this.conexion.getSocket().getOutputStream());
	            Mensaje mensajeClienteServidor = new Mensaje();
	            mensajeClienteServidor.setMensaje(Integer.toString(puerto));
	            out.writeObject(mensajeClienteServidor);
	            this.conexion.cerrarConexion();
        	}
        	catch(Exception ignored) {
        		//e.printStackTrace();
        	}
        }
    }
}

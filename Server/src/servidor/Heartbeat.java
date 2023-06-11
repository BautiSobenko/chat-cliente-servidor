package servidor;

import java.io.ObjectOutputStream;
import java.util.Timer;
import java.util.TimerTask;

import conexion.Conexion;
import mensaje.Mensaje;

public class Heartbeat implements Runnable{
    private static final int INTERVALO_HEARTBEAT = 5000; // Intervalo de tiempo entre cada heartbeat en milisegundos
    private Conexion conexion;
    
    public Heartbeat(Conexion conexion) {
		super();
		this.conexion = conexion;
	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		Timer timer = new Timer();
        timer.schedule(new HeartbeatTask(), 0, INTERVALO_HEARTBEAT);
        
        // El resto de la lógica de tu aplicación aquí...
        
        // Ejemplo: Esperar 30 segundos antes de finalizar la aplicación
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Finalizar la aplicación
        timer.cancel();
        System.out.println("Aplicación finalizada");
    }
	
	
	static class HeartbeatTask extends TimerTask {
        ObjectOutputStream out;
        private int puertoMonitor = 3333;
        public Conexion conexion;
        
        public void run() {
            // Le mando mensaje al monitor
        	this.conexion.crearConexionEnvio("localhost", this.puertoMonitor);
            out = new ObjectOutputStream(this.conexion.getSocket().getOutputStream());
            Mensaje mensajeClienteServidor = new Mensaje();
            mensajeClienteServidor.setMensaje("InicioServidor");
            out.writeObject(mensajeClienteServidor);
            this.conexion.cerrarConexion();
        }
    }
}

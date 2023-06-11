package monitor;

import conexion.Conexion;
import mensaje.Mensaje;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Timer;
import java.util.TimerTask;

public class Monitor implements Runnable{

	private static final int INTERVALO_HEARTBEAT = 5000;
	public static final int puerto = 1111;
    public Conexion conexionServer;

    public Monitor(){
    }

    @Override
    public void run() {

        try {

            this.conexionServer = new Conexion();
            //ServerSocket
            conexionServer.establecerConexion(puerto);
            
            Mensaje mensaje;
            String msg;
            int puertoRecibido;
            
    		Timer timer = new Timer();
    		HeartbeatCheckTask task = new HeartbeatCheckTask();
            timer.schedule(task, 0, INTERVALO_HEARTBEAT);
            
            while (true){

                this.conexionServer.aceptarConexion();
                mensaje = this.recibeMensaje();
                msg = mensaje.getMensaje();
                puertoRecibido = Integer.parseInt(msg);

                if (puertoRecibido == 9090)
                	task.reciboHeartBeatUno();
                else
                	task.reciboHeartBeatDos();
            }

        } catch (Exception e) {
            System.out.println("entro exc");
        }

    }

    public Mensaje recibeMensaje() {
        ObjectInputStream in = this.conexionServer.getInputStreamConexion();
        try {
            return (Mensaje) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    static class HeartbeatCheckTask extends TimerTask {
        public boolean heartbeatServidorUno = false;
        public boolean heartbeatServidorDos = false;
        
        @Override
        public void run() {
            if (heartbeatServidorUno) {
                System.out.println("Servidor Uno Activo");
                // Realizar la acción correspondiente cuando se recibe el heartbeat correctamente
                
                // Reiniciar la variable para el siguiente chequeo
                heartbeatServidorUno = false;
            } else {
                System.out.println("Servidor Uno perdido");
                // Realizar la acción correspondiente cuando se pierde el heartbeat
            }
            
            if (heartbeatServidorDos) {
                System.out.println("Servidor Dos Activo");
                // Realizar la acción correspondiente cuando se recibe el heartbeat correctamente
                
                // Reiniciar la variable para el siguiente chequeo
                heartbeatServidorDos = false;
            } else {
                System.out.println("Servidor Dos perdido");
                // Realizar la acción correspondiente cuando se pierde el heartbeat
            }
        }
        
        public void reciboHeartBeatUno() {
        	this.heartbeatServidorUno=true;
        }
        
        public void reciboHeartBeatDos() {
        	this.heartbeatServidorDos = true;
    }
  }


}
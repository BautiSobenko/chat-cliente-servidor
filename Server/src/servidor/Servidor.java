package servidor;

import conexion.Conexion;
import configuracion.ConfiguracionServer;
import mensaje.Mensaje;
import mensaje.clienteConectado;
import vista.vistas.VistaServidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

public class Servidor implements Runnable, Recepcion, Emision {

	private static Servidor servidor = null;

    private final VistaServidor vistaServidor;

    private int puertoServer;

    private ArrayList<clienteConectado> registros = new ArrayList<clienteConectado>();
    private ArrayList<clienteConectado> conexiones = new ArrayList<clienteConectado>();

    private Conexion conexion;

    private Servidor(){

        this.vistaServidor = new VistaServidor();

        Thread hiloServer = new Thread(this);
        hiloServer.start();
        
    }
    
    public static Servidor getServer(){
        if( servidor == null ){
            servidor = new Servidor();
        }
        return servidor;
    }

    @Override
    public void run() {

        String ipOrigen = null;
        int puertoOrigen = 0;
        String nicknameOrigen = null;
        ObjectOutputStream out;
        
        try {

            this.conexion = new Conexion();
            
            //Verifico si esta el otro activo
            try {
                // Existe un 9090 (Primario) en ejecucion?
                this.conexion.crearConexionEnvio("localhost", 9090);
                out = new ObjectOutputStream(this.conexion.getSocket().getOutputStream());
                Mensaje mensajeClienteServidor = new Mensaje();
                mensajeClienteServidor.setMensaje("estas?");
                out.writeObject(mensajeClienteServidor);
                this.conexion.cerrarConexion();
                
                // Si se envia el mensaje, es porque hay un Primario, entonces soy Secundario
                this.puertoServer = 8888;
                this.vistaServidor.muestraMensaje("Servidor Iniciado! \nPuerto: " + this.puertoServer + "\n");
                this.vistaServidor.muestraMensaje("Soy servidor secundario \n\n");
                conexion.establecerConexion(8888);
                
            }
            catch(Exception e) {
                // Si no encontre el 9090, soy Primario
            	this.puertoServer = 9090;
            	this.vistaServidor.muestraMensaje("Servidor Iniciado! \nPuerto: " + this.puertoServer + "\n");
            	this.vistaServidor.muestraMensaje("Soy servidor primario \n\n");
                conexion.establecerConexion(9090);
            }
            
            //Inicio el heartbeat
            Heartbeat hb = new Heartbeat(this,this.puertoServer);
            Thread hiloHeartbeat = new Thread(hb);
            hiloHeartbeat.start();

            String ipDestino;
            String nicknameDestino;
            String msg;
            Mensaje mensaje;
            int puertoDestino;
            
            //Si soy Secundario, le solicito las listas al primario
            if(this.puertoServer == 8888) {
                this.conexion.crearConexionEnvio("localhost", 9090);
                out = new ObjectOutputStream(this.conexion.getSocket().getOutputStream());
                Mensaje mensajeClienteServidor = new Mensaje();
                mensajeClienteServidor.setMensaje("InicioServidor");
                out.writeObject(mensajeClienteServidor);
                this.conexion.cerrarConexion();
            }
            
            while (true) {

                conexion.aceptarConexion();
        
               mensaje = this.recibeMensaje();
               msg = mensaje.getMensaje();
                
               if(msg.equals("estas?")) {
            	   
               }
               else if ( msg.equals("SOS PRIMARIO") ) {
            	    this.puertoServer = 9090;
                	this.vistaServidor.muestraMensaje("PASE A SER PRIMARIO \n\n");
                	this.vistaServidor.muestraMensaje("Puerto: " + this.puertoServer + "\n");
                	//Establezco conexion en el 9090
                	conexion.cerrarServer();
                	conexion.establecerConexion(9090);
            	}
                else if ( msg.equals("SINCRONIZACION") ) {
                    this.conexiones = mensaje.getConectados();
                    this.registros = mensaje.getRegistrados();   
            	}
                else if(msg.equals("InicioServidor")) {
            		this.sincronizacionRedundancia();
                }
                else {

                    puertoDestino = mensaje.getPuertoDestino();
                    ipDestino = mensaje.getIpDestino();
                    nicknameDestino = mensaje.getNicknameDestino();

                    puertoOrigen = mensaje.getPuertoOrigen();
                    ipOrigen = mensaje.getIpOrigen();
                    nicknameOrigen = mensaje.getNicknameOrigen();

                  if (msg.equals("ELIMINA REGISTRO")) {
                        this.eliminaRegistro(ipOrigen, puertoOrigen);
                        this.vistaServidor.muestraMensaje("BAJA CLIENTE: " + nicknameOrigen + " | " + ipOrigen + " | " + puertoOrigen + "\n\n");
                  }else {

                        if (msg.equalsIgnoreCase("LLAMADA ACEPTADA")) {
                            this.agregaConectado(ipOrigen, puertoOrigen, nicknameOrigen);
                            this.agregaConectado(ipDestino, puertoDestino, nicknameDestino);
                            this.sincronizacionRedundancia();
                        } else if (msg.equalsIgnoreCase("DESCONECTAR")) {
                            this.eliminaConectado(ipOrigen, puertoOrigen);
                            this.eliminaConectado(ipDestino, puertoDestino);
                            this.sincronizacionRedundancia();
                            mensaje.setConectados(this.getClientesFueraDeSesion());
                        } else if (msg.equalsIgnoreCase("REGISTRO")) {

                            if (this.registrarCliente(ipOrigen, puertoOrigen, nicknameOrigen)) {
                                msg = "REGISTRO EXITOSO";
                                mensaje.setConectados(this.getClientesFueraDeSesion());
                                this.sincronizacionRedundancia();
                            } else
                                msg = "REGISTRO FALLIDO";

                            mensaje.setMensaje(msg);
                        } else if (msg.equalsIgnoreCase("RECARGAR CONECTADOS")) {
                            mensaje.setConectados(this.getClientesFueraDeSesion());
                        } else if ((msg.equalsIgnoreCase("LLAMADA") && verificaConexion(ipDestino, puertoDestino))) {
                            msg = "OCUPADO";
                            mensaje.setMensaje(msg);
                        }

                        this.vistaServidor.muestraMensaje("ORIGEN: " + ipOrigen + " => DESTINO: " + ipDestino + " :\n" + msg + "\n\n");

                        //Reenvio del mensaje
                        this.conexion.crearConexionEnvio(ipDestino, puertoDestino);

                        out = new ObjectOutputStream(this.conexion.getSocket().getOutputStream());

                        out.writeObject(mensaje);

                        this.conexion.cerrarConexion();
                    }


            }

        }
        }catch (IOException e) {
            try {

                Mensaje mensaje = new Mensaje();
                mensaje.setMensaje("ERROR LLAMADA");

                this.conexion.crearConexionEnvio(ipOrigen, puertoOrigen);

                out = new ObjectOutputStream(this.conexion.getSocket().getOutputStream());
                out.writeObject(mensaje);

                this.vistaServidor.muestraMensaje("ERROR EN CONEXION: "+ nicknameOrigen +" | "+ ipOrigen + " | " + puertoOrigen + "\n\n");

                this.conexion.cerrarConexion();

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }


        }

    }

    /*
    Sincronizo al modificar Registros
    Sincronizo al modificar Conexiones
     */
    public void sincronizacionRedundancia() {

        //Debemos levantar la IP y Puerto del server redundante de la configuracion

        String ipServerRedundante = "localhost";
        int puertoServerRedundante = 8888;

        try {
            this.conexion.crearConexionEnvio(ipServerRedundante, puertoServerRedundante);

            ObjectOutputStream out = new ObjectOutputStream(this.conexion.getSocket().getOutputStream());

            Mensaje mensajeSincronizacion = new Mensaje();
            mensajeSincronizacion.setMensaje("SINCRONIZACION");
            mensajeSincronizacion.setConectados(this.conexiones);
            mensajeSincronizacion.setRegistrados(this.registros);

            out.writeObject(mensajeSincronizacion);


            this.conexion.cerrarConexion();

        } catch (Exception e) {
            this.vistaServidor.muestraMensaje("ERROR EN CONEXION CON SERVIDOR REDUNDANTE \n");
            e.printStackTrace();
        }

    }

    @Override
    public void enviaMensaje(String msg) {

    }

    public ArrayList<clienteConectado> getClientesFueraDeSesion() {

        ArrayList<clienteConectado> clientesFueraDeSesion = new ArrayList<>();

        for ( clienteConectado clienteRegistrado : this.registros ) {

            boolean enSesion = this.conexiones.stream().anyMatch( c -> c.getIp().equals(clienteRegistrado.getIp()) && clienteRegistrado.getPuerto() == c.getPuerto());

            if( !enSesion )
                clientesFueraDeSesion.add(clienteRegistrado);


        }

        return clientesFueraDeSesion;

    }

    public boolean verificaConexion(String ipDestino, int puertoDestino) {

        Optional<clienteConectado> opt = this.conexiones.stream().filter( e -> e.getIp().equals(ipDestino) && e.getPuerto() == puertoDestino ).findFirst();

        return opt.isPresent();
    }

    public boolean registrarCliente(String ipOrigen, int puertoOrigen, String nicknameOrigen) {

        boolean existeRegistro = false;
        
        ArrayList<clienteConectado> filtrado = (ArrayList<clienteConectado>) registros.stream().filter(e -> Objects.equals(e.getIp(), ipOrigen) && e.getPuerto()==puertoOrigen).collect(Collectors.toList());

        if(filtrado.size()>0)
        	existeRegistro = true;
        
        if( existeRegistro ){
            return false;
        }else{
        	clienteConectado cliente = new clienteConectado(ipOrigen,puertoOrigen,nicknameOrigen);
            this.registros.add(cliente);
            this.vistaServidor.muestraMensaje("REGISTRO: "+ nicknameOrigen +" | "+ ipOrigen + " | " + puertoOrigen + "\n\n");

            return true;

        }

    }

    @Override
    public Mensaje recibeMensaje() {
        ObjectInputStream in = this.conexion.getInputStreamConexion();
        try {
            return (Mensaje) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void agregaConectado(String ip, int puerto,String nickname) {

        clienteConectado cliente = new clienteConectado(ip,puerto,nickname);
        this.conexiones.add(cliente);

    }
    
    public void eliminaRegistro(String ip, int puerto) {

        this.registros.removeIf( c -> c.getIp().equals(ip) && c.getPuerto() == puerto );

    }
    
    public void eliminaConectado(String ip, int puerto) {

        this.conexiones.removeIf( c -> c.getIp().equals(ip) && c.getPuerto() == puerto );

    }
   
    public Conexion getConexion() {
    	return this.conexion;
    }
    
    public int getPuertoServer() {
    	return this.puertoServer;
    }
    
}

package servidor;

import conexion.Conexion;
import configuracion.ConfiguracionServer;
import mensaje.Mensaje;
import mensaje.clienteConectado;
import vista.vistas.VistaServidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.stream.Collectors;

public class Servidor implements Runnable, Recepcion, Emision {

    private static Servidor servidor = null;

    private final VistaServidor vistaServidor;

    private final int puertoServer;

    
    private ArrayList<clienteConectado> registros = new ArrayList<clienteConectado>();
    private ArrayList<clienteConectado> conexiones = new ArrayList<clienteConectado>();

    private Conexion conexion;

    private Servidor(){

        this.puertoServer = Integer.parseInt(ConfiguracionServer.getConfig().getParametros()[1]);

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
        try {

            this.conexion = new Conexion();

            //ServerSocket
            conexion.establecerConexion(this.puertoServer);

            this.vistaServidor.muestraMensaje("Servidor Iniciado! \nPuerto: " + this.puertoServer + "\n");

            String ipDestino;
            String nicknameDestino = null;
            String msg;
            Mensaje mensaje;
            int puertoDestino;

            while (true) {

                //Acepto conexion, me crea un Socket
                conexion.aceptarConexion();

                mensaje = this.recibeMensaje();

                puertoDestino = mensaje.getPuertoDestino();
                puertoOrigen = mensaje.getPuertoOrigen();
                ipOrigen = mensaje.getIpOrigen();
                ipDestino = mensaje.getIpDestino();
                nicknameOrigen = mensaje.getNicknameOrigen();
                nicknameDestino = mensaje.getNicknameDestino();
                msg = mensaje.getMensaje();

                if( msg.equals("ELIMINA REGISTRO") ) {
                    this.eliminaRegistro(ipOrigen, puertoOrigen);
                    this.vistaServidor.muestraMensaje("BAJA CLIENTE: "+ nicknameOrigen +" | "+ ipOrigen + " | " + puertoOrigen + "\n\n");
                }else {

                    if (msg.equalsIgnoreCase("LLAMADA ACEPTADA")) {
                        this.agregaConectado(ipOrigen,puertoOrigen,nicknameOrigen);
                        this.agregaConectado(ipDestino,puertoDestino,nicknameDestino);
                    } else if (msg.equalsIgnoreCase("DESCONECTAR")) {
                        this.eliminaConectado(ipOrigen, puertoOrigen);
                        this.eliminaConectado(ipDestino, puertoDestino);
                    } else if (msg.equalsIgnoreCase("REGISTRO")) {

                        if (this.registrarCliente(ipOrigen, puertoOrigen,nicknameOrigen)) {
                        	msg = "REGISTRO EXITOSO";
                        	mensaje.setConectados(this.getClientesFueraDeSesion());
                        }
                        else
                            msg = "REGISTRO FALLIDO";

                        mensaje.setMensaje(msg);
                    } else if ( msg.equalsIgnoreCase("RECARGAR CONECTADOS") ) {
                        mensaje.setConectados(this.getClientesFueraDeSesion());
                    }else if( (msg.equalsIgnoreCase("LLAMADA") && verificaConexion(ipDestino, puertoDestino)) ){
                        msg = "OCUPADO";
                        mensaje.setMensaje(msg);
                    }

                    this.vistaServidor.muestraMensaje("ORIGEN: " + ipOrigen + " => DESTINO: " + ipDestino + " :\n" + msg + "\n\n");

                    //Creo socket: Reenvio del mensaje
                    this.conexion.crearConexionEnvio(ipDestino, puertoDestino);

                    ObjectOutputStream out = new ObjectOutputStream(this.conexion.getSocket().getOutputStream());

                    	out.writeObject(mensaje);
                }

                this.conexion.cerrarConexion();

            }

        } catch (IOException e) {

            try {

                Mensaje mensaje = new Mensaje();
                mensaje.setMensaje("ERROR LLAMADA");

                this.conexion.crearConexionEnvio(ipOrigen, puertoOrigen);

                ObjectOutputStream out = new ObjectOutputStream(this.conexion.getSocket().getOutputStream());
                out.writeObject(mensaje);

                this.vistaServidor.muestraMensaje("ERROR EN CONEXION: "+ nicknameOrigen +" | "+ ipOrigen + " | " + puertoOrigen + "\n\n");

                this.conexion.cerrarConexion();

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }


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
        boolean conexionExistente = false;
        
        ArrayList<clienteConectado> filtrado = (ArrayList<clienteConectado>) conexiones.stream().filter(e -> e.getIp()==ipDestino && e.getPuerto()==puertoDestino).collect(Collectors.toList());

        if(filtrado.size()>0)
        	conexionExistente = true;
        
        return conexionExistente;
    }

    public boolean registrarCliente(String ipOrigen, int puertoOrigen, String nicknameOrigen) {

        boolean existeRegistro = false;
        
        ArrayList<clienteConectado> filtrado = (ArrayList<clienteConectado>) registros.stream().filter(e -> e.getIp()==ipOrigen && e.getPuerto()==puertoOrigen).collect(Collectors.toList());

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
        ObjectInputStream in = conexion.getInputStreamConexion();
        try {
            return  (Mensaje) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void agregaConectado(String ip, int puerto,String nickname) {
    	boolean noExiste = true;
    	
    	ArrayList<clienteConectado> filtrado = (ArrayList<clienteConectado>) conexiones.stream().filter(e -> e.getIp()==ip && e.getPuerto()==puerto).collect(Collectors.toList());

        if(filtrado.size()>0)
        	noExiste = false;
        
        if( noExiste ){
        	clienteConectado cliente = new clienteConectado(ip,puerto,nickname);
            this.conexiones.add(cliente);
        }
    }
    
    public void eliminaRegistro(String ip, int puerto) {

        this.registros.removeIf( c -> c.getIp().equals(ip) && c.getPuerto() == puerto );

    }
    
    public void eliminaConectado(String ip, int puerto) {

        this.conexiones.removeIf( c -> c.getIp().equals(ip) && c.getPuerto() == puerto );

    }
    
    
}

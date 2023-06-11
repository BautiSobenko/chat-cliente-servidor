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

    private int soyServidorPrimario = 1; //Inicio como verdadero y cambio si me demuestran lo contrario
	
	private static Servidor servidor = null;

    private final VistaServidor vistaServidor;

    private final int puertoServer;

    private int puertoOtroServidor;
    
    private ArrayList<clienteConectado> registros = new ArrayList<clienteConectado>();
    private ArrayList<clienteConectado> conexiones = new ArrayList<clienteConectado>();

    private Conexion conexion;

    private Servidor(int puerto){

        //this.puertoServer = Integer.parseInt(ConfiguracionServer.getConfig().getParametros()[1]);

        this.puertoServer = puerto;
    	this.vistaServidor = new VistaServidor();
    	if(this.puertoServer==9090)
        	this.puertoOtroServidor = 8888;
        else
        	this.puertoOtroServidor = 9090;
        
        //Tengo que verificar que tipo de servidor es, si es secundario envio solicitud de recincronizacion

        Thread hiloServer = new Thread(this);
        hiloServer.start();

        
    }
    
    public static Servidor getServer(int puerto){
        if( servidor == null ){
            servidor = new Servidor(puerto);
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
            ObjectOutputStream out;
            
            //Verifico si esta el otro activo
            try {
                this.conexion.crearConexionEnvio("localhost", this.puertoOtroServidor);
                out = new ObjectOutputStream(this.conexion.getSocket().getOutputStream());
                Mensaje mensajeClienteServidor = new Mensaje();
                mensajeClienteServidor.setMensaje("InicioServidor");
                out.writeObject(mensajeClienteServidor);
                this.conexion.cerrarConexion();
            }
            catch(Exception e) {
            	this.vistaServidor.muestraMensaje("SOY SERVIDOR PRIMARIO \n");
            }
            
            //Cuando inicio el servidor mando al otro servidor que me inicie
            //Si el otro servidor esta activo, me contestara, sino me mantengo como servidor primario
            
            
            while (true) {
            	
                conexion.aceptarConexion();

                mensaje = this.recibeMensaje();
                msg = mensaje.getMensaje();

                /*
                Tendremos 2 tipos de Mensajes:
                 1. Mensajes Cliente-Servidor
                 2. Mensajes Servidor-Servidor -> De Sincronizacion
                 */

                if ( msg.equals("SINCRONIZACION") ) {
   
                    this.conexiones = mensaje.getConectados();
                    this.registros = mensaje.getRegistrados();   
            	}else {
            	if(msg.equals("InicioServidor")) {      		
            	//Se inicio el otro servidor y me esta avisando
            	//Le contesto que yo soy primario	
            		this.conexion.crearConexionEnvio("localhost", this.puertoOtroServidor);
                    out = new ObjectOutputStream(this.conexion.getSocket().getOutputStream());
                    mensaje = new Mensaje();
                    mensaje.setMensaje("SosSegundoComoFrancia");
                    mensaje.setConectados(this.conexiones);
                    mensaje.setRegistrados(this.registros);
                    out.writeObject(mensaje);
                    this.conexion.cerrarConexion();
                }else {
                	if(msg.equals("SosSegundoComoFrancia")) {
                		//El otro servidor me aviso que el es el primario
                		this.soyServidorPrimario = 0;
                		this.vistaServidor.muestraMensaje("SOY SERVIDOR SECUNDARIO \n");
                		this.conexiones = mensaje.getConectados();
                		this.registros = mensaje.getRegistrados();
                	}else {
                		
                    mensaje = (Mensaje) mensaje;

                    puertoDestino = mensaje.getPuertoDestino();
                    ipDestino = mensaje.getIpDestino();
                    nicknameDestino = mensaje.getNicknameDestino();

                    puertoOrigen = mensaje.getPuertoOrigen();
                    ipOrigen = mensaje.getIpOrigen();
                    nicknameOrigen = mensaje.getNicknameOrigen();

                  if (msg.equals("ELIMINA REGISTRO")) {
                        this.eliminaRegistro(ipOrigen, puertoOrigen);
                        this.vistaServidor.muestraMensaje("BAJA CLIENTE: " + nicknameOrigen + " | " + ipOrigen + " | " + puertoOrigen + "\n\n");
                    } else {

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

        } 
        }
        }catch (IOException e) {

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

    /*
    Sincronizo al modificar Registros
    Sincronizo al modificar Conexiones
     */
    public void sincronizacionRedundancia() {

        //Debemos levantar la IP y Puerto del server redundante de la configuracion

        String ipServerRedundante = "localhost";
        int puertoServerReundante = 0;
        
        if(this.puertoServer==9090)
        	puertoServerReundante = 8888;
        else
        	puertoServerReundante = 9090;
        	

        try {
            this.conexion.crearConexionEnvio(ipServerRedundante, puertoServerReundante);

            ObjectOutputStream out = new ObjectOutputStream(this.conexion.getSocket().getOutputStream());

            Mensaje mensajeSincronizacion = new Mensaje();
            mensajeSincronizacion.setMensaje("SINCRONIZACION");
            mensajeSincronizacion.setConectados(this.conexiones);
            mensajeSincronizacion.setRegistrados(this.registros);

            out.writeObject(mensajeSincronizacion);

            this.conexion.cerrarConexion();

        } catch (IOException e) {
            this.vistaServidor.muestraMensaje("ERROR EN CONEXION - SINCRONIZACION CON REDUNDANCIA \n");
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
            //Le paso el cliente registrado al servidor
            this.vistaServidor.muestraMensaje("REGISTRO: "+ nicknameOrigen +" | "+ ipOrigen + " | " + puertoOrigen + "\n\n");

            return true;

        }

    }

    @Override
    public Mensaje recibeMensaje() {
        ObjectInputStream in = conexion.getInputStreamConexion();
        try {
            return (Mensaje) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void agregaConectado(String ip, int puerto,String nickname) {

        clienteConectado cliente = new clienteConectado(ip,puerto,nickname);
        this.conexiones.add(cliente);
        //Le mando agregar conectado a servidor secundario

    }
    
    public void eliminaRegistro(String ip, int puerto) {

        this.registros.removeIf( c -> c.getIp().equals(ip) && c.getPuerto() == puerto );
        //Le mando eliminar registrado a servidor secundario

    }
    
    public void eliminaConectado(String ip, int puerto) {

        this.conexiones.removeIf( c -> c.getIp().equals(ip) && c.getPuerto() == puerto );
        //Le mando eliminar conectado a servidor secundario

    }
   
    
    
}

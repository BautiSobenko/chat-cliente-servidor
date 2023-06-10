package servidor;

import conexion.Conexion;
import configuracion.ConfiguracionServer;
import mensaje.Mensaje;
import mensaje.MensajeClienteServidor;
import mensaje.MensajeSincronizacion;
import mensaje.clienteConectado;
import vista.vistas.VistaServidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.stream.Collectors;

public class Servidor implements Runnable, Recepcion, Emision {

    private int primario;
	
	private static Servidor servidor = null;

    private final VistaServidor vistaServidor;

    private final int puertoServer;

    private ArrayList<clienteConectado> registros = new ArrayList<clienteConectado>();
    private ArrayList<clienteConectado> conexiones = new ArrayList<clienteConectado>();

    private Conexion conexion;

    private Servidor(){

        this.puertoServer = Integer.parseInt(ConfiguracionServer.getConfig().getParametros()[1]);

        this.vistaServidor = new VistaServidor();
        
        //Tengo que verificar que tipo de servidor es, si es secundario envio solicitud de recincronizacion

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

                conexion.aceptarConexion();

                mensaje = this.recibeMensaje();
                msg = mensaje.getMensaje();

                /*
                Tendremos 2 tipos de Mensajes:
                 1. Mensajes Cliente-Servidor
                 2. Mensajes Servidor-Servidor -> De Sincronizacion
                 */

                if ( msg.equals("SINCRONIZACION") ) {
                    MensajeSincronizacion mensajeSincronizacion = (MensajeSincronizacion) mensaje;

                    this.conexiones = mensajeSincronizacion.getConexiones();
                    this.registros = mensajeSincronizacion.getRegistros();

                }else {

                    MensajeClienteServidor mensajeClienteServidor = (MensajeClienteServidor) mensaje;

                    puertoDestino = mensajeClienteServidor.getPuertoDestino();
                    ipDestino = mensajeClienteServidor.getIpDestino();
                    nicknameDestino = mensajeClienteServidor.getNicknameDestino();

                    puertoOrigen = mensajeClienteServidor.getPuertoOrigen();
                    ipOrigen = mensajeClienteServidor.getIpOrigen();
                    nicknameOrigen = mensajeClienteServidor.getNicknameOrigen();

                    //Tengo que agregar un tipo de mensaje donde reciba las actualizaciones del primario
                    //Tengo que agregar un mensaje que represente resincronizacion donde reciba las dos listas, la de registro y conectados
                    //Tengo que agregar un mensaje que represente la SOLICITUD de resincronizacion, para enviar las dos listas
                    //Hay que crear una nueva clase para mandar el objeto de resincronizacion

                  if (msg.equals("ELIMINA REGISTRO")) {
                        this.eliminaRegistro(ipOrigen, puertoOrigen);
                        this.vistaServidor.muestraMensaje("BAJA CLIENTE: " + nicknameOrigen + " | " + ipOrigen + " | " + puertoOrigen + "\n\n");
                    } else {

                        if (msg.equalsIgnoreCase("LLAMADA ACEPTADA")) {
                            this.agregaConectado(ipOrigen, puertoOrigen, nicknameOrigen);
                            this.agregaConectado(ipDestino, puertoDestino, nicknameDestino);
                        } else if (msg.equalsIgnoreCase("DESCONECTAR")) {
                            this.eliminaConectado(ipOrigen, puertoOrigen);
                            this.eliminaConectado(ipDestino, puertoDestino);
                            mensajeClienteServidor.setConectados(this.getClientesFueraDeSesion());
                        } else if (msg.equalsIgnoreCase("REGISTRO")) {

                            if (this.registrarCliente(ipOrigen, puertoOrigen, nicknameOrigen)) {
                                msg = "REGISTRO EXITOSO";
                                mensajeClienteServidor.setConectados(this.getClientesFueraDeSesion());
                            } else
                                msg = "REGISTRO FALLIDO";

                            mensajeClienteServidor.setMensaje(msg);
                        } else if (msg.equalsIgnoreCase("RECARGAR CONECTADOS")) {
                            mensajeClienteServidor.setConectados(this.getClientesFueraDeSesion());
                        } else if ((msg.equalsIgnoreCase("LLAMADA") && verificaConexion(ipDestino, puertoDestino))) {
                            msg = "OCUPADO";
                            mensajeClienteServidor.setMensaje(msg);
                        }

                        this.vistaServidor.muestraMensaje("ORIGEN: " + ipOrigen + " => DESTINO: " + ipDestino + " :\n" + msg + "\n\n");

                        //Creo socket: Reenvio del mensaje
                        this.conexion.crearConexionEnvio(ipDestino, puertoDestino);

                        ObjectOutputStream out = new ObjectOutputStream(this.conexion.getSocket().getOutputStream());

                        out.writeObject(mensajeClienteServidor);
                    }

                }

                this.conexion.cerrarConexion();

            }

        } catch (IOException e) {

            try {

                MensajeClienteServidor mensajeClienteServidor = new MensajeClienteServidor();
                mensajeClienteServidor.setMensaje("ERROR LLAMADA");

                this.conexion.crearConexionEnvio(ipOrigen, puertoOrigen);

                ObjectOutputStream out = new ObjectOutputStream(this.conexion.getSocket().getOutputStream());
                out.writeObject(mensajeClienteServidor);

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
    public MensajeClienteServidor recibeMensaje() {
        ObjectInputStream in = conexion.getInputStreamConexion();
        try {
            return  (MensajeClienteServidor) in.readObject();
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

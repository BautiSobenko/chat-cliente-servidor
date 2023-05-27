package servidor;

import conexion.Conexion;
import configuracion.ConfiguracionServer;
import mensaje.Mensaje;
import vista.vistas.VistaServidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Servidor implements Runnable, Recepcion, Emision {

    private static Servidor servidor = null;

    private final VistaServidor vistaServidor;

    private final int puertoServer;

    private HashMap<Integer, String> conexiones;
    private HashMap<Integer, String> registro;

    private Conexion conexion;

    private Servidor(){

        this.puertoServer = Integer.parseInt(ConfiguracionServer.getConfig().getParametros()[1]);

        this.vistaServidor = new VistaServidor();


        conexiones = new HashMap<>();
        registro = new HashMap<>();

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
        try {

            this.conexion = new Conexion();

            //ServerSocket
            conexion.establecerConexion(this.puertoServer);

            this.vistaServidor.muestraMensaje("Servidor Iniciado! \nPuerto: " + this.puertoServer + "\n");

            String ipDestino;
            String msg;
            int puertoDestino;
            Mensaje mensaje;

            while (true) {

                //Acepto conexion, me crea un Socket
                conexion.aceptarConexion();

                mensaje = this.recibeMensaje();

                puertoDestino = mensaje.getPuertoDestino();
                puertoOrigen = mensaje.getPuertoOrigen();
                ipOrigen = mensaje.getIpOrigen();
                ipDestino = mensaje.getIpDestino();
                msg = mensaje.getMensaje();

                if( msg.equals("ELIMINA REGISTRO") ) {
                    this.registro.remove(puertoOrigen);
                    this.vistaServidor.muestraMensaje("BAJA CLIENTE: " + ipOrigen + " | " + puertoOrigen + "\n\n");
                }else {

                    if (msg.equalsIgnoreCase("LLAMADA ACEPTADA")) {
                        this.conexiones.put(puertoOrigen, ipOrigen);
                        this.conexiones.put(puertoDestino, ipDestino);
                    } else if (msg.equalsIgnoreCase("DESCONECTAR")) {
                        this.conexiones.remove(puertoOrigen);
                        this.conexiones.remove(puertoDestino);
                    } else if (msg.equalsIgnoreCase("REGISTRO")) {

                        if (this.registrarCliente(ipOrigen, puertoOrigen))
                            msg = "REGISTRO EXITOSO";
                        else
                            msg = "REGISTRO FALLIDO";

                        mensaje.setMensaje(msg);
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

                this.vistaServidor.muestraMensaje("ERROR EN CONEXION: " + ipOrigen + " | " + puertoOrigen + "\n\n");

                this.conexion.cerrarConexion();

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }


        }

    }

    @Override
    public void enviaMensaje(String msg) {

    }

    public boolean verificaConexion(String ipDestino, int puertoDestino) {
        boolean conexionExistente = false;

        for (Map.Entry<Integer, String> entry : this.conexiones.entrySet()) {
            if( entry.getValue().equalsIgnoreCase(ipDestino) && entry.getKey() == puertoDestino) {
                conexionExistente = true;
                break;
            }
        }

        return conexionExistente;
    }

    public boolean registrarCliente(String ipOrigen, int puertoOrigen) {

        boolean existeRegistro = false;

        for (Map.Entry<Integer, String> entry : this.registro.entrySet()) {
            if( entry.getValue().equalsIgnoreCase(ipOrigen) && entry.getKey() == puertoOrigen) {
                existeRegistro = true;
                break;
            }
        }

        if( existeRegistro ){
            return false;
        }else{
            this.registro.put(puertoOrigen, ipOrigen);
            this.vistaServidor.muestraMensaje("REGISTRO: " + ipOrigen + " | " + puertoOrigen + "\n\n");

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
}

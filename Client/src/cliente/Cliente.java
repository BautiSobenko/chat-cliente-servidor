package cliente;

import conexion.Conexion;
import controlador.*;

import encriptacion.Encriptacion;
import encriptacion.RSA;
import mensaje.Mensaje;

import javax.naming.ldap.Control;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.PublicKey;

public class Cliente implements Runnable, Emision, Recepcion {

    //singleton 
    private static Cliente cliente = null;

    public final Conexion conexion;

    private int puertoServidor;
    private int puertoDestino;
    private int puertoOrigen;
    private String ipServer;
    private String ipDestino;
    private String ipOrigen;
    private String nicknameOrigen;
    private String nicknameDestino;

    private final Encriptacion<PublicKey> rsa;
    private PublicKey publicKeyExtremo; //Public Key que me envia el extremo para el cifrado de mensajes


    private Cliente(){
        this.rsa = new RSA();
        this.conexion = new Conexion();
    }

    public static Cliente getCliente() {
        if( cliente == null) {
            cliente = new Cliente();
        }
        return cliente;
    }

    @Override
    public void enviaMensaje(String msg) {
    	boolean envio = true;
        
    	try {
    		this.conexion.crearConexionEnvio(this.ipServer, 9090); //Cambiar 9090 a this.puertoServidor y corregir en el archivo
    	}/*
    	    catch(Exception e) {
    		try {
    			if(this.puertoServidor == 9090) {
	    			this.conexion.crearConexionEnvio(this.ipServer,8888);
	    			this.puertoServidor = 8888;
    			}
                else {
                    this.conexion.crearConexionEnvio(this.ipServer, 9090);
                    this.puertoServidor = 9090;
                }
    		}*/
    		catch(Exception ex) {
    			ControladorRegistro.get(false).aviso("No se pudo establecer conexion con el Servidor");
    			envio = false;
    		}
    	
    	if(envio) {
	    	try {

	            Mensaje mensaje = new Mensaje();
	
	            //Obtengo la ip origen (Informacion extra)
	            InetAddress adress = InetAddress.getLocalHost();
	            this.ipOrigen = adress.getHostAddress();
	
	            mensaje.setIpOrigen(this.ipOrigen);
	            mensaje.setPuertoOrigen(this.puertoOrigen);
	            mensaje.setNicknameOrigen(this.nicknameOrigen);
	
	            if( msg.equals("REGISTRO")  || msg.equals("ELIMINA REGISTRO") || msg.equals("RECARGAR CONECTADOS") ) {
	                mensaje.setIpDestino(this.ipOrigen);
	                mensaje.setPuertoDestino(this.puertoOrigen);
	            }else {
	                //Si es "localhost", trabajo con la IP real, no con la String "localhost"
	                if( this.ipDestino.equals("localhost") )
	                    mensaje.setIpDestino(adress.getHostAddress());
	                else
	                    mensaje.setIpDestino(this.ipDestino);
	
	                mensaje.setPuertoDestino(this.puertoDestino);
	            }
	
	            //Los mensajes de "Control" no debo cifrarlos
	            if( msg.equals("LLAMADA") || msg.equals("DESCONECTAR") || msg.equals("REGISTRO") || msg.equals("ELIMINA REGISTRO") || msg.equals("RECARGAR CONECTADOS") ) {
	
	                if (msg.equals("LLAMADA")){
	                    mensaje.setPublicKey(this.rsa.getPublicKey()); //Cuando yo llamo, ya envio mi clave publica (puede aceptarme)
	                }
	
	                mensaje.setMensaje(msg);
	            }else
	                mensaje.setMensaje( this.rsa.encriptar(msg, this.publicKeyExtremo) ); //Encripto con la llave publica que me envio
	
	
	            ObjectOutputStream out = this.conexion.getOutputStreamConexion();//new ObjectOutputStream(sCliente.getOutputStream());
	            out.writeObject(mensaje);
	            out.close();
	
	            conexion.cerrarConexion();
	
	        } catch (Exception e) {
	            ControladorRegistro.get(false).aviso("No se pudo establecer conexion con el Servidor");
	        }
    	}
    }


    public void enviaMensaje(String msg, String ipDestino, int puertoDestino, String nicknameDestino) {
    	boolean envio = true;
    	
    	try {
    		this.conexion.crearConexionEnvio(this.ipServer, 9090); //Cambiar 9090 a this.puertoServidor y corregir en el archivo
    	}/*
    	catch(Exception e) {
    		try {
    			if(this.puertoServidor == 9090) {
	    			this.conexion.crearConexionEnvio(this.ipServer,8888);
	    			this.puertoServidor = 8888;
    			}
                else {
                    this.conexion.crearConexionEnvio(this.ipServer, 9090);
                    this.puertoServidor = 9090;
                }
    		}*/
    		catch(Exception ex) {
    			ControladorRegistro.get(false).aviso("No se pudo establecer conexion con el Servidor");
    			envio = false;
    		}

    	if(envio) {
    		try {
	            Mensaje mensaje = new Mensaje();
	
	            InetAddress adress = InetAddress.getLocalHost(); //Obtengo la ip origen (Informacion extra)
	            this.ipOrigen = adress.getHostAddress();
	
	
	            if (msg.equalsIgnoreCase("LLAMADA ACEPTADA")) {
	
	                this.puertoDestino = puertoDestino;
	                this.ipDestino = ipDestino;
	                this.nicknameDestino = nicknameDestino;
	
	                mensaje.setPuertoDestino(puertoDestino);
	                mensaje.setIpDestino(ipDestino);
	                mensaje.setNicknameDestino(nicknameDestino);
	
	                mensaje.setIpOrigen(this.ipOrigen);
	                mensaje.setPuertoOrigen(this.puertoOrigen);
	                mensaje.setNicknameOrigen(this.nicknameOrigen);
	
	                mensaje.setPublicKey(this.rsa.getPublicKey()); //Acepte la llamada, le envio mi clave publica al extremo para comenzar a intercambiar mensajes
	
	                mensaje.setMensaje(msg);
	
	            }
	
	            ObjectOutputStream out = this.conexion.getOutputStreamConexion(); //new ObjectOutputStream(sCliente.getOutputStream());
	            out.writeObject(mensaje);
	            out.close();
	
	            conexion.cerrarConexion();
        } catch (Exception e) {
           
        }
    	}
    }

    @Override
    public void run() {

        try {
            this.conexion.establecerConexion(this.puertoOrigen);

            String ipO, txt, nickname;
            int puertoO;
            Mensaje mensajeRecibido;

            while (true) {

                this.conexion.aceptarConexion();

                mensajeRecibido = this.recibeMensaje();

                puertoO = mensajeRecibido.getPuertoOrigen();
                ipO = mensajeRecibido.getIpOrigen();
                nickname = mensajeRecibido.getNicknameOrigen();

                txt = mensajeRecibido.getMensaje();

                if (txt.equalsIgnoreCase("LLAMADA")) {
                    ControladorRecepcionLlamada controladorRecepcionLlamada = ControladorRecepcionLlamada.get(false);

                    //Posible respuesta al que me envio la llamada
                    controladorRecepcionLlamada.setPuertoDestino(puertoO);
                    controladorRecepcionLlamada.setNicknameDestino(nickname);
                    controladorRecepcionLlamada.setIpDestino(ipO);

                    controladorRecepcionLlamada.actualizarLabelIP(ipO,nickname);
                    ControladorRecepcionLlamada.get(true);

                    this.publicKeyExtremo = mensajeRecibido.getPublicKey(); //Recibo clave publica del extremo (puedo aceptar la llamada)
                }
                else if (txt.equalsIgnoreCase("LLAMADA ACEPTADA")) {
                    this.publicKeyExtremo = mensajeRecibido.getPublicKey(); //Recibo la clave publica del extremo que acepto mi llamada
                    ControladorInicio.get(false);
                    ControladorSesionLlamada.get(true);
                }
                else if (txt.equalsIgnoreCase("DESCONECTAR")) {
                    ControladorSesionLlamada.get(false).esconderVista();
                    ControladorSesionLlamada.get(false).borrarHistorial();
                    ControladorInicio.get(false).limpiarCampos();
                    ControladorInicio.get(true).setListaConectados(mensajeRecibido.getConectados());
                    this.publicKeyExtremo = null;
                }
                else if (txt.equalsIgnoreCase("REGISTRO EXITOSO")) {
                	//Recibo el mensaje registro exitoso, entonces se que tengo la lista
                    ControladorRegistro.get(false).registroCliente(true,mensajeRecibido.getConectados());              
                }
                else if (txt.equalsIgnoreCase("REGISTRO FALLIDO")) {
                    ControladorRegistro.get(false).registroCliente(false,mensajeRecibido.getConectados());

                    //Si el registro falla, debo mantener los cambios previos a esa configuracion
                    //TO-DO Habria que hacer un nuevo mensaje de actualizar configuracion
                    //Ya que el Registro y configuracion se comportan diferente

                    /*
                    ControladorInicio.get(false).setMiPuerto(ControladorConfiguracion.get(false).getPuertoAntiguo());
                    ControladorInicio.get(false).setMiNickname(ControladorConfiguracion.get(false).getNicknameAntiguo());
                    ControladorInicio.get(false).actualizarTituloVista();
                     */
                }
                else if (txt.equalsIgnoreCase("ERROR LLAMADA")) {
                    ControladorInicio.get(true).error("Error en la conexion");
                }
                else if (txt.equalsIgnoreCase("OCUPADO")) {
                    ControladorInicio.get(false).error("El contacto al que intenta llamar se encuentra Ocupado");
                }
                else if( txt.equalsIgnoreCase("RECARGAR CONECTADOS") ){
                    ControladorInicio.get(true).setListaConectados(mensajeRecibido.getConectados());
                } else {
                    String mensajeDesencriptado = this.rsa.desencriptar(txt); //Lo desencripto con mi clave privada. El extremo encripto con mi clave publica (enviada)
                    ControladorSesionLlamada.get(false).muestraMensaje(nickname + ": " + mensajeDesencriptado);
                }

                this.conexion.cerrarConexion();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void setPuertoDestino(int puertoDestino) {
        this.puertoDestino = puertoDestino;
    }

    public void setPuertoOrigen(int puertoOrigen) {
        this.puertoOrigen = puertoOrigen;
    }

    public void setIpDestino(String ipDestino) {
        this.ipDestino = ipDestino;
    }

    public void setIpServer(String ipServer) {
        this.ipServer = ipServer;
    }

    public void setPuertoServidor(int puertoServidor) {
        this.puertoServidor = puertoServidor;
    }

    public void setIpOrigen(String ipOrigen) {
        this.ipOrigen = ipOrigen;
    }

    public int getPuertoOrigen() {
        return puertoOrigen;
    }

    public String getIpOrigen() {
        return ipOrigen;
    }

    
    public String getNicknameOrigen() {
		return nicknameOrigen;
	}

	public void setNicknameOrigen(String nicknameOrigen) {
		this.nicknameOrigen = nicknameOrigen;
	}

	public String getNicknameDestino() {
		return nicknameDestino;
	}

	public void setNicknameDestino(String nicknameDestino) {
		this.nicknameDestino = nicknameDestino;
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

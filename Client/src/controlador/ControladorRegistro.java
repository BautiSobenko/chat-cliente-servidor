package controlador;

import configuracion.Configuracion;
import configuracion.ConfiguracionCliente;
import mensaje.clienteConectado;
import vista.interfaces.IVistaConfiguracion;
import vista.vistas.VistaRegistro;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ControladorRegistro implements ActionListener {

        private static ControladorRegistro controladorRegistro = null;

        private final IVistaConfiguracion vista;

        private ControladorRegistro() {
            this.vista = new VistaRegistro();
            this.vista.setActionListener(this);

            //Actualizo label con mi IP
            InetAddress adress;
            try {
                adress = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
            String ipOrigen = adress.getHostAddress();
            this.vista.setLblDireccionIP(ipOrigen);
        }

        public static ControladorRegistro get(boolean mostrar){
            if( controladorRegistro == null ) {
                controladorRegistro = new ControladorRegistro();
            }

            if( mostrar ){
                controladorRegistro.vista.mostrar();
            }

            return controladorRegistro;
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            try {

                ControladorInicio controladorInicio = ControladorInicio.get(false);

                ConfiguracionCliente.getConfig(this.vista.getIP(),this.vista.getPuerto(),this.vista.getNickname());

                if ( ConfiguracionCliente.getConfig().validarConfiguracion() ) {

                    //To-Do: actualizar para que escriba y levante el nickname
                    ConfiguracionCliente.getConfig().escribirArchivoConfiguracion();

                    controladorInicio.setMiPuerto(this.vista.getPuerto());
                    controladorInicio.setMiNickname(this.vista.getNickname());
                    controladorInicio.startCliente();

                } else {
                    vista.lanzarVentanaEmergente("Error al ingresar IP o Puerto");
                }

            }catch (RuntimeException exception){
                vista.lanzarVentanaEmergente("El puerto ingresado ya esta en uso");

            }catch (UnknownHostException ignored){

            } catch (Exception ex) {
                vista.lanzarVentanaEmergente("Error al escribir archivo de configuracion");
            }
        }

        public void aviso(String msg) {
            this.vista.lanzarVentanaEmergente(msg);
        }

        public void registroCliente(boolean exitoRegistro, ArrayList<clienteConectado> lista) {

            if( exitoRegistro ){
            	this.vista.esconder();
                ControladorInicio.get(true).setListaConectados(lista);
            }else{
                vista.lanzarVentanaEmergente("Actualmente hay una sesion abierta con el IP y Puerto ingresado");
            }

        }





}

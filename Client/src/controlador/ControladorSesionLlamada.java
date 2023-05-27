package controlador;

import cliente.Cliente;
import vista.interfaces.IVistaSesionLlamada;
import vista.vistas.VistaSesionLlamada;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;


public class ControladorSesionLlamada implements ActionListener, WindowListener {

    private static ControladorSesionLlamada controladorSesionLlamada = null;

    private final IVistaSesionLlamada vista;

    private ControladorSesionLlamada() {
        this.vista = new VistaSesionLlamada();
        this.vista.setActionListener(this);
        this.vista.setWindowListener(this);
    }

    public static ControladorSesionLlamada get(boolean mostrar){
        if( controladorSesionLlamada == null ) {
            controladorSesionLlamada = new ControladorSesionLlamada();
        }

        if( mostrar ){
            controladorSesionLlamada.vista.mostrar();
            controladorSesionLlamada.actualizarTitulo(Cliente.getCliente().getIpOrigen(), Cliente.getCliente().getPuertoOrigen());
        }

        return controladorSesionLlamada;
    }

    public void esconderVista() {
        this.vista.esconder();
    }

    public void actualizarTitulo(String ip, int puerto) {
        this.vista.actualizarTitulo(ip, puerto);
    }

    public void muestraMensaje(String msg) {
        vista.agregarLineaChat(msg);
    }

    public void borrarHistorial(){
        this.vista.borrarHistorial();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String comando = e.getActionCommand();

        String mensaje;

        switch (comando) {
            case ("Enviar") -> {
                mensaje = vista.getMensaje();
                vista.limpiarCampo();
                Cliente.getCliente().enviaMensaje(mensaje);
                this.muestraMensaje("Yo: " + mensaje);
            }
            case ("Desconectar") -> {
                Cliente.getCliente().enviaMensaje("DESCONECTAR");
                this.esconderVista();
                this.borrarHistorial();
                ControladorInicio.get(true).limpiarCampos();
            }
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        Cliente.getCliente().enviaMensaje("DESCONECTAR");
        this.esconderVista();
        this.borrarHistorial();
        ControladorInicio.get(true).limpiarCampos();
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
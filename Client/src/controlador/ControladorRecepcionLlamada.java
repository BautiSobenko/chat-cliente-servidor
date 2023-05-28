package controlador;

import cliente.Cliente;
import vista.interfaces.IVistaRecepcionLlamada;
import vista.vistas.VistaRecepcionLlamada;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControladorRecepcionLlamada implements ActionListener {

    private static ControladorRecepcionLlamada controladorRecepcionLlamada = null;

    private final IVistaRecepcionLlamada vista;
    private String ipDestino, nicknameDestino;
    private String ipOrigen;
    private int puertoOrigen;
    private int puertoDestino;

    private ControladorRecepcionLlamada() {
        this.vista = new VistaRecepcionLlamada();
        this.vista.setActionListener(this);
    }

    public static ControladorRecepcionLlamada get(boolean mostrar){
        if( controladorRecepcionLlamada == null ) {
            controladorRecepcionLlamada = new ControladorRecepcionLlamada();
        }


        if( mostrar ){
            controladorRecepcionLlamada.vista.mostrar();
        }

        return controladorRecepcionLlamada;
    }

    public void actualizarLabelIP(String IP,String nickname){
        controladorRecepcionLlamada.vista.setLabelIP(IP,nickname);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String comando = e.getActionCommand();

        switch (comando) {
            case ("Aceptar") -> {
                Cliente.getCliente().enviaMensaje("LLAMADA ACEPTADA", this.ipDestino, this.puertoDestino);
                this.vista.esconder();
                ControladorInicio.get(false);
                ControladorSesionLlamada.get(true);
            }
            case ("Rechazar") -> {
                this.vista.esconder();
                ControladorInicio.get(true).limpiarCampos();
            }
        }
    }

    public String getIpDestino() {
        return ipDestino;
    }

    public void setIpDestino(String ipDestino) {
        this.ipDestino = ipDestino;
    }

    public int getPuertoDestino() {
        return puertoDestino;
    }

    public void setPuertoDestino(int puertoDestino) {
        this.puertoDestino = puertoDestino;
    }

    public String getIpOrigen() {
        return ipOrigen;
    }

    public void setIpOrigen(String ipOrigen) {
        this.ipOrigen = ipOrigen;
    }

    public int getPuertoOrigen() {
        return puertoOrigen;
    }

    public void setPuertoOrigen(int puertoOrigen) {
        this.puertoOrigen = puertoOrigen;
    }

	public String getNicknameDestino() {
		return nicknameDestino;
	}

	public void setNicknameDestino(String nicknameDestino) {
		this.nicknameDestino = nicknameDestino;
	}
    
}


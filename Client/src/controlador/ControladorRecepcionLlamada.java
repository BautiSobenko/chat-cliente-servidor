package controlador;

import cliente.Cliente;
import vista.interfaces.IVistaRecepcionLlamada;
import vista.vistas.VistaRecepcionLlamada;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControladorRecepcionLlamada implements ActionListener {

    private static ControladorRecepcionLlamada controladorRecepcionLlamada = null;
    private final IVistaRecepcionLlamada vista;
    private String ipDestino;
    private String nicknameDestino;
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
                Cliente.getCliente().enviaMensaje("LLAMADA ACEPTADA", this.ipDestino, this.puertoDestino, this.nicknameDestino);
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

    public void setIpDestino(String ipDestino) {
        this.ipDestino = ipDestino;
    }

    public void setPuertoDestino(int puertoDestino) {
        this.puertoDestino = puertoDestino;
    }

	public void setNicknameDestino(String nicknameDestino) {
		this.nicknameDestino = nicknameDestino;
	}
    
}


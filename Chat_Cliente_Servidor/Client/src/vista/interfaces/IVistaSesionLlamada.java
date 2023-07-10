package vista.interfaces;

import java.awt.event.ActionListener;
import java.awt.event.WindowListener;

public interface IVistaSesionLlamada {

    public void setActionListener(ActionListener controlador);
    void setWindowListener(WindowListener controlador);

    public void mostrar();
    public void esconder();
    String getMensaje();
    void agregarLineaChat(String mensaje);
    void limpiarCampo();
    void actualizarTitulo(String ip, int puerto);
	public void lanzarVentanaEmergente(String string);
    public void error(String string);
    public void borrarHistorial();


}

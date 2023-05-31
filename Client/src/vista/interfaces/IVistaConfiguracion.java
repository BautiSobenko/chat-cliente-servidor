package vista.interfaces;

import java.awt.event.ActionListener;
import java.awt.event.WindowListener;

public interface IVistaConfiguracion {

	public void setActionListener(ActionListener controlador);
	public void setWindowListener(WindowListener controlador);
	public String getIP();
	public int getPuerto();
	public void mostrar();
	public void esconder();
	void lanzarVentanaEmergente(String mensaje);
	void setLblDireccionIP(String direccionIP);
	public void setTxtPuerto(String puerto);
	public void setTxtNickname(String miNickname);
	public String getNickname();
}

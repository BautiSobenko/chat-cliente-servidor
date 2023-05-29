package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.UnknownHostException;

import configuracion.ConfiguracionCliente;
import vista.interfaces.IVistaConfiguracion;
import configuracion.Configuracion;
import vista.vistas.VistaConfiguracionPuerto;


public class ControladorConfiguracion implements ActionListener, WindowListener {

	private static ControladorConfiguracion controladorConfiguracion = null;

	private final IVistaConfiguracion vista;

	private ControladorConfiguracion() {
		this.vista = new VistaConfiguracionPuerto();
		this.vista.setActionListener(this);
		this.vista.setWindowListener(this);
	}

    public static ControladorConfiguracion get(boolean mostrar){
        if( controladorConfiguracion == null ) {
			controladorConfiguracion = new ControladorConfiguracion();
		}

		if( mostrar ){
			controladorConfiguracion.lecturaArchivoConfiguracion();
			controladorConfiguracion.vista.setTxtPuerto( controladorConfiguracion.getMiPuerto() );
			controladorConfiguracion.vista.setTxtNickname(controladorConfiguracion.getMiNickname() );
			controladorConfiguracion.vista.mostrar();
		}

		return controladorConfiguracion;
    }


	@Override
	public void actionPerformed(ActionEvent e) {

		try {

			ControladorInicio controladorInicio = ControladorInicio.get(true);

			int miPuerto = vista.getPuerto();
			String nickname = vista.getNickname();


			if(controladorInicio.getMiPuerto() != miPuerto ){

				ConfiguracionCliente.getConfig().setNickname(nickname);
				ConfiguracionCliente.getConfig().setPuerto(miPuerto);

				if (ConfiguracionCliente.getConfig().validarConfiguracion()){

					ConfiguracionCliente.getConfig().escribirArchivoConfiguracion();

					controladorInicio.setMiNickname(nickname);
					controladorInicio.setMiPuerto(miPuerto);
					controladorInicio.startCliente();

					this.vista.esconder();
				} else
					vista.lanzarVentanaEmergente("Error al ingresar IP o Puerto");

			} else {
				if( !controladorInicio.getMiNickname().equals(nickname) ) {
					ConfiguracionCliente.getConfig().setNickname(nickname);
					ConfiguracionCliente.getConfig().escribirArchivoConfiguracion();
				}
				this.vista.esconder();
			}

		}catch (RuntimeException exception){
			vista.lanzarVentanaEmergente("El puerto ingresado ya esta en uso");
		}catch (UnknownHostException ignored){

		} catch (Exception ex) {
			vista.lanzarVentanaEmergente("Error al escribir archivo de configuracion");
		}
	}

	public String getMiPuerto() {
		return String.valueOf(ConfiguracionCliente.getConfig().getParametros()[1]);
	}

	public String getMiNickname() {
		return ConfiguracionCliente.getConfig().getParametros()[2];
	}

	public void lecturaArchivoConfiguracion() {
		ConfiguracionCliente.getConfig().leerArchivoConfiguracion();
	}

	//! Metodos WindowListener

	@Override
	public void windowOpened(WindowEvent e) {

	}

	@Override
	public void windowClosing(WindowEvent e) {
		ControladorInicio.get(true);
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

package vista.interfaces;

import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import mensaje.clienteConectado;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;

public interface IVistaInicio {

    void setActionListener(ActionListener controlador);
    void setListListener(ListSelectionListener controlador);
    void setWindowListener(WindowListener controlador);

    String getIP();
    int getPuerto();
    void lanzarVentanaEmergente(String mensaje);
    void limpiarCampo();
    void mostrar();
    void esconder();
    void error(String mensaje);
    void tituloInstancia(String ipOrigen, int miPuerto);
    void setConectados(ArrayList<clienteConectado> lista);

    void limpiarConectados();
    void setTxtIP(String ip);
    void setTxtPuerto(String puerto);

}

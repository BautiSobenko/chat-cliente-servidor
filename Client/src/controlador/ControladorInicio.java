package controlador;

import cliente.Cliente;
import configuracion.ConfiguracionServer;
import servidor.clienteConectado;
import vista.interfaces.IVistaInicio;
import vista.vistas.VistaInicio;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ControladorInicio implements ActionListener, WindowListener {

    private static ControladorInicio controladorInicio = null;

    private final IVistaInicio vista;
    private Cliente cliente;
    private int miPuerto;
    private String miNickname;

    private ControladorInicio() {
        this.vista = new VistaInicio();
        this.vista.setActionListener(this);
        this.vista.setWindowListener(this);
    }

    public void startCliente() {
        this.cliente = Cliente.getCliente();

        this.cliente.setPuertoServidor(ConfiguracionServer.getConfig().getPuerto());
        this.cliente.setIpServer(ConfiguracionServer.getConfig().getIp());

        this.cliente.setPuertoOrigen(miPuerto); //Lo seteo para evitar problemas en el ServerSocket en el run()
        this.cliente.setNicknameOrigen(miNickname);

        Thread hiloCliente = new Thread(this.cliente);
        hiloCliente.start();

        this.cliente.enviaMensaje("REGISTRO");

    }

    public static ControladorInicio get(boolean mostrar) {
        if( controladorInicio == null) {
            controladorInicio = new ControladorInicio();
        }
        if( mostrar ) {
            controladorInicio.vista.mostrar();
        }else
            controladorInicio.vista.esconder();


        return controladorInicio;
    }

    public int getMiPuerto() {

        return miPuerto;
    }

    public void lanzarAviso(String msg){
        this.vista.lanzarVentanaEmergente(msg);
    }

    public void error(String msg) {
        this.vista.error(msg);
    }

    public void limpiarCampos(){
        this.vista.limpiarCampo();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String comando = e.getActionCommand();

        switch (comando) {
            case "Configuracion" -> {
                ControladorConfiguracion.get(true);
                this.vista.esconder();
            }
            case "Conectar" -> {
                String ipDestino = vista.getIP();
                int puertoDestino = vista.getPuerto();
                if (puertoDestino != this.miPuerto) {
                    this.limpiarCampos();
                    try {
                        this.cliente.setIpDestino(ipDestino);
                        this.cliente.setPuertoDestino(puertoDestino);
                        this.cliente.setIpOrigen("localhost");
                        this.cliente.setPuertoOrigen(miPuerto);

                        this.cliente.enviaMensaje("LLAMADA");

                        this.lanzarAviso("Esperando a ser atendido...");

                    } catch (Exception exception) {
                        this.vista.error("Error en la conexion");
                    }
                } else
                    this.vista.error("No se puede comunicar con su mismo puerto");
            }
        }

    }

    public void setMiPuerto(int puerto) {
    	this.miPuerto = puerto;
        actualizarTituloVista();
    }
    
    public void setListaConectados(ArrayList<clienteConectado> lista) {
    	vista.setConectados(lista);
    }

    public void setMiNickname(String miNickname) {
		this.miNickname = miNickname;
	}

	public void actualizarTituloVista(){
        InetAddress adress; //Obtengo la ip origen (Informacion extra)

        try {
            adress = InetAddress.getLocalHost();
            String ipOrigen = adress.getHostAddress();
            this.vista.tituloInstancia(ipOrigen, miPuerto);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        try {
            this.cliente.enviaMensaje("ELIMINA REGISTRO");
            System.exit(0);
        }
        catch(Exception i) {
            System.exit(0);
        }
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

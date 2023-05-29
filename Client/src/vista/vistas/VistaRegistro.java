package vista.vistas;

import configuracion.Configuracion;
import configuracion.ConfiguracionCliente;
import vista.interfaces.IVistaConfiguracion;

import java.awt.EventQueue;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;

public class VistaRegistro extends JFrame implements IVistaConfiguracion {

	private final JTextField txtPuerto;
	private final JButton btnRegistrar;
	private final JLabel lblDireccionIP;
	private JTextField txtNickName;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VistaRegistro frame = new VistaRegistro();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public VistaRegistro() {
		setTitle("Registro en Servidor");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 533, 341);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		Configuracion config = new Configuracion();
		config.leerArchivoConfiguracion();

		JLabel lbl1 = new JLabel("Direccion IP");
		lbl1.setHorizontalAlignment(SwingConstants.CENTER);
		lbl1.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lbl1.setBounds(208, 22, 107, 14);
		contentPane.add(lbl1);
		
		this.lblDireccionIP = new JLabel("");
		lblDireccionIP.setHorizontalAlignment(SwingConstants.CENTER);
		lblDireccionIP.setFont(new Font("Tahoma", Font.ITALIC, 16));
		lblDireccionIP.setBounds(72, 60, 285, 29);
		contentPane.add(lblDireccionIP);
		lblDireccionIP.setText(config.getIp());
		
		JLabel lblIngreseSuPuerto = new JLabel("Ingrese su Puerto");
		lblIngreseSuPuerto.setHorizontalAlignment(SwingConstants.CENTER);
		lblIngreseSuPuerto.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblIngreseSuPuerto.setBounds(191, 100, 127, 26);
		contentPane.add(lblIngreseSuPuerto);
		
		txtPuerto = new JTextField();
		txtPuerto.setHorizontalAlignment(SwingConstants.CENTER);
		txtPuerto.setBounds(191, 137, 146, 30);
		contentPane.add(txtPuerto);
		txtPuerto.setColumns(10);
		txtPuerto.setText(String.valueOf(config.getPuerto()));
		
		this.btnRegistrar = new JButton("Registrar");
		btnRegistrar.setFont(new Font("Tahoma", Font.PLAIN, 16));
		btnRegistrar.setBounds(122, 254, 285, 39);
		contentPane.add(btnRegistrar);
		
		JLabel lblNewLabel = new JLabel("Ingrese su nombre de usuario:");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblNewLabel.setBounds(145, 178, 234, 14);
		contentPane.add(lblNewLabel);
		
		txtNickName = new JTextField();
		txtNickName.setHorizontalAlignment(SwingConstants.CENTER);
		txtNickName.setColumns(10);
		txtNickName.setBounds(191, 203, 146, 30);
		contentPane.add(txtNickName);
		txtNickName.setText(config.getNickname());
	}

	@Override
	public void setActionListener(ActionListener controlador) {
		this.btnRegistrar.addActionListener(controlador);
	}

	@Override
	public void setWindowListener(WindowListener controlador) {

	}

	@Override
	public String getIP() {
		return this.lblDireccionIP.getText();
	}

	@Override
	public int getPuerto() {
		return Integer.parseInt(this.txtPuerto.getText());
	}

	@Override
	public void mostrar() {
		this.setVisible(true);
	}

	@Override
	public void esconder() {
		this.setVisible(false);
	}

	@Override
	public void lanzarVentanaEmergente(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje,"Error en Conexion", JOptionPane.ERROR_MESSAGE);
	}

	public void setLblDireccionIP(String direccionIP) {
		this.lblDireccionIP.setText(direccionIP);
	}

	@Override
	public void setTxtPuerto(String puerto) {

	}
	
	public String getNickname() {
		return this.txtNickName.getText();
	}
}

package vista.vistas;

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
		setBounds(100, 100, 450, 300);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lbl1 = new JLabel("Direccion IP");
		lbl1.setHorizontalAlignment(SwingConstants.CENTER);
		lbl1.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lbl1.setBounds(164, 25, 107, 14);
		contentPane.add(lbl1);
		
		this.lblDireccionIP = new JLabel("");
		lblDireccionIP.setHorizontalAlignment(SwingConstants.CENTER);
		lblDireccionIP.setFont(new Font("Tahoma", Font.ITALIC, 16));
		lblDireccionIP.setBounds(72, 60, 285, 29);
		contentPane.add(lblDireccionIP);
		
		JLabel lblIngreseSuPuerto = new JLabel("Ingrese su Puerto");
		lblIngreseSuPuerto.setHorizontalAlignment(SwingConstants.CENTER);
		lblIngreseSuPuerto.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblIngreseSuPuerto.setBounds(152, 103, 127, 26);
		contentPane.add(lblIngreseSuPuerto);
		
		txtPuerto = new JTextField();
		txtPuerto.setHorizontalAlignment(SwingConstants.CENTER);
		txtPuerto.setBounds(142, 142, 146, 30);
		contentPane.add(txtPuerto);
		txtPuerto.setColumns(10);
		
		this.btnRegistrar = new JButton("Registrar");
		btnRegistrar.setFont(new Font("Tahoma", Font.PLAIN, 16));
		btnRegistrar.setBounds(72, 199, 285, 39);
		contentPane.add(btnRegistrar);
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
}

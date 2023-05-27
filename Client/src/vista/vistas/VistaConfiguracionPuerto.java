package vista.vistas;

import configuracion.ConfiguracionCliente;
import vista.interfaces.IVistaConfiguracion;
import configuracion.Configuracion;

import java.awt.EventQueue;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;

public class VistaConfiguracionPuerto extends JFrame implements IVistaConfiguracion {

	private final JTextField txtPuerto;
	private final JButton btnContinuar;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VistaConfiguracionPuerto frame = new VistaConfiguracionPuerto();
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
	public VistaConfiguracionPuerto() {
		setTitle("Configuracion ");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 415, 235);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Ingrese el Puerto que desea utilizar:");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblNewLabel.setBounds(74, 22, 261, 30);
		contentPane.add(lblNewLabel);
		
		txtPuerto = new JTextField();
		txtPuerto.setBounds(120, 63, 159, 30);
		contentPane.add(txtPuerto);
		txtPuerto.setColumns(10);
		txtPuerto.setHorizontalAlignment(SwingConstants.CENTER);
		txtPuerto.setText(String.valueOf(ConfiguracionCliente.getConfig().getParametros()[1]));
		
		btnContinuar = new JButton("Continuar");
		btnContinuar.setFont(new Font("Tahoma", Font.PLAIN, 16));
		btnContinuar.setBounds(83, 115, 236, 43);
		contentPane.add(btnContinuar);

	}

	public void setActionListener(ActionListener controlador) {
		this.btnContinuar.addActionListener(controlador);
	}

	@Override
	public void setWindowListener(WindowListener controlador) {
		this.addWindowListener(controlador);
	}

	public String getIP() {
		return "";
	}

	public int getPuerto() {
		return Integer.parseInt(this.txtPuerto.getText());
	}

	@Override
	public void mostrar() {
		this.setVisible(true);
	}

	public void setTxtPuerto(String puerto) {
		this.txtPuerto.setText(puerto);
	}

	@Override
	public void esconder() {
		this.setVisible(false);
	}

	@Override
	public void lanzarVentanaEmergente(String mensaje) {
		JOptionPane.showMessageDialog(this,mensaje);
	}

	@Override
	public void setLblDireccionIP(String direccionIP) {

	}

}

package vista.vistas;

import vista.interfaces.IVistaConfiguracion;
import configuracion.Configuracion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;

public class VistaConfiguracionPuerto extends JFrame implements IVistaConfiguracion {

	private JTextField txtPuerto;
	private final JButton btnContinuar;
	private JTextField txtNickname;

	public VistaConfiguracionPuerto() {
		setTitle("Configuracion ");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 415, 323);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		Configuracion config = new Configuracion();
		config.leerArchivoConfiguracion();
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblPuerto = new JLabel("Ingrese el Puerto que desea utilizar:");
		lblPuerto.setHorizontalAlignment(SwingConstants.CENTER);
		lblPuerto.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblPuerto.setBounds(74, 106, 261, 30);
		contentPane.add(lblPuerto);
		
		txtPuerto = new JTextField();
		txtPuerto.setBounds(114, 147, 159, 30);
		contentPane.add(txtPuerto);
		txtPuerto.setColumns(10);
		txtPuerto.setHorizontalAlignment(SwingConstants.CENTER);
		txtPuerto.setText(String.valueOf(config.getPuerto()));

		btnContinuar = new JButton("Continuar");
		btnContinuar.setFont(new Font("Tahoma", Font.PLAIN, 16));
		btnContinuar.setBounds(74, 206, 236, 43);
		contentPane.add(btnContinuar);
		
		txtNickname = new JTextField();
		txtNickname.setHorizontalAlignment(SwingConstants.CENTER);
		txtNickname.setColumns(10);
		txtNickname.setBounds(114, 51, 159, 30);
		contentPane.add(txtNickname);
		txtNickname.setText(config.getNickname());

		JLabel lblNickname = new JLabel("Ingrese Nickname:");
		lblNickname.setHorizontalAlignment(SwingConstants.CENTER);
		lblNickname.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblNickname.setBounds(61, 10, 261, 30);
		contentPane.add(lblNickname);

	}

	public void setActionListener(ActionListener controlador) {
		this.btnContinuar.addActionListener(controlador);
	}

	@Override
	public void setWindowListener(WindowListener controlador) {
		this.addWindowListener(controlador);
	}

	@Override
	public String getIP() {
		return null;
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

	@Override
	public String getNickname() {
		return this.txtNickname.getText();
	}

	@Override
	public void setTxtNickname(String nickname) {
		this.txtNickname.setText(nickname);
	}
	
	
}

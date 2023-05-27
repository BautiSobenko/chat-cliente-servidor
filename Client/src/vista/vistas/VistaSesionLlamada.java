package vista.vistas;

import vista.interfaces.IVistaSesionLlamada;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;

public class VistaSesionLlamada extends JFrame implements IVistaSesionLlamada {

	private final JTextField txtMensaje;
	private final JButton btnEnviar;
	private final JButton btnDesconectar;
	private final JTextArea txtArea;


	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VistaSesionLlamada frame = new VistaSesionLlamada();
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
	public VistaSesionLlamada() {
		setTitle("Sesion");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 608, 383);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		txtArea = new JTextArea();
		txtArea.setBounds(10, 11, 572, 228);
		contentPane.add(txtArea);

		txtMensaje = new JTextField();
		txtMensaje.setColumns(10);
		txtMensaje.setBounds(10, 250, 572, 38);
		contentPane.add(txtMensaje);

		btnEnviar = new JButton("Enviar");
		btnEnviar.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnEnviar.setBounds(10, 299, 263, 38);
		contentPane.add(btnEnviar);

		btnDesconectar = new JButton("Desconectar");
		btnDesconectar.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnDesconectar.setBounds(331, 299, 251, 38);
		contentPane.add(btnDesconectar);
	}

	@Override
	public void setActionListener(ActionListener controlador) {
		this.btnEnviar.addActionListener(controlador);
		this.btnDesconectar.addActionListener(controlador);
	}

	@Override
	public void setWindowListener(WindowListener controlador) {
		this.addWindowListener(controlador);
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
	public String getMensaje() {
		return this.txtMensaje.getText();
	}

	@Override
	public void agregarLineaChat(String mensaje) {
		this.txtArea.append(mensaje + "\n");
	}

	@Override
	public void limpiarCampo() {
		this.txtMensaje.setText("");
	}

	@Override
	public void lanzarVentanaEmergente(String string) {
		JOptionPane.showMessageDialog(this,string,"Aviso",JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public void error(String string) {
		JOptionPane.showMessageDialog(this,string,"Error",JOptionPane.ERROR_MESSAGE);
		
	}

	@Override
	public void actualizarTitulo(String ip, int puerto) {
		this.setTitle("Sesion: " + ip + " | " + puerto );
	}

	@Override
	public void borrarHistorial() {
		this.txtArea.setText("");
	}
}

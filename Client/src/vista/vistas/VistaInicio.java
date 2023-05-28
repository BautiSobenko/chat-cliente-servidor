package vista.vistas;

import vista.interfaces.IVistaInicio;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import servidor.clienteConectado;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

public class VistaInicio extends JFrame implements IVistaInicio {

	private JTextField txtPuerto;
	private final JButton btnConfiguracion;
	private final JButton btnConectar;
	private final JTextField txtIP;
	private JList<clienteConectado> listaConectados;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VistaInicio frame = new VistaInicio();
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
	public VistaInicio() {
		setTitle("Inicio");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 659, 619);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("IP: ");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblNewLabel.setBounds(113, 399, 26, 14);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Puerto:");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblNewLabel_1.setBounds(93, 450, 61, 14);
		contentPane.add(lblNewLabel_1);
		
		txtPuerto = new JTextField();
		txtPuerto.setToolTipText("");
		txtPuerto.setHorizontalAlignment(SwingConstants.CENTER);
		txtPuerto.setFont(new Font("Tahoma", Font.PLAIN, 13));
		txtPuerto.setColumns(10);
		txtPuerto.setBounds(224, 438, 227, 40);
		contentPane.add(txtPuerto);
		
		btnConectar = new JButton("Conectar");
		btnConectar.setFont(new Font("Tahoma", Font.BOLD, 13));
		btnConectar.setBounds(461, 510, 147, 48);
		contentPane.add(btnConectar);
		
		btnConfiguracion = new JButton("Configuracion");
		btnConfiguracion.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnConfiguracion.setBounds(32, 510, 160, 48);
		contentPane.add(btnConfiguracion);
		
		txtIP = new JTextField();
		txtIP.setText("localhost");
		txtIP.setHorizontalAlignment(SwingConstants.CENTER);
		txtIP.setFont(new Font("Tahoma", Font.PLAIN, 13));
		txtIP.setColumns(10);
		txtIP.setBounds(224, 387, 227, 40);
		contentPane.add(txtIP);
		
		listaConectados = new JList();
		listaConectados.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				//Aca se van a setear los campos
			}
		});
		listaConectados.setBounds(32, 23, 576, 288);
		contentPane.add(listaConectados);
		
		JButton btnRecrgarConectados = new JButton("Recargar Conectados");
		btnRecrgarConectados.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				///Aca se va a enviar un mensaje que pida la lista de nuevo
			}
		});
		btnRecrgarConectados.setFont(new Font("Tahoma", Font.BOLD, 13));
		btnRecrgarConectados.setBounds(227, 317, 196, 48);
		contentPane.add(btnRecrgarConectados);

		this.esconder();
	}

	@Override
	public void setActionListener(ActionListener controlador) {
		this.btnConfiguracion.addActionListener(controlador);
		this.btnConectar.addActionListener(controlador);
	}

	@Override
	public void setWindowListener(WindowListener controlador) {
		this.addWindowListener(controlador);
	}

	@Override
	public String getIP() {
		return this.txtIP.getText();
	}

	@Override
	public int getPuerto() {
		return Integer.parseInt(this.txtPuerto.getText());
	}


	@Override
	public void lanzarVentanaEmergente(String mensaje) {
		JOptionPane.showMessageDialog(this,mensaje,"Aviso",JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public void limpiarCampo() {
		this.txtIP.setText("");
		this.txtPuerto.setText("");
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
	public void error(String mensaje) {
		JOptionPane.showMessageDialog(this,mensaje,"Error",JOptionPane.ERROR_MESSAGE);
	}

	public void habilitarBotonConexion(){
		this.btnConectar.setEnabled(true);
	}

	@Override
	public void tituloInstancia(String ipOrigen, int puerto) {
		this.setTitle("IP: " + ipOrigen + " | Puerto: " + puerto);
	}

	public void deshabilitarBotonConexion(){
		this.btnConectar.setEnabled(false);
	}

	@Override
	public void setConectados(ArrayList<clienteConectado> lista) {
		// TODO Auto-generated method stub
		Iterator<clienteConectado> it = lista.iterator();
		int i=0;
		String conectados[];
		while(it.hasNext()){
			conectados[i] = it.toString();
			it.next();
			i+=1;
		}
		this.listaConectados.setModel(conectados);
	}
}

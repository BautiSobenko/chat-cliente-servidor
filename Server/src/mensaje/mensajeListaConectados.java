package mensaje;

import java.util.ArrayList;

import servidor.clienteConectado;

public class mensajeListaConectados {
	
	private String msg;
	private ArrayList<clienteConectado> conectados;
	
	public mensajeListaConectados(String msg, ArrayList<clienteConectado> conectados) {
		super();
		this.msg = msg;
		this.conectados = conectados;
	}
	
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public ArrayList<clienteConectado> getConectados() {
		return conectados;
	}
	public void setConectados(ArrayList<clienteConectado> conectados) {
		this.conectados = conectados;
	}

}

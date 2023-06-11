package mensaje;

import java.io.Serializable;

public class clienteConectado implements Serializable{
	
	private String ip;
	private int puerto;
	private String nickname;
	
	
	public clienteConectado(String ip, int puerto, String nickname) {
		super();
		this.ip = ip;
		this.puerto = puerto;
		this.nickname = nickname;
	}
	
	public clienteConectado() {}


	public String getIp() {
		return ip;
	}


	public void setIp(String ip) {
		this.ip = ip;
	}


	public int getPuerto() {
		return puerto;
	}


	public void setPuerto(int puerto) {
		this.puerto = puerto;
	}


	public String getNickname() {
		return nickname;
	}


	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
    public String toString() {
        return this.nickname+" en la direccion "+this.ip +" y en el puerto "+this.puerto;
      }

}

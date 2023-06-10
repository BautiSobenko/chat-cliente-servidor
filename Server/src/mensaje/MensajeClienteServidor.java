package mensaje;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;

public class MensajeClienteServidor extends Mensaje implements Serializable {
	
	String ipOrigen;
    String ipDestino;
    int puertoDestino;
    int puertoOrigen;
    String nicknameOrigen;
    String nicknameDestino;
    PublicKey publicKey;
    private ArrayList<clienteConectado> conectados;

    public MensajeClienteServidor() {
    }


    public int getPuertoOrigen() {
        return puertoOrigen;
    }

    public int getPuertoDestino() {
        return puertoDestino;
    }

    public String getIpOrigen() {
        return ipOrigen;
    }

    public String getIpDestino() {
        return ipDestino;
    }

    public String getNicknameOrigen() {
		return nicknameOrigen;
	}

	public void setNicknameOrigen(String nicknameOrigen) {
		this.nicknameOrigen = nicknameOrigen;
	}

	public String getNicknameDestino() {
		return nicknameDestino;
	}

	public void setConectados(ArrayList<clienteConectado> conectados) {
		this.conectados = conectados;
	}

	@Override
    public String toString() {
        return "Mensaje{" +
                "ipOrigen='" + ipOrigen + '\'' +
                ", ipDestino='" + ipDestino + '\'' +
                ", Mensaje='" + super.getMensaje() + '\'' +
                ", puertoDestino=" + puertoDestino +
                '}';
    }
}

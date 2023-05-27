package mensaje;

import java.io.Serializable;
import java.security.PublicKey;

public class Mensaje implements Serializable {

    String ipOrigen;
    String ipDestino;
    String Mensaje;
    int puertoDestino;
    int puertoOrigen;
    PublicKey publicKey;

    public Mensaje() {
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public int getPuertoOrigen() {
        return puertoOrigen;
    }

    public void setPuertoOrigen(int puertoOrigen) {
        this.puertoOrigen = puertoOrigen;
    }

    public int getPuertoDestino() {
        return puertoDestino;
    }

    public void setPuertoDestino(int puertoDestino) {
        this.puertoDestino = puertoDestino;
    }

    public String getIpOrigen() {
        return ipOrigen;
    }

    public void setIpOrigen(String ipOrigen) {
        this.ipOrigen = ipOrigen;
    }

    public String getIpDestino() {
        return ipDestino;
    }

    public void setIpDestino(String ipDestino) {
        this.ipDestino = ipDestino;
    }

    public String getMensaje() {
        return Mensaje;
    }

    public void setMensaje(String mensaje) {
        Mensaje = mensaje;
    }

    @Override
    public String toString() {
        return "Mensaje{" +
                "ipOrigen='" + ipOrigen + '\'' +
                ", ipDestino='" + ipDestino + '\'' +
                ", Mensaje='" + Mensaje + '\'' +
                ", puertoDestino=" + puertoDestino +
                '}';
    }
}

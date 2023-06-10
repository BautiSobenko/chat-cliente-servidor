package mensaje;

public abstract class Mensaje {

    private String msg;

    public Mensaje() {
    }

    public String getMensaje() {
        return msg;
    }

    public void setMensaje(String msg) {
        this.msg = msg;
    }
}

package encriptacion;

import java.security.PublicKey;

public interface Encriptacion<T> {

    public String encriptar(String msg) throws Exception;
    public String encriptar(String msg, T objeto) throws Exception;

    public String desencriptar(String msg) throws Exception;

    PublicKey getPublicKey();
}

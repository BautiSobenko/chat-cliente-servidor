package encriptacion;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import javax.crypto.*;

public class RSA implements Encriptacion<PublicKey> {

    private static Cipher rsa;

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public RSA() {

        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");

            keyPairGenerator.initialize(1024);

            //Genero llaves
            KeyPair keys = keyPairGenerator.generateKeyPair();

            //Llave public
            this.publicKey = keys.getPublic();

            //Llave privada
            this.privateKey = keys.getPrivate();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    @Override
    public String encriptar(String msg) {
        return null;
    }

    public String encriptar(String msg, PublicKey publicKeyExtremo) throws Exception  {
        byte[] msgToBytes = msg.getBytes();
        rsa = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsa.init(Cipher.ENCRYPT_MODE, publicKeyExtremo);
        byte[] encriptado = rsa.doFinal(msgToBytes);
        return encode(encriptado);
    }

    private String encode(byte[] encriptado) {
        return Base64.getEncoder().encodeToString(encriptado);
    }

    public String desencriptar(String encriptado) throws Exception{
        byte[] encriptedBytes = decode(encriptado);
        rsa = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsa.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] desencriptado = rsa.doFinal(encriptedBytes);
        return new String(desencriptado, StandardCharsets.UTF_8);
    }

    private byte[] decode(String encriptado) {
        return Base64.getDecoder().decode(encriptado);
    }

}

package modelo;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Cifrado {
    public static byte[] encriptar(String pass, String texto, String
            algoritmo)
            throws Exception {
        java.security.Key key = new SecretKeySpec(pass.getBytes(),
                algoritmo);
        Cipher cipher = Cipher.getInstance(algoritmo);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(texto.getBytes());
    }
    public static String desencriptar(String pass, byte[] encriptado,
                                      String algoritmo)
            throws Exception {
        java.security.Key key = new SecretKeySpec(pass.getBytes(),
                algoritmo);
        Cipher cipher = Cipher.getInstance(algoritmo);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] bytes = cipher.doFinal(encriptado);
        return new String(bytes);
    }

}

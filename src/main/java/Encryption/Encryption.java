package Encryption;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

/**
 * Created by Evdal on 07.03.2016.
 */
public class Encryption {

    public boolean passDecoding(String pass, String saltString, String hashString) throws Exception {
        byte[] salt = stringToByte(saltString);

        byte[] hashPass = stringToByte(hashString);

        KeySpec spec = new PBEKeySpec(pass.toCharArray(), salt, 65536, 128);
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = f.generateSecret(spec).getEncoded();

        if (Arrays.equals(hashPass, hash)) return true;
        return false;

    }


    private ArrayList<String> passEncoding(String password) throws Exception {
        Random rand = new Random();
        byte[] salt = new byte[16];
        rand.nextBytes(salt);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = f.generateSecret(spec).getEncoded();
        Base64.Encoder enc = Base64.getEncoder();
        ArrayList<String> out = new ArrayList<String>();
        out.add(enc.encodeToString(salt));
        out.add(enc.encodeToString(hash));

        return out;
    }

    private byte[] stringToByte(String string) {
        Base64.Decoder dec = Base64.getDecoder();
        return dec.decode(string);


    }

//}


    //Eks program:

    public static void main(String[] args) throws Exception {
        Encryption en = new Encryption();
        ArrayList<String> passInfo = en.passEncoding("password");
        if (en.passDecoding("password", passInfo.get(0), passInfo.get(1))) System.out.println("Success!");
        //}*/
    }
}

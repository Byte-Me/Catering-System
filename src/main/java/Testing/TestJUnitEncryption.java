package Testing;

import Encryption.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by Evdal on 09.03.2016.
 * ALL GOOD DON'T CHANGE ANYTHING
 */
public class TestJUnitEncryption {
    private Encryption en;
    private static String hash ="2oUGF8AAgobU1E3rcAtyiw==";
    private static String salt = "oQaZgG266KjDzEkGTgXYMQ==";

    @Before
            public void setUp(){
        en = new Encryption();

    }
    @Test
    public void encryptPassTest() throws Exception{

        String password = "password";
        String wrongPass = "pssword";
        String[] passEncrypted = en.passEncoding(password);
        String hash = passEncrypted[0];
        String salt = passEncrypted[1];
        boolean decryptValid = en.passDecoding(password, hash, salt);
        boolean decryptInvalid = en.passDecoding(wrongPass, hash, salt);


        assertTrue(decryptValid);
        assertFalse(decryptInvalid);
    }

    @After
    public void tearDown(){
        en = null;
    }
}

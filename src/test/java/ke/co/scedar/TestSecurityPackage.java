package ke.co.scedar;

import ke.co.scedar.utils.security.Encryption;
import ke.co.scedar.utils.security.Passwords;
import org.junit.jupiter.api.Test;

public class TestSecurityPackage {

    @Test public void testJavaUtilBase64(){
        String password = "Hello@2009";
        String hash = Passwords.make(password);
        System.out.println("Hash: "+hash);
        System.out.println(Passwords.check(password, hash));
    }

    @Test public void testEncryptDecrypt(){
        String clearText = "Hello William! - Said Job";
        String cipherText1 = Encryption.encrypt(clearText);
        String cipherText2 = Encryption.encrypto(clearText);
        System.out.println("CS1: "+cipherText1);
        System.out.println("CS2: "+cipherText2);
        System.out.println(Encryption.decrypt(cipherText1));
        System.out.println(Encryption.decrypto(cipherText2));
    }

}

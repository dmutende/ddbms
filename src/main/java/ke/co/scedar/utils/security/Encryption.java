package ke.co.scedar.utils.security;

import ke.co.scedar.utils.Constants;
import ke.co.scedar.utils.JvmManager;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Objects;

public class Encryption {

    private static final String UNICODE_FORMAT = "UTF8";
    private static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
    private static final String _key = "THISISELONTHISISELINTHISIELON";
    private Cipher cipher;
    private SecretKey key;

    private Encryption(){
        String encryptionKey = "ThisIsElonThisIsElonThisIsElon";
        String encryptionScheme = DESEDE_ENCRYPTION_SCHEME;
        byte[] arrayBytes = new byte[0];
        try {
            arrayBytes = encryptionKey.getBytes(UNICODE_FORMAT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        KeySpec ks = null;
        try {
            ks = new DESedeKeySpec(arrayBytes);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        SecretKeyFactory skf = null;
        try {
            skf = SecretKeyFactory.getInstance(encryptionScheme);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            cipher = Cipher.getInstance(encryptionScheme);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            key = Objects.requireNonNull(skf).generateSecret(ks);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    private Encryption(String encryptionKey){
        String encryptionScheme = DESEDE_ENCRYPTION_SCHEME;
        byte[] arrayBytes = new byte[0];
        try {
            arrayBytes = encryptionKey.getBytes(UNICODE_FORMAT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        KeySpec ks = null;
        try {
            ks = new DESedeKeySpec(arrayBytes);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        SecretKeyFactory skf = null;
        try {
            skf = SecretKeyFactory.getInstance(encryptionScheme);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            cipher = Cipher.getInstance(encryptionScheme);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            key = Objects.requireNonNull(skf).generateSecret(ks);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    private Encryption(String encryptionKey, String encryptionScheme){
        byte[] arrayBytes = new byte[0];
        try {
            arrayBytes = encryptionKey.getBytes(UNICODE_FORMAT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        KeySpec ks = null;
        try {
            ks = new DESedeKeySpec(arrayBytes);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        SecretKeyFactory skf = null;
        try {
            skf = SecretKeyFactory.getInstance(encryptionScheme);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            cipher = Cipher.getInstance(encryptionScheme);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            key = Objects.requireNonNull(skf).generateSecret(ks);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    public static String encrypt(String unencryptedString) {
        String encryptedString = null;
        Encryption encryption = new Encryption();
        try {
            encryption.cipher.init(Cipher.ENCRYPT_MODE, encryption.key);
            byte[] plainText = unencryptedString.getBytes(UNICODE_FORMAT);
            byte[] encryptedText = encryption.cipher.doFinal(plainText);
            encryptedString = Base64.getEncoder().encodeToString(encryptedText);
        } catch (Exception e) {
            e.printStackTrace();
            return Constants.SKY_DELIMITER;
        }
        return encryptedString;
    }


    public static String decrypt(String encryptedString) {
        String decryptedText=null;
        Encryption encryption = new Encryption();
        try {
            encryption.cipher.init(Cipher.DECRYPT_MODE, encryption.key);
            byte[] encryptedText = Base64.getDecoder().decode(encryptedString);
            byte[] plainText = encryption.cipher.doFinal(encryptedText);
            decryptedText= new String(plainText);
        } catch (Exception e) {
            e.printStackTrace();
            return Constants.SKY_DELIMITER;
        }
        return decryptedText;
    }

    public static String encrypto(String unencryptedString){
        Crypto c = new Crypto();
        String d = c.encrypt(_key, unencryptedString);
        JvmManager.gc(c);
        return HashUtils.base64Encode(d);
    }

    public static String decrypto(String encryptedString){
        Crypto c = new Crypto();
        String d = c.decrypt(_key, HashUtils.base64Decode(encryptedString));
        JvmManager.gc(c);
        return d;
    }

}

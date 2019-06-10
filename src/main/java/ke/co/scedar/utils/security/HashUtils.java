package ke.co.scedar.utils.security;

import ke.co.scedar.utils.ScedarByte;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HashUtils {

    /**
     *
     * @param clearText
     *      String value of text to be encoded in base64
     * @return
     *      String value of encoded text provided
     */
    public static String base64Encode(String clearText){
        return Base64.getEncoder().withoutPadding().encodeToString(clearText.getBytes());
    }

    public static String base64Encode(Byte[] bytea){
        return Base64.getEncoder().withoutPadding().encodeToString(ScedarByte.byteObjectArrToByteArr(bytea));
    }

    public static String base64Encode(byte[] bytea){
        return Base64.getEncoder().withoutPadding().encodeToString(bytea);
    }

    /**
     *
     * @param hash
     *      String value of encoded base64 text
     * @return
     *      String value of decoded base64 text provided
     */
    public static String base64Decode(String hash){
        return new String(Base64.getDecoder().decode(hash), StandardCharsets.UTF_8);
    }

    public static Byte[] base64DecodeToBytes(String hash){
        return ScedarByte.byteArrToByteObjectArr(Base64.getDecoder().decode(hash));
    }

    public static byte[] base64DecodeTo_bytes(String hash){
        return Base64.getDecoder().decode(hash);
    }

    public static Object fromString( String s ) throws IOException,
            ClassNotFoundException {
        byte [] data = Base64.getDecoder().decode( s );
        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(  data ) );
        Object o  = ois.readObject();
        ois.close();
        return o;
    }

    /**
     *
     * @param base
     *      Base String to be hashed to SHA256 string
     * @return
     *      Hashed SHA256 base String
     */
    public static String SHA256(String base){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(base.getBytes());
            StringBuilder hash = new StringBuilder();
            for (byte b : digest.digest()) hash.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            return hash.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}

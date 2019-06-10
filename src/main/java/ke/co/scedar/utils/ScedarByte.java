package ke.co.scedar.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * sls-api (ke.co.scedar.utils)
 * Created by: elon
 * On: 04 Jul, 2018 7/4/18 10:22 PM
 **/
public class ScedarByte {

    public static Byte[] byteArrToByteObjectArr(byte[] bytea){
        Byte[] byteObjects = new Byte[bytea.length];
        int i=0;
        for(byte b: bytea)
            byteObjects[i++] = b;  // Autoboxing.
        return byteObjects;
    }

    public static byte[] byteObjectArrToByteArr(Byte[] bytea){
        byte[] bytes = new byte[bytea.length];
        int j=0;
        for(Byte b: bytea)
            bytes[j++] = b;
        return bytes;
    }

    public static Byte[] convertToBytea(File path){
        return convertToBytea(path.toPath());
    }

    public static Byte[] convertToBytea(String path){
        return convertToBytea(Paths.get(path));
    }

    public static Byte[] convertToBytea(Path path){
        try {
            return byteArrToByteObjectArr(Files.readAllBytes(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Path convertToFile(String path, Byte[] bytea){
        return convertToFile(Paths.get(path), byteObjectArrToByteArr(bytea));
    }

    public static Path convertToFile(String path, byte[] bytea){
        return convertToFile(Paths.get(path), bytea);
    }

    public static Path convertToFile(Path path, byte[] bytea){
        try {
            return Files.write(path, bytea);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

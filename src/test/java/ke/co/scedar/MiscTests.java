package ke.co.scedar;

import org.junit.jupiter.api.Test;

public class MiscTests {

    @Test public void testHashOnRegex(){
        String testStr = "Hello world. We are replacing world everywhere " +
                "world exists with a world version that has something extra";

        System.out.println(testStr.replaceAll("world", "##temp_34234124_world"));
    }

}

package ke.co.scedar.utils;

/**
 * sls-api (ke.co.scedar.utils.memory)
 * Created by: elon
 * On: 04 Jul, 2018 7/4/18 9:22 PM
 **/
public class JvmManager {

    public static void gc(boolean withSysGc, Object... objects){
        gc(objects);
        if(withSysGc) System.gc();
    }

    public static void gc(Object... objects){
        for (Object o : objects){
            o = null;
        }
    }

}

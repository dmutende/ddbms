package ke.co.scedar.api;

import io.undertow.Handlers;
import io.undertow.Undertow;

public class Server {

    private static Undertow server = null;

    public static int port = 8003;
    public static String host = "0.0.0.0";

    public static void start(int port, String host){
        Server.port = port;
        Server.host = host;
    }

    public static void start(){
        server = Undertow.builder()
                .addHttpListener(port, host)
                .setHandler(Handlers.path()
                        .addPrefixPath("/", Routes.ping())
                        .addPrefixPath("/execute-query", Routes.executeQuery())
                ).build();
        server.start();
    }

    public static void stop(){
        if(server != null) server.stop();
    }

    public static void destroy(){
        stop();
        host = null;
        server = null;
    }
}

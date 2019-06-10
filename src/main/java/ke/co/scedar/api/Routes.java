package ke.co.scedar.api;

import io.undertow.Handlers;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.util.Methods;
import ke.co.scedar.api.handlers.Ping;
import ke.co.scedar.api.handlers.utils.CorsHandler;
import ke.co.scedar.api.handlers.ExecuteQuery;
import ke.co.scedar.api.handlers.utils.FallBack;
import ke.co.scedar.api.handlers.utils.InvalidMethod;

public class Routes {

    public static RoutingHandler ping(){
        return Handlers.routing()
                .get("/", new Ping())
                .add(Methods.OPTIONS, "/*", new CorsHandler())
                .setInvalidMethodHandler(new InvalidMethod())
                .setFallbackHandler(new FallBack());
    }

    public static RoutingHandler executeQuery(){
        return Handlers.routing()
                .post("/", new BlockingHandler(new ExecuteQuery()))
                .add(Methods.OPTIONS, "/*", new CorsHandler())
                .setInvalidMethodHandler(new InvalidMethod())
                .setFallbackHandler(new FallBack());
    }

}

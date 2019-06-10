package ke.co.scedar.api.handlers;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.scedar.api.handlers.utils.CustomHandler;
import ke.co.scedar.utils.Constants;

public class Ping extends CustomHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        super.handleRequest(exchange);

        String jsonResponse = "{" +
                    "\"status\": \"ALIVE\", " +
                    "\"message\": \"We're now accepting queries :)\" }";
        send(exchange, jsonResponse, StatusCodes.OK, Constants.applicationJson);
    }
}

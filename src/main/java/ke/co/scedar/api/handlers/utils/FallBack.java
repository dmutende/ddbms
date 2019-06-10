package ke.co.scedar.api.handlers.utils;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.scedar.utils.ExceptionRepresentation;

public class FallBack extends CustomHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) {

        super.handleRequest(exchange);

        send(exchange, new ExceptionRepresentation(
                "URI Not Found",
                exchange.getRequestURI(),
                "URI "+exchange.getRequestURI()+" not found on server",
                StatusCodes.NOT_FOUND,
                exchange.getRequestMethod()
        ), StatusCodes.NOT_FOUND);
    }
}
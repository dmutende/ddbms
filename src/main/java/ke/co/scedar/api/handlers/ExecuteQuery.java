package ke.co.scedar.api.handlers;

import gudusoft.gsqlparser.EDbVendor;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.scedar.api.handlers.utils.CustomHandler;
import ke.co.scedar.api.payloads.ExecuteQueryPayload;
import ke.co.scedar.db.fragment_schema.catalog.QueryRouter;
import ke.co.scedar.parser.SqlParser;
import ke.co.scedar.parser.SqlStatementComponents;
import ke.co.scedar.utils.Constants;
import ke.co.scedar.utils.ExceptionRepresentation;
import ke.co.scedar.utils.Logging;

public class ExecuteQuery extends CustomHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        super.handleRequest(exchange);

        ExecuteQueryPayload payload = (ExecuteQueryPayload)
                getBodyObject(exchange, ExecuteQueryPayload.class);

        if(payload == null){
            send(exchange, new ExceptionRepresentation(
                    exchange.getQueryParameters().get(Constants.MARSHALL_ERROR).getFirst(),
                    exchange.getRequestURI(),
                    "Error: Unable to understand payload.",
                    StatusCodes.INTERNAL_SERVER_ERROR,
                    exchange.getRequestMethod()
            ), StatusCodes.INTERNAL_SERVER_ERROR);
            return;
        }

        //Parse Query and verify it against existing application fragment schema
        SqlStatementComponents sqlStatementComponents = new SqlParser().parse(
                payload.getQuery(), EDbVendor.dbvmysql
        );

        if(sqlStatementComponents.hasErrors()){
            send(exchange, new ExceptionRepresentation(
                    sqlStatementComponents.getErrorMessage(),
                    exchange.getRequestURI(),
                    "There were "+sqlStatementComponents.getErrorCount()+" error(s) in your sql statement",
                    StatusCodes.NOT_ACCEPTABLE,
                    exchange.getRequestMethod()
            ), StatusCodes.NOT_ACCEPTABLE);
            return;
        }

        //Choose where to execute query
        Logging.log();
        Logging.log("***************** Query Router *****************");
        QueryRouter.route(sqlStatementComponents);
        Logging.log("***************** END of Query Router *****************");

        if(sqlStatementComponents.hasErrors()){
            send(exchange, new ExceptionRepresentation(
                    sqlStatementComponents.getErrorMessage(),
                    exchange.getRequestURI(),
                    "There were "+sqlStatementComponents.getErrorCount()+" error(s) while processing your request",
                    StatusCodes.NOT_ACCEPTABLE,
                    exchange.getRequestMethod()
            ), StatusCodes.NOT_ACCEPTABLE);
            return;
        }

        send(exchange, sqlStatementComponents.getQueryResultsManager()
                .convertResultSetToObject(true), StatusCodes.OK);
    }
}

package handlers;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.net.httpserver.*;
import dao.DataAccessException;
import request_result.ClearResult;
import request_result.RegisterRequest;
import request_result.RegisterResult;
import serialize_deserialize.Json_Converter;
import service.ClearService;
import service.RegisterService;

/**
 * the class that handles requests to clear the database
 */
public class ClearHandler implements HttpHandler {

    private static Logger logger;

    public ClearHandler() {
        logger = Logger.getLogger("familymapserver");
    }

    /**
     * handles the requests to clear the database.
     * Note that this method will permanently delete all data in the database, so should be used with caution.
     * @param exchange the http request to clear the database
     * @throws IOException
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;
        try {
            logger.log(Level.FINEST, "In ClearHandler");

            // Get the HTTP request headers
            Headers reqHeaders = exchange.getRequestHeaders();

            // Create ClearService object
            ClearService service = new ClearService();
            ClearResult result = new ClearResult();
            try {
                result = service.clear();
            } catch (DataAccessException e) {
                logger.log(Level.SEVERE, "Error encountered while closing database", e);
                result = new ClearResult(false, e.getMessage());
            }


            Json_Converter converter = new Json_Converter();
            String jsonResult = converter.serialize(result, ClearResult.class);
            logger.log(Level.FINEST, "Serializing Clear COMPLETE");
            logger.log(Level.FINEST, "json string generated" + jsonResult);

            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);

            // Get the response body output stream.
            OutputStream respBody = exchange.getResponseBody();

            // Write the JSON string to the output stream.
            converter.convertStringToOutputStream(jsonResult, respBody);
            logger.log(Level.FINEST, "COMPLETED converting string to OutputStream");


            // Close the output stream.  This is how Java knows we are done
            // sending data and the response is complete/
            respBody.close();
            exchange.getResponseBody().close();
            logger.log(Level.FINEST, "Setting success to true");
            success = true;

            if (!success) {
                // The HTTP request was invalid somehow, so we return a "bad request"
                // status code to the client.
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                exchange.getResponseBody().close();
            }
        } catch (IOException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();
            logger.log(Level.SEVERE, "Internal server error: ", e);
        }
    }
}

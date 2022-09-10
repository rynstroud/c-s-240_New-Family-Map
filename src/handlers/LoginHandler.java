package handlers;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.net.httpserver.*;
import dao.DataAccessException;
import request_result.LoginRequest;
import request_result.LoginResult;
import serialize_deserialize.Json_Converter;
import service.LoginService;

/**
 * the class that handles requests to log into the server
 */
public class LoginHandler implements HttpHandler {

    private static Logger logger;

    public LoginHandler() {
        logger = Logger.getLogger("familymapserver");
    }

    /**
     * the method that handles the request to log into a server. It sends a response detailing whether or not the method was successful,
     * along with details of the user login info and new auth token
     * @param exchange the http request to log in
     */
    @Override
    public void handle(HttpExchange exchange) {
        boolean success = false;
        try {
            logger.log(Level.FINEST, "In LoginHandler");
            if (exchange.getRequestMethod().toLowerCase().equals("post")) {

                // Get the request body input stream
                InputStream reqBody = exchange.getRequestBody();

                //convert from inputStream to LoginRequest object
                Json_Converter converter = new Json_Converter();
                LoginRequest request = Json_Converter.deserialize(reqBody, LoginRequest.class);
                if (request.getUsername() == null)
                    logger.log(Level.SEVERE, "ERROR: request failed to initialize associatedUsername");
                logger.log(Level.FINEST, "Deserializing Login COMPLETE");

                LoginService service = new LoginService(request);
                LoginResult result = service.login();

                logger.log(Level.FINEST, "Setting success to " + result.isSuccess());
                success = result.isSuccess();

                //Converts the result to a JSON string
                String jsonResult = Json_Converter.serialize(result, LoginResult.class);
                logger.log(Level.FINEST, "Serializing Login COMPLETE");
                logger.log(Level.FINEST, "json string generated" + jsonResult);
                if (success) {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);

                    // Get the response body output stream.
                    OutputStream respBody = exchange.getResponseBody();

                    // Write the JSON string to the output stream.
                    converter.convertStringToOutputStream(jsonResult, respBody);
                    logger.log(Level.FINEST, "COMPLETED converting string to OutputStream");


                    // Close the output stream.  This is how Java knows we are done
                    // sending data and the response is complete/
                    respBody.close();
                } else {
                    // The HTTP request was invalid somehow, so we return a "bad request"
                    // status code to the client.
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                    // Get the response body output stream.
                    OutputStream respBody = exchange.getResponseBody();

                    // Write the JSON string to the output stream.
                    converter.convertStringToOutputStream(jsonResult, respBody);
                    logger.log(Level.FINEST, "COMPLETED converting string to OutputStream");
                    respBody.close();
                }
                exchange.getResponseBody().close();
            }
        } catch (IOException e) {
            try {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
                exchange.getResponseBody().close();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Error found in sending response errors: " + ex.getMessage(), ex);
            }
            logger.log(Level.SEVERE, "Error: " + e.getMessage(), e);
        }
    }
}

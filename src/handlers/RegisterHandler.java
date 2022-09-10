package handlers;

import java.io.*;
import java.net.*;
import java.util.logging.*;

import com.sun.net.httpserver.*;
import dao.DataAccessException;
import serialize_deserialize.Json_Converter;
import request_result.*;
import service.RegisterService;

/**
 * the class that handles requests to register a new user with the server
 */
public class RegisterHandler implements HttpHandler {

    private static Logger logger;

    public RegisterHandler() {
        logger = Logger.getLogger("familymapserver");
    }

    /**
     * the method that handles the request to register a new user with the server.
     * It sends a response detailing whether or not the method was successful,
     * along with details of the user registration info and new authtoken
     * @param exchange the http request to register a new user
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;
        try {
            logger.log(Level.FINEST, "In RegisterHandler");
            if (exchange.getRequestMethod().toLowerCase().equals("post")) {

                // Get the HTTP request headers
                Headers reqHeaders = exchange.getRequestHeaders();

                // Get the request body input stream
                InputStream reqBody = exchange.getRequestBody();

                //convert from inputStream to RegisterRequest object
                Json_Converter converter = new Json_Converter();
                RegisterRequest request = converter.deserialize(reqBody, RegisterRequest.class);
                if (request.getFirstName() == null)
                    logger.log(Level.SEVERE, "ERROR: request failed to initialize first name");
                logger.log(Level.FINEST, "Deserializing Register COMPLETE");

                //calling the service to interact with the dao's
                RegisterService service = new RegisterService(request);
                RegisterResult result = service.register();

                logger.log(Level.FINEST, "Setting success equal to " + result.isSuccess());
                success = result.isSuccess();

                // converting result back to JSON string
                String jsonResult = converter.serialize(result, RegisterResult.class);
                logger.log(Level.FINEST, "Serializing Register COMPLETE");
                logger.log(Level.FINEST, "json string generated" + jsonResult);

                if (success) {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);

                    // Get the response body output stream.
                    OutputStream respBody = exchange.getResponseBody();

                    // Write the JSON string to the output stream.
                    converter.convertStringToOutputStream(jsonResult, respBody);
                    logger.log(Level.FINEST, "COMPLETED converting string to OutputStream");


                    // Close the output stream.
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
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();
            e.printStackTrace();
        } catch (DataAccessException e) {
            RegisterResult result = new RegisterResult(null, "", "", false);
            System.out.println(result.getInternal_Error());
        }
    }
}

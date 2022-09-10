package handlers;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.net.httpserver.*;
import request_result.*;
import serialize_deserialize.Json_Converter;
import service.FillService;

/**
 * the class that handles requests to fill the database with fake data for ancestors
 */
public class FillHandler implements HttpHandler {
    private static Logger logger;
    private final static int DEFAULT_NUM_GENERATIONS = 4;
    private final static int LENGTH_OF_FILL = 6;

    public FillHandler() {
        logger = Logger.getLogger("familymapserver");
    }

    //obtaining the username from the request URI
    private String getUsername(HttpExchange exchange) {
        logger.log(Level.FINEST, "Obtaining associatedUsername from exchange");
        String temp = exchange.getRequestURI().toString().substring(LENGTH_OF_FILL);
        if (temp.indexOf('/') == -1) {
            logger.log(Level.FINEST, "associatedUsername obtained from exchange URI: " + temp);
            return temp;
        }
        String username = temp.substring(0, temp.indexOf('/'));
        logger.log(Level.FINEST, "associatedUsername obtained from exchange URI: " + username);
        return username;
    }

    //obtaining the number of generations to be filled from the request URI
    private int getNumGenerations(HttpExchange exchange, String username) {
        logger.log(Level.FINEST, "Obtaining number of generations from exchange");
        String temp = exchange.getRequestURI().toString().substring(LENGTH_OF_FILL + username.length());
        String numGenerations = temp.substring(temp.indexOf('/') + 1);
        logger.log(Level.FINEST, "numGenerations obtained from exchange URI: " + numGenerations);
        if (numGenerations.length() != 0) return Integer.parseInt(numGenerations);
        else return DEFAULT_NUM_GENERATIONS;
    }

    /**
     * the method that handles the request to fill the database with fake ancestor data.
     * It sends a response detailing whether or not the method was successful,
     * along with details like how many people and events were created if successful
     * @param exchange the http request to fill the database
     */
    @Override
    public void handle(HttpExchange exchange) {
        boolean success = false;
        try {
            logger.log(Level.FINEST, "In FillHandler");
            if (exchange.getRequestMethod().equalsIgnoreCase("post")) {

                // Create FillService object
                FillService service = new FillService();
                FillResult result;
                try {
                    logger.log(Level.FINEST, "Attempting to fill through fillService");
                    result = service.fill(getUsername(exchange), getNumGenerations(exchange, getUsername(exchange)));
                } catch (Exception e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    result = new FillResult(false, e.getMessage());
                }

                //Converts the result to a JSON string
                Json_Converter converter = new Json_Converter();
                String jsonResult = converter.serialize(result, FillResult.class);
                logger.log(Level.FINEST, "Serializing Fill COMPLETE");
                logger.log(Level.FINEST, "json string generated" + jsonResult);

                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);

                // Get the response body output stream.
                OutputStream respBody = exchange.getResponseBody();

                // Write the JSON string to the output stream.
                converter.convertStringToOutputStream(jsonResult, respBody);
                logger.log(Level.FINEST, "COMPLETED converting string to OutputStream");


                // Close the output stream.
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
            }
        } catch(Exception e){
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

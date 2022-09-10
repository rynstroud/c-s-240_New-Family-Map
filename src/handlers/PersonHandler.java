package handlers;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.net.httpserver.*;
import dao.DataAccessException;
import model.AuthToken;
import model.Person;
import model.User;
import request_result.PersonsResult;
import request_result.PersonResult;
import serialize_deserialize.Json_Converter;
import service.PersonService;
import service.PersonsService;

/**
 * the class that handles requests to return a certain person or all people associated with a certain person
 */
public class PersonHandler implements HttpHandler {

    private static Logger logger;
    private final static int LENGTH_OF_PERSON = 8;

    public PersonHandler() {
        logger = Logger.getLogger("familymapserver");
    }

    //This returns the personID that was sent in the request URI
    private String getPersonID(HttpExchange exchange) {
        try {
            logger.log(Level.FINEST, "Obtaining personID from exchange");
            String temp = exchange.getRequestURI().toString().substring(LENGTH_OF_PERSON);
            if (temp.indexOf('/') == -1) {
                logger.log(Level.FINEST, "personID obtained from exchange URI: " + temp);
                return temp;
            }
            String personID = temp.substring(0, temp.indexOf('/'));
            logger.log(Level.FINEST, "personID obtained from exchange URI: " + personID);
            return personID;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error found in getPersonID: " + e.getMessage(), e);
            return "";
        }
    }

    /**
     * the default handle method. it calls the authorizing request handler to authorize the associated auth token and then calls the handleAfterAuth method
     * @param exchange the http request to find person/persons
     */
    @Override
    public void handle(HttpExchange exchange) {
        AuthorizingRequestHandler handler = new AuthorizingRequestHandler();
        handler.handle(exchange);
    }

    /**
     * the method that handles the request to find a person or all people associated with a person. It sends a response detailing whether or not the method was successful,
     * along with details of the person/persons found if it passed
     * @param exchange the http request to find person/persons
     */
    public void handleAfterAuth(HttpExchange exchange, AuthToken token, boolean goodAuth) {
        boolean success = false;
        OutputStream outputStream = exchange.getResponseBody();
        logger.log(Level.FINEST,"Exchange request URI for person/(s): " + exchange.getRequestURI().toString());
        if (exchange.getRequestURI().toString().equals("/person")) {
            //return a response body with ALL persons
            try {
                handleBadAuth(exchange, goodAuth);

                PersonsResult result = findPersons(exchange, token.getUsername());
                success = result.isSuccess();

                //sending the appropriate response based on the result
                if (success) sendGoodResponse(exchange, result, null);
                else sendBadResponse(exchange, result, null);

            //Error catching
            } catch (IOException e) {
                try {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
                    exchange.getResponseBody().close();
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, "Error found in sending response errors: " + ex.getMessage(), ex);
                    PersonResult result = new PersonResult(false, "Error: " + e.getMessage());
                }
                PersonResult result = new PersonResult(false, "Error: " + e.getMessage());
                logger.log(Level.SEVERE, "Error: " + e.getMessage(), e);
            } catch (DataAccessException e) {
                PersonsResult result = new PersonsResult(false, e.getMessage());
                System.out.println(result.getInternal_Error());
            }
        }


        else {
            //return a response body with the specified person
            try {
                handleBadAuth(exchange, goodAuth);

                PersonResult result = findPerson(exchange, token.getUsername());
                logger.log(Level.FINEST, "Setting success equal to " + result.isSuccess());
                success = result.isSuccess();
                if (success) sendGoodResponse(exchange, null, result);
                else sendBadResponse(exchange,null, result);

             //Error catching
            } catch (IOException e) {
                try {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
                    exchange.getResponseBody().close();
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, "Error found in sending response errors: " + ex.getMessage(), ex);
                    PersonResult result = new PersonResult(false, "Error: " + e.getMessage());
                }
                PersonResult result = new PersonResult(false, "Error: " + e.getMessage());
                logger.log(Level.SEVERE, "Error: " + e.getMessage(), e);
            } catch (DataAccessException e) {
                PersonResult result = new PersonResult(false, e.getMessage());
                System.out.println(result.getInternal_Error());
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Unknown exception encountered in findPerson.");
            }
        }
    }

    //calling the All Persons specific service
    private PersonsResult findPersons(HttpExchange exchange, String username) throws DataAccessException, IOException {
        PersonsService service = new PersonsService();
        PersonsResult result = service.findAllPersons(username);
        return result;
    }

    //calling the find one person specific service
    private PersonResult findPerson(HttpExchange exchange, String username) throws DataAccessException, IOException {
        PersonService service = new PersonService();
        PersonResult result = service.findPerson(getPersonID(exchange), username);
        return result;
    }

    //Sends the 200 code for a successful exchange
    private void sendGoodResponse(HttpExchange exchange, PersonsResult result1, PersonResult result2) throws IOException {
        Json_Converter converter = new Json_Converter();
        String jsonResult;
        if (result1 != null) jsonResult = Json_Converter.serialize(result1, PersonsResult.class);
        else jsonResult = Json_Converter.serialize(result2, PersonResult.class);
        logger.log(Level.FINEST, "Serializing PersonsResult COMPLETE");
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
    }

    //Sends the 400 code for an unsuccessful exchange
    private void sendBadResponse(HttpExchange exchange, PersonsResult result1, PersonResult result2) throws IOException {
        Json_Converter converter = new Json_Converter();
        String jsonResult;
        if (result1 != null) jsonResult = Json_Converter.serialize(result1, PersonsResult.class);
        else jsonResult = Json_Converter.serialize(result2, PersonResult.class);
        logger.log(Level.FINEST, "Serializing PersonsResult COMPLETE");
        logger.log(Level.FINEST, "json string generated" + jsonResult);

        exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);

        // Get the response body output stream.
        OutputStream respBody = exchange.getResponseBody();

        // Write the JSON string to the output stream.
        converter.convertStringToOutputStream(jsonResult, respBody);
        logger.log(Level.FINEST, "COMPLETED converting string to OutputStream");


        // Close the output stream.  This is how Java knows we are done
        // sending data and the response is complete/
        respBody.close();
        exchange.getResponseBody().close();
    }

    //Sending a response showing the request was unable to be authenticated
    private void handleBadAuth(HttpExchange exchange, boolean goodAuth) throws IOException {
        if (!goodAuth) {
            PersonsResult result = new PersonsResult();
            result.setSuccess(false);
            result.setMessage("Error: Unable to authenticate user.");
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);

            // Get the response body output stream.
            OutputStream respBody = exchange.getResponseBody();

            //converting object to a JSON string
            Json_Converter converter = new Json_Converter();
            String jsonResult = Json_Converter.serialize(result, PersonsResult.class);
            logger.log(Level.FINEST, "Serializing PersonsResult COMPLETE");
            logger.log(Level.FINEST, "json string generated" + jsonResult);

            // Write the JSON string to the output stream.
            converter.convertStringToOutputStream(jsonResult, respBody);
            logger.log(Level.FINEST, "COMPLETED converting string to OutputStream");


            // Close the output stream.  This is how Java knows we are done
            // sending data and the response is complete/
            exchange.getResponseBody().close();
        }
    }

}

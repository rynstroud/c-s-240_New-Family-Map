package handlers;


import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.net.httpserver.*;
import dao.DataAccessException;
import model.AuthToken;
import request_result.EventResult;
import request_result.EventsResult;
import request_result.PersonsResult;
import serialize_deserialize.Json_Converter;
import service.EventService;
import service.EventsService;

/**
 * the class that handles requests to return a certain event or all events associated with a certain person
 */
public class EventHandler implements HttpHandler {

    private static Logger logger;
    private final static int LENGTH_OF_EVENT = 7;

    public EventHandler() {
        logger = Logger.getLogger("familymapserver");
    }

    //This returns the eventID that was sent in the request URI
    private String getEventID(HttpExchange exchange) {
        logger.log(Level.FINEST, "Obtaining eventID from exchange");
        logger.log(Level.FINEST, "Current request URI: " + exchange.getRequestURI().toString());
        String temp = exchange.getRequestURI().toString().substring(LENGTH_OF_EVENT);
        if (temp.indexOf('/') == -1) {
            logger.log(Level.FINEST, "eventID obtained from exchange URI: " + temp);
            return temp;
        }
        String eventID = temp.substring(0, temp.indexOf('/'));
        logger.log(Level.FINEST, "eventID obtained from exchange URI: " + eventID);
        return eventID;
    }

    /**
     * the default handle method. it calls the authorizing request handler to authorize the associated auth token and then calls the handleAfterAuth method
     * @param exchange the http request to find event/events
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        AuthorizingRequestHandler handler = new AuthorizingRequestHandler();
        handler.handle(exchange);
    }

    /**
     * the method that handles the request to find a certain event or all events associated with a person.
     * It sends a response detailing whether or not the method was successful,
     * along with details of the event/events found if it passed
     * @param exchange the http request to find event/events
     */
    public void handleAfterAuth(HttpExchange exchange, AuthToken token, boolean goodAuth) throws IOException {
        boolean success = false;
        OutputStream outputStream = exchange.getResponseBody();
        if (exchange.getRequestURI().toString().equals("/event")) {

            //return a response body with ALL events
            try {

                //takes care of the cases in which the request was not authorized
                handleBadAuth(exchange, goodAuth);

                EventsService service = new EventsService();
                EventsResult result = service.findAllEvents(token.getUsername());

                //Converting the result into a string
                Json_Converter converter = new Json_Converter();
                String jsonResult = Json_Converter.serialize(result, EventsResult.class);
                logger.log(Level.FINEST, "Serializing EventsResult COMPLETE");
                logger.log(Level.FINEST, "json string generated" + jsonResult);

                success = result.isSuccess();

                //Sends a bad request code if the service failed to find the events
                if (!success) exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                else exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);

                // Get the response body output stream.
                OutputStream respBody = exchange.getResponseBody();

                // Write the JSON string to the output stream.
                converter.convertStringToOutputStream(jsonResult, respBody);
                logger.log(Level.FINEST, "COMPLETED converting string to OutputStream");


                // Close the output stream.  This is how Java knows we are done
                // sending data and the response is complete/
                respBody.close();
                exchange.getResponseBody().close();

            } catch (IOException e) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
                exchange.getResponseBody().close();
                e.printStackTrace();
            } catch (DataAccessException e) {
                EventsResult result = new EventsResult(false, e.getMessage());
                System.out.println(result.getInternal_Error());
            }
        }
        else {
            //return a response body with the specified event
            try {

                //takes care of the cases in which the request was not authorized
                handleBadAuth(exchange, goodAuth);

                EventService service = new EventService();
                EventResult result = service.findEvent(getEventID(exchange), token.getUsername());

                Json_Converter converter = new Json_Converter();
                String jsonResult = Json_Converter.serialize(result, EventResult.class);
                logger.log(Level.FINEST, "Serializing EventResult COMPLETE");
                logger.log(Level.FINEST, "json string generated" + jsonResult);

                //Sends a bad request code if the service failed to find the event
                if ((result.getMessage() != null) && ((result.getMessage().equals("EventID does not belong to user.")) || (!result.isSuccess()))) {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                }
                else exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);

                // Get the response body output stream.
                OutputStream respBody = exchange.getResponseBody();

                // Write the JSON string to the output stream.
                converter.convertStringToOutputStream(jsonResult, respBody);
                logger.log(Level.FINEST, "COMPLETED converting string to OutputStream");

                // Close the output stream.
                respBody.close();
                exchange.getResponseBody().close();
            } catch (IOException e) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
                exchange.getResponseBody().close();
                EventResult result = new EventResult(false, e.getMessage());
                e.printStackTrace();
            } catch (DataAccessException e) {
                EventResult result = new EventResult(false, e.getMessage());
                System.out.println(result.getInternal_Error());
            }
        }
    }

    /**
     * handles the event result to be returned if the event is unauthorized
     * @param exchange
     */
    private void handleBadAuth(HttpExchange exchange, boolean goodAuth) throws IOException {
        if (!goodAuth) {
            EventsResult result = new EventsResult();
            result.setSuccess(false);
            result.setMessage("Error: Unable to authenticate user.");
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);

            // Get the response body output stream.
            OutputStream respBody = exchange.getResponseBody();

            Json_Converter converter = new Json_Converter();
            String jsonResult = Json_Converter.serialize(result, EventsResult.class);
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

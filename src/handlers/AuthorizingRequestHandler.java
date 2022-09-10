package handlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.DataAccessException;
import model.AuthToken;
import service.AuthTokenService;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * the class that handles requests requiring authentication of authtokens
 */
public class AuthorizingRequestHandler implements HttpHandler {
    //Parent for handlers that require authorization
    //Read the authorization header to get the auth_token. Return a 401 if missing or invalid
    private static Logger logger;

    public AuthorizingRequestHandler() {
        logger = Logger.getLogger("familymapserver");
    }

    /**
     * handles the http requests from the client that require authentication of authtokens.
     * It authenticates the request, then sends the exchange to be handled by the appropriate handler for the rest of the request.
     * @param exchange the http request to be authenticated
     */
    @Override
    public void handle(HttpExchange exchange) {
        // Get the HTTP request headers
        Headers reqHeaders = exchange.getRequestHeaders();

        // Extract the auth token from the "Authorization" header
        String authToken = reqHeaders.getFirst("Authorization");

        try {
            //validating the auth token
            AuthTokenService tokenService = new AuthTokenService();

            AuthToken foundToken = tokenService.findAuthToken(authToken);
            if (foundToken != null) {

                //there are only two handlers which require authorization
                if (exchange.getRequestURI().toString().contains("/event")) {
                    EventHandler eventHandler = new EventHandler();
                    eventHandler.handleAfterAuth(exchange, foundToken, true);
                    return;
                }
                else if (exchange.getRequestURI().toString().contains("/person")) {
                    PersonHandler personHandler = new PersonHandler();
                    personHandler.handleAfterAuth(exchange, foundToken, true);
                    return;
                }
                else {
                    logger.log(Level.SEVERE, "Request URI here was: " + exchange.getRequestURI().toString());
                }
            }
            else {
                //Calling the handlers with a bad auth token flag
                //there are only two handlers which require authorization
                if (exchange.getRequestURI().toString().contains("/event")) {
                    EventHandler eventHandler = new EventHandler();
                    eventHandler.handleAfterAuth(exchange, null, false);
                    return;
                }
                else if (exchange.getRequestURI().toString().contains("/person")) {
                    PersonHandler personHandler = new PersonHandler();
                    personHandler.handleAfterAuth(exchange, null, false);
                    return;
                }
            }

        //None of these should ever be called unless the exchange was corrupted.
            // The errors should be caught in the other handlers called
        } catch (IOException e) {
            try {
                exchange.getResponseBody().close();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Error found in sending response errors: " + ex.getMessage(), ex);
            }
            logger.log(Level.SEVERE, "Error: " + e.getMessage(), e);
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, "Data access exception caught by AuthorizingRequestHandler()\n");
            e.printStackTrace();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Something went wrong while handling the authorization request. Sorry." + e.getMessage());
        }

    }

}

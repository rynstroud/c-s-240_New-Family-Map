package handlers;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.net.httpserver.*;
import dao.DataAccessException;
import request_result.LoadRequest;
import request_result.LoadResult;
import serialize_deserialize.Json_Converter;
import service.LoadService;

/**
 * the class that handles requests to fill the database with given data
 */
public class LoadHandler implements HttpHandler {

    private static Logger logger;

    public LoadHandler() {
        logger = Logger.getLogger("familymapserver");
    }

    /**
     * the method that handles the request to fill the database with given data.
     * It sends a response detailing whether or not the method was successful,
     * along with details like how many people, users, and events were created if successful
     * @param exchange the http request to fill the database with given data
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;
        try {
            logger.log(Level.FINEST, "In LoadHandler");
            if (exchange.getRequestMethod().equalsIgnoreCase("post")) {

                // Get the request body input stream
                InputStream reqBody = exchange.getRequestBody();

                //convert from inputStream to LoadRequest object
                Json_Converter converter = new Json_Converter();
                LoadRequest request = converter.deserialize(reqBody, LoadRequest.class);
                logger.log(Level.FINEST, "Deserializing Load COMPLETE");

                LoadService service = new LoadService();
                LoadResult result = service.load(request);

                //Converts the result to a JSON string
                String jsonResult = converter.serialize(result, LoadResult.class);
                logger.log(Level.FINEST, "Serializing Load COMPLETE");
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
                logger.log(Level.FINEST, "Setting success equals true");
                success = result.isSuccess();
            }
            if (!success) {
                // The HTTP request was invalid somehow, so we return a "bad request"
                // status code to the client.
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                exchange.getResponseBody().close();
            }
        } catch (IOException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();
            e.printStackTrace();
        } catch (DataAccessException e) {
            LoadResult result = new LoadResult(false);
            System.out.println(result.getInternal_Error());
        }
    }

}

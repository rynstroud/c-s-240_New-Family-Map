package handlers;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.sun.net.httpserver.*;

/**
 * class that handles all requests from the server that don't have another handler
 */
public class FileHandler implements HttpHandler {

    private static Logger logger;

    public FileHandler() {
        logger = Logger.getLogger("familymapserver");
    }

    /**
     * sends the server to an appropriate page based on the request.
     * if the request is null, sends it to "index.html", which is the default website.
     * @param exchange
     * @throws IOException
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;
        try {
            //we should only get "get" methods here
            if (exchange.getRequestMethod().toLowerCase().equals("get")) {
                String urlPath = exchange.getRequestURI().toString();

                //the default url path
                if ((urlPath == null) || (urlPath.equals("/") || (urlPath.equals("")))) urlPath = "/index.html";
                String filePath = "web" + urlPath;

                File returnFile = new File(filePath);
                if (returnFile.exists()) {
                    exchange.sendResponseHeaders(200,0);
                    OutputStream respBody = exchange.getResponseBody();
                    Files.copy(returnFile.toPath(), respBody);
                }
                else {
                    //Sending 404 error code for files not found
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
                    File error404 = new File("web/HTML/404.html");
                    OutputStream respBody = exchange.getResponseBody();
                    Files.copy(error404.toPath(), respBody);
                }
                exchange.getResponseBody().close();
                success = true;
            }
            if (!success) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
                exchange.getResponseBody().close();
            }
        } catch (IOException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();
            e.printStackTrace();
        }
    }

}

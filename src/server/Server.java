package server;

import java.io.*;
import java.net.*;
import java.util.logging.*;

import com.sun.net.httpserver.*;

import handlers.*;
import handlers.FileHandler;

/**
 * the class creating the full server through which everything runs
 */
public class Server {
    private HttpServer server;
    private static final int MAX_WAITING_CONNECTIONS = 12;
    private static Logger logger;

    static {
        try {
            initLog();
        } catch (IOException e) {
            System.out.println("Could not initialize log: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void initLog() throws IOException {
        Level logLevel = Level.FINE;

        logger = Logger.getLogger("familymapserver");
        logger.setLevel(logLevel);
        logger.setUseParentHandlers(false);

        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(logLevel);
        consoleHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(consoleHandler);
    }

    private void run(String portNumber) {
        logger.info("Initializing HTTP Server");

        try {
            server = HttpServer.create(
                    new InetSocketAddress(Integer.parseInt(portNumber)),
                    MAX_WAITING_CONNECTIONS);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return;
        }
        server.setExecutor(null);

        logger.info("Creating contexts");
        server.createContext("/user/register", new RegisterHandler());
        server.createContext("/user/login", new LoginHandler());
        server.createContext("/clear", new ClearHandler());
        server.createContext("/fill", new FillHandler());
        server.createContext("/load", new LoadHandler());
        server.createContext("/person", new PersonHandler());
        server.createContext("/event", new EventHandler());
        server.createContext("/", new FileHandler());

        logger.info("Starting server");
        server.start();
        logger.info("Server started");

    }

    /**
     * runs the server by calling private method run, which initializes and starts the server
     * @param args the port number of the website to be created
     */
    public static void main(String[] args) {
        String portNumber = args[0];
        new Server().run(portNumber);
    }
}

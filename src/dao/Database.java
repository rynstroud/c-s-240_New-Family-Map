package dao;

import model.Event;
import model.Person;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {
    private Connection conn;
    private AuthTokenDAO authTokenDAO;
    private EventDAO eventDAO;
    private PersonDAO personDAO;
    private UserDAO userDAO;
    private static Logger logger;

    public Database() {
        logger = Logger.getLogger("familymapserver");
    }

    public AuthTokenDAO getAuthTokenDAO() {
        return authTokenDAO;
    }

    public void setAuthTokenDAO(AuthTokenDAO authTokenDAO) {
        this.authTokenDAO = authTokenDAO;
    }

    public EventDAO getEventDAO() {
        return eventDAO;
    }

    public void setEventDAO(EventDAO eventDAO) {
        this.eventDAO = eventDAO;
    }

    public PersonDAO getPersonDAO() {
        return personDAO;
    }

    public void setPersonDAO(PersonDAO personDAO) {
        this.personDAO = personDAO;
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    private void initializeDAOs() {
        authTokenDAO = new AuthTokenDAO(conn);
        eventDAO = new EventDAO(conn);
        personDAO = new PersonDAO(conn);
        userDAO = new UserDAO(conn);
    }
    //Whenever we want to make a change to our database we will have to open a connection and use
    //Statements created by that connection to initiate transactions

    /**
     * opens a new connection between this program and the SQLite database
     * @return the connection opened between the program and the SQLite database
     * @throws DataAccessException
     */
    public Connection openConnection() throws DataAccessException {
        try {
            logger.log(Level.FINE, "Opening database connection");
            //The Structure for this Connection is driver:language:path
            //The path assumes you start in the root of your project unless given a non-relative path
            final String CONNECTION_URL = "jdbc:sqlite:database/familymap.sqlite";

            // Open a database connection to the file given in the path
            conn = DriverManager.getConnection(CONNECTION_URL);

            // Start a transaction
            conn.setAutoCommit(false);

            // Initialize DAOs with new connection
            initializeDAOs();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Unable to open connection to database", e);
            throw new DataAccessException("Unable to open connection to database");
        }

        return conn;
    }

    /**
     * returns the current open connection or opens a new one if there isnt one already open
     * @return the open connection
     * @throws DataAccessException
     */
    public Connection getConnection() throws DataAccessException {
        if(conn == null) {
            return openConnection();
        } else {
            return conn;
        }
    }

    /**
     * will not open a new connection but instead returns null if there isnt a connection at the time
     * @return conn the connection state
     */
    public Connection connectionState() { return conn; }


    //When we are done manipulating the database it is important to close the connection. This will
    //End the transaction and allow us to either commit our changes to the database or rollback any
    //changes that were made before we encountered a potential error.

    //IMPORTANT: IF YOU FAIL TO CLOSE A CONNECTION AND TRY TO REOPEN THE DATABASE THIS WILL CAUSE THE
    //DATABASE TO LOCK. YOUR CODE MUST ALWAYS INCLUDE A CLOSURE OF THE DATABASE NO MATTER WHAT ERRORS
    //OR PROBLEMS YOU ENCOUNTER

    /**
     * closes the current connection between this program and the SQLite database
     * @param commit a boolean flag telling the program whether or not to commit the changes to the database that it has made
     * @throws DataAccessException
     */
    public void closeConnection(boolean commit) throws DataAccessException {
        try {
            logger.log(Level.FINE, "Closing database connection");
            if (commit) {
                //This will commit the changes to the database
                conn.commit();
            } else {
                //If we find out something went wrong, pass a false into closeConnection and this
                //will rollback any changes we made during this connection
                conn.rollback();
            }

            conn.close();
            conn = null;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Unable to close connection to database", e);
            throw new DataAccessException("Unable to close database connection");
        }
    }

    /**
     * clears all the tables in the database
     * @throws DataAccessException
     */
    public void clearTables() throws DataAccessException {
        try (Statement stmt = conn.createStatement()){
            String sql = "DELETE FROM USERS;" + "DELETE FROM EVENTS;" + "DELETE FROM PERSONS;" + "DELETE FROM AUTHORIZATION_TOKEN;";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQL Error encountered while clearing tables: " + e.getMessage(), e);
            throw new DataAccessException("SQL Error encountered while clearing tables");
        }
    }

    /**
     * clears the provided table in the database
     * @param table the table to be cleared
     * @throws DataAccessException
     */
    public void clear(String table) throws DataAccessException {
        try (Statement stmt = conn.createStatement()){
            String sql = "DELETE FROM " + table;
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}


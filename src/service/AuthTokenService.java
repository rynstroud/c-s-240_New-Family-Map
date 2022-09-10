package service;

import dao.DataAccessException;
import dao.Database;
import model.AuthToken;

import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * the class authorizing auth tokens
 */
public class AuthTokenService {

    Database db;
    private static Logger logger;

    public AuthTokenService() {
        logger = Logger.getLogger("familymapserver");
        db = null;
    }
    /**
     * finds the authtoken of a given user, either authorizing it if it is in the database or returning null to say it is invalid
     * @param token
     * @return
     * @throws DataAccessException
     */
    public AuthToken findAuthToken(String token) throws DataAccessException {
        Database currDB = getCurrDatabase();
        try {
            //calling the auth token dao to find the given auth token
            AuthToken authToken = currDB.getAuthTokenDAO().find(token);

            currDB.closeConnection(true);

            //returning the results of the find method
            if (authToken != null) return authToken;
            else return null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Something went wrong in the AuthTokenService while trying to find auth token. Sorry!");
            currDB.closeConnection(false);
            return null;
        }

    }

    private Database getCurrDatabase() throws DataAccessException {
        if (db == null) {
            db = new Database();
            db.getConnection();
        }
        return db;
    }

    public void setDb(Database db) {
        this.db = db;
    }
}

package service;

import dao.DataAccessException;
import dao.Database;
import model.AuthToken;
import model.User;
import request_result.LoginRequest;
import request_result.LoginResult;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * holds the algorithm to log into the app
 * without interacting directly with the database
 */
public class LoginService {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
    LoginRequest r;
    private static Logger logger;

    public LoginService(LoginRequest r1) {
        logger = Logger.getLogger("familymapserver");
        r = r1;
    }

    /**
     *
     * @return an auth token
     */
    public LoginResult login() {

        Database db = new Database();

        try {
            logger.log(Level.FINE, "OPENING CONNECTION IN LOGIN SERVICE");
            db.getConnection();

            User user = db.getUserDAO().find(r.getUsername());
            //if associatedUsername or password is invalid, return that
            if ((user == null) ||
                    (!user.getPassword().equals(r.getPassword()))) {
                logger.log(Level.SEVERE, "CLOSING BAD CONNECTION IN LOGIN SERVICE");
                db.closeConnection(false);
                return new LoginResult(false, "Error: Request property missing or has invalid value");
            }

            //Creating auth token fields
            Date date = new Date();
            Timestamp timestamp = new Timestamp(date.getTime());
            AuthToken authToken = new AuthToken(db.getAuthTokenDAO().generateAuthToken(),
                    r.getUsername(), sdf.format(timestamp));
            db.getAuthTokenDAO().insert(authToken);

            logger.log(Level.FINE, "CLOSING CONNECTION IN LOGIN SERVICE");
            db.closeConnection(true);

            return new LoginResult(authToken.getToken(), r.getUsername(), user.getPersonID(), true);
        } catch (DataAccessException e) {
            try {
                logger.log(Level.SEVERE, "CLOSING BAD CONNECTION IN LOGIN SERVICE");
                db.closeConnection(false);
            } catch (DataAccessException ex) {
                logger.log(Level.SEVERE, "Error found in closing connection: " + ex.getMessage(), ex);
            }
            logger.log(Level.SEVERE, "Error: " + e.getMessage(), e);
            return new LoginResult(null, "","", false);
        } catch (Exception e) {
            try {
                logger.log(Level.SEVERE, "CLOSING BAD CONNECTION IN LOGIN SERVICE");
                db.closeConnection(false);
            } catch (DataAccessException ex) {
                logger.log(Level.SEVERE, "Error found in closing connection: " + ex.getMessage(), ex);
            }
            logger.log(Level.SEVERE, "Error: " + e.getMessage(), e);
            return new LoginResult(null, "","", false);
        }
    }
}

package service;

import dao.DataAccessException;
import dao.Database;
import model.Person;
import model.User;
import request_result.*;

import java.text.SimpleDateFormat;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * holds the algorithm to register a new user
 * without interacting directly with the database
 */
public class RegisterService {
    private static Logger logger;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
    private RegisterRequest r;
    private int DEFAULT_NUM_GENERATIONS = 4;

    public RegisterService(RegisterRequest r1) {
        logger = Logger.getLogger("familymapserver");
        r  = r1;
    }

    public RegisterService() {
        logger = Logger.getLogger("familymapserver");
    }

    public void setR(RegisterRequest r) {
        this.r = r;
    }

    /**
     * calls the PersonDAO class to generate a unique person id
     * @param db the database through which this is performed
     * @return the unique person id created
     * @throws DataAccessException
     */
    private String generatePersonId(Database db) throws DataAccessException {
        logger.log(Level.FINEST, "Generating personID");
        return db.getPersonDAO().generatePersonID();
    }

    /**
     *
     * @return the result of the registration (an auth token)
     */
    public RegisterResult register() throws DataAccessException {
        logger.log(Level.FINEST, "Registering user through RegisterService");
        Database db = new Database();

        try {
            db.getConnection();

            logger.log(Level.FINEST, "Inserting new User into database");
            if (r.getFirstName() == null) logger.log(Level.SEVERE, "ERROR: request's first name is null");
            User user = new User(r.getUsername(), r.getPassword(), r.getEmail(),
                    r.getFirstName(), r.getLastName(), r.getGender(), generatePersonId(db));
            if (user.getFirstName() == null) logger.log(Level.SEVERE, "ERROR: user's first name is null");
            db.getUserDAO().insert(user);

            logger.log(Level.FINEST, "Inserting new Person into database");
            Person person = new Person(user.getPersonID(), r.getUsername(), r.getFirstName(),
                    r.getLastName(), r.getGender(), null, null, null);

            db.getPersonDAO().insert(person);

            db.closeConnection(true);

            addAncestorData(person);

            String authStr = login(user);

            logger.log(Level.FINEST, "Returning successful RegisterResult");
            return new RegisterResult(authStr, r.getUsername(), user.getPersonID(), true);
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, "Error generated while trying to Register", e);
            if (db.connectionState() != null) db.closeConnection(false);
            String message = e.getMessage();
            if (e.getMessage().contains("Abort due to constraint violation")) message = "User already exists";
            return new RegisterResult("Error: " + message, false);
        }
    }

    private void addAncestorData(Person person) throws DataAccessException {
        FillService service = new FillService();
        service.fill(person.getAssociatedUsername(), DEFAULT_NUM_GENERATIONS);
    }

    private String login(User user) throws DataAccessException {
        LoginRequest request = new LoginRequest();
        request.setUsername(user.getAssociatedUsername());
        request.setPassword(user.getPassword());
        LoginService service = new LoginService(request);
        LoginResult result = service.login();
        return result.getAuthtoken();
    }

    @Override
    public int hashCode() {
        return r.getEmail().charAt(3) + r.getPassword().charAt(1) * r.getGender();
    }
}

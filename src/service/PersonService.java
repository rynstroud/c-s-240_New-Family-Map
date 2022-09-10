package service;

import dao.DataAccessException;
import dao.Database;
import model.Person;
import model.User;
import request_result.PersonResult;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * holds the algorithm to find a specific person
 * without interacting directly with the database
 */
public class PersonService {
    private static Logger logger;
    private Database database;

    public PersonService() {
        database = null;
        logger = Logger.getLogger("familymapserver");
    }

    /**
     *
     * @param personID the client's request to find a person
     *          in their family based on a specific personID
     * @return a single person object with the specified personID (contained in the PersonResult)
     */
    public PersonResult findPerson(String personID, String username) throws DataAccessException {
        logger.log(Level.FINER, "Finding person through PersonService");
        Database db = getCurrDatabase();

        try {
            PersonResult personResult = new PersonResult();
            if (db.connectionState() == null) {
                db.openConnection();
                if (isRelated(db.getUserDAO().find(username), personID)) {
                    if (db.getPersonDAO().find(personID) != null) setPersonResultAttributes(personID, db, personResult);
                    else personResult = null;
                }
                else {
                    personResult.setSuccess(false);
                    personResult.setMessage("PersonID does not belong to user.");
                }
                db.closeConnection(true);
            }
            else {
                if (isRelated(db.getUserDAO().find(username), personID)) {
                    if (db.getPersonDAO().find(personID) != null) setPersonResultAttributes(personID, db, personResult);
                    else personResult = null;
                }
                else {
                    personResult.setSuccess(false);
                    personResult.setMessage("Error: PersonID does not belong to user.");
                }
            }

            return personResult;
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, "Error generated while trying to FindPerson", e);
            db.closeConnection(false);
            return new PersonResult(false, "Error: " + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error generated while trying to FindPerson", e);
            db.closeConnection(false);
            return new PersonResult(false, "Error: " + e.getMessage());
        }
    }

    private void setPersonResultAttributes(String personID, Database db, PersonResult personResult) throws DataAccessException {
        Person personFound = db.getPersonDAO().find(personID);

        logger.log(Level.FINEST, "Setting attributes in PersonResult");
        personResult.setAssociatedUsername(personFound.getAssociatedUsername());
        personResult.setPersonID(personFound.getPersonID());
        personResult.setFirstName(personFound.getFirstName());
        personResult.setLastName(personFound.getLastName());
        personResult.setGender(personFound.getGender());
        personResult.setFatherID(personFound.getFather());
        personResult.setMotherID(personFound.getMother());
        personResult.setSpouseID(personFound.getSpouse());
        personResult.setSuccess(true);
    }

    public Database getCurrDatabase() {
        if (database == null) database = new Database();
        return database;
    }

    public void setDb(Database db) {
        this.database = db;
    }

    private boolean isRelated(User user, String personID) throws DataAccessException {
        // if the personID is from the current user, then they are related
        if (user.getPersonID().equals(personID)) return true;
        Person person = getCurrDatabase().getPersonDAO().find(user.getPersonID());

        return relatedHelper(person, personID);
    }

    private boolean relatedHelper(Person person, String personID) throws DataAccessException {
        // if the personID is from the current person, then they are related
        if (person.getPersonID().equals(personID)) return true;

        // check if personID is related on mother's side
        if (person.getMother().equals(personID)) return true;
        relatedHelper(database.getPersonDAO().find(person.getMother()), personID);

        // check if personID is related on father's side
        if (person.getFather().equals(personID)) return true;
        relatedHelper(database.getPersonDAO().find(person.getFather()), personID);

        return false;
    }
}

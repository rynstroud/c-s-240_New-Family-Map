package service;

import dao.DataAccessException;
import dao.Database;
import model.Event;
import model.Person;
import model.User;
import request_result.LoadRequest;
import request_result.LoadResult;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * holds the algorithm to clear the database and load new info
 * without interacting directly with the database
 */
public class LoadService {
    private static Logger logger;
    private Database database;
    private int personsAdded;
    private int usersAdded;
    private int eventsAdded;

    public LoadService() {
        logger = Logger.getLogger("familymapserver");
        database = null;
        personsAdded = 0;
        usersAdded = 0;
        eventsAdded = 0;
    }

    /**
     *
     * @param r the client's request to clear the database
     *          and load the person, user, and event data into the database
     * @return the LoadResult
     */
    public LoadResult load(LoadRequest r) throws DataAccessException {
        logger.log(Level.FINEST, "Loading database through load service");
        Database db = getCurrDatabase();
        try {
            clearDB();
            db.getConnection();

            logger.log(Level.FINEST, "Inserting all provided persons into database");
            for (Person person : r.getPersons()) {
                if (person.getPersonID() == null) person.setPersonID(db.getPersonDAO().generatePersonID());
                db.getPersonDAO().insert(person);
                ++personsAdded;
            }

            logger.log(Level.FINEST, "Inserting all provided users into database");
            for (User user : r.getUsers()) {
                if (user.getAssociatedUsername() == null) {
                    user.setAssociatedUsername(db.getPersonDAO().find(user.getPersonID()).getAssociatedUsername());
                }
                db.getUserDAO().insert(user);
                ++usersAdded;
            }

            logger.log(Level.FINEST, "Inserting all provided events into database");
            for (Event event : r.getEvents()) {
                db.getEventDAO().insert(event);
                ++eventsAdded;
            }

            String message = "Successfully added " + usersAdded + " users, " + personsAdded + " persons, and " + eventsAdded + " events to the database.";
            LoadResult result = new LoadResult(true, message);

            db.closeConnection(true);
            return result;
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, "Error generated while trying to Load", e);
            db.closeConnection(false);
            return new LoadResult(false, "Error: " + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error generated while trying to Load", e);
            db.closeConnection(false);
            return new LoadResult(false, "Error: " + e.getMessage());
        }
    }

    private void clearDB() throws DataAccessException {
        ClearService service = new ClearService();
        service.clear();
    }

    private Database getCurrDatabase() {
        if (database == null) database = new Database();
        return database;
    }

    private void addUsername(String personID, String username) throws DataAccessException {
        // if the personID is from the current user, then they are related
        Person person = getCurrDatabase().getPersonDAO().find(personID);
        person.setAssociatedUsername(username);
        updatePerson(person);

        Person spouse = getCurrDatabase().getPersonDAO().find(person.getSpouse());
        if (spouse != null) {
            spouse.setAssociatedUsername(username);
            updatePerson(spouse);
        }


        relatedHelper(person, personID, username);
    }

    private void relatedHelper(Person person, String personID, String username) throws DataAccessException {
        // if the personID is from the current person, then they are related
        if (person.getPersonID().equals(personID) && person.getAssociatedUsername() == null) {
            person.setAssociatedUsername(username);
            updatePerson(person);
        }

        // check if personID is related on mother's side
        if (person.getMother() != null) {
            Person mother = getCurrDatabase().getPersonDAO().find(person.getMother());
            mother.setAssociatedUsername(username);
            updatePerson(mother);

            relatedHelper(database.getPersonDAO().find(person.getMother()), person.getMother(), username);
        }

        // check if personID is related on father's side
        if (person.getFather() != null) {
            Person father = getCurrDatabase().getPersonDAO().find(person.getFather());
            father.setAssociatedUsername(username);
            updatePerson(father);

            relatedHelper(database.getPersonDAO().find(person.getFather()), person.getFather(), username);
        }
    }

    private void updatePerson(Person person) throws DataAccessException {
        getCurrDatabase().getPersonDAO().deletePerson(person);
        getCurrDatabase().getPersonDAO().insert(person);
    }
}

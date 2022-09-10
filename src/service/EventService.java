package service;

import dao.DataAccessException;
import dao.Database;
import model.Event;
import model.Person;
import model.User;
import request_result.EventResult;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * holds the algorithm to find a specific event
 * without interacting directly with the database
 */
public class EventService {

    private static Logger logger;
    private Database database;

    public EventService() {
        database = null;
        logger = Logger.getLogger("familymapserver");
    }

    /**
     *
     * @param eventID the client's request to find a specific event
     * @return the EventResult, which contains the event with the specified personID
     */
    public EventResult findEvent(String eventID, String username) throws DataAccessException {
        logger.log(Level.FINER, "Finding Event through EventService");
        Database db = getCurrDatabase();

        try {
            EventResult eventResult = new EventResult();

            //the if/else branches are here so i can test without having to auto commit changes
            if (db.connectionState() == null) {
                db.getConnection();

                //finding the event through the event DAO
                Event eventFound = db.getEventDAO().find(eventID);
                if (isRelated(db.getUserDAO().find(username), eventFound)) {
                    if (db.getEventDAO().find(eventID) != null) setAttributes(eventFound, eventResult, eventID);
                    else eventResult = null;
                }

                //the event belongs to a different user than the one who tried to access it
                else {
                    eventResult.setSuccess(false);
                    eventResult.setMessage("eventID does not belong to user.");
                }
                db.closeConnection(true);
            }

            //this branch will not execute unless i am testing
            else {
                Event eventFound = db.getEventDAO().find(eventID);
                if (isRelated(db.getUserDAO().find(username), eventFound)) {
                    setAttributes(eventFound, eventResult, eventID);
                }
                else {
                    eventResult.setSuccess(false);
                    eventResult.setMessage("Error: EventID does not belong to user.");
                }
            }

            return eventResult;
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, "Error generated while trying to FindEvent", e);
            db.closeConnection(false);
            return new EventResult(false, "Error: " + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error generated while trying to FindEvent", e);
            db.closeConnection(false);
            return new EventResult(false, "Error: " + e.getMessage());
        }
    }

    public Database getCurrDatabase() {
        if (database == null) database = new Database();
        return database;
    }

    public void setDb(Database db) {
        this.database = db;
    }

    private void setAttributes(Event eventFound, EventResult eventResult, String eventID) throws DataAccessException {
        logger.log(Level.FINEST, "Setting attributes in EventResult");
        eventResult.setAssociatedUsername(eventFound.getUsername());
        eventResult.setEventID(eventID);
        eventResult.setEventType(eventFound.getEventType());
        eventResult.setCity(eventFound.getCity());
        eventResult.setCountry(eventFound.getCountry());
        eventResult.setLatitude(eventFound.getLatitude());
        eventResult.setLongitude(eventFound.getLongitude());
        eventResult.setPersonID(eventFound.getPersonID());
        eventResult.setYear(eventFound.getYear());
        eventResult.setSuccess(true);
    }

    private boolean isRelated(User user, Event eventFound) throws DataAccessException {
        // if the personID is from the current user, then they are related
        if (eventFound.getUsername().equals(user.getAssociatedUsername())) return true;
        Person person = getCurrDatabase().getPersonDAO().find(user.getPersonID());

        return relatedHelper(person, eventFound);
    }

    private boolean relatedHelper(Person person, Event eventFound) throws DataAccessException {
        // if the personID is from the current person, then they are related
        if (eventFound.getPersonID().equals(person.getPersonID())) return true;

        // check if personID is related on mother's side
        if (eventFound.getPersonID().equals(person.getMother())) return true;
        relatedHelper(database.getPersonDAO().find(person.getMother()), eventFound);

        // check if personID is related on father's side
        if (eventFound.getPersonID().equals(person.getFather())) return true;
        relatedHelper(database.getPersonDAO().find(person.getFather()), eventFound);

        return false;
    }
}

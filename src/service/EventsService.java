package service;

import dao.DataAccessException;
import dao.Database;
import model.Event;
import request_result.EventsResult;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * holds the algorithm to return all events in the database
 * without interacting directly with the database
 */
public class EventsService {

    private static Logger logger;
    private Database database;

    public EventsService() {
        database = null;
        logger = Logger.getLogger("familymapserver");
    }

    /**
     *
     * @return the EventsResult, which contains a JSON object with a “data” attribute
     * that contains an array of Event objects
     */
    public EventsResult findAllEvents(String username) throws DataAccessException {
        logger.log(Level.FINER, "Finding all events through EventsService");
        Database db = getCurrDatabase();

        try {
            EventsResult eventsResult = new EventsResult();

            //the if/else branches are here so i can test without having to auto commit changes
            if (db.connectionState() == null) {
                logger.log(Level.FINE, "OPENING BAD CONNECTION IN FIND ALL EVENTS");
                db.openConnection();

                //returning all events as an array list for easy addition of new events
                List<Event> allEventsTemp = db.getEventDAO().returnAllEvents(username);
                Event[] allEvents = allEventsTemp.toArray(new Event[0]);
                eventsResult.addData(allEvents);
                eventsResult.setSuccess(true);
                logger.log(Level.FINE, "CLOSING CONNECTION IN FIND ALL EVENTS");
                db.closeConnection(true);
            }

            //this branch will not execute unless i am testing
            else {
                List<Event> allEventsTemp = db.getEventDAO().returnAllEvents(username);
                Event[] allEvents = allEventsTemp.toArray(new Event[0]);
                eventsResult.addData(allEvents);
                eventsResult.setSuccess(true);
            }

            return eventsResult;
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, "Error generated while trying to Find all Events", e);
            logger.log(Level.SEVERE, "CLOSING BAD CONNECTION IN FIND ALL EVENTS");
            db.closeConnection(false);
            return new EventsResult(false, "Error: " + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error generated while trying to Find all Events", e);
            logger.log(Level.SEVERE, "CLOSING BAD CONNECTION IN FIND ALL EVENTS");
            db.closeConnection(false);
            return new EventsResult(false, "Error: " + e.getMessage());
        }
    }

    public Database getCurrDatabase() {
        if (database == null) database = new Database();
        return database;
    }

    public void setDb(Database db) {
        this.database = db;
    }
}

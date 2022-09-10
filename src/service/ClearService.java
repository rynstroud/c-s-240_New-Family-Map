package service;

import dao.DataAccessException;
import dao.Database;
import request_result.ClearResult;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * holds the algorithm to clear the database
 * without interacting directly with the database
 */
public class ClearService {

    Database database;
    private static Logger logger;

    public ClearService() {
        database = null;
        logger = Logger.getLogger("familymapserver");
    }


    /**
     *
     * @return if the clear worked
     */
    public ClearResult clear() throws DataAccessException {
        logger.log(Level.FINE, "Clearing database through ClearService");
        Database db = getCurrDatabase();

        try {
            //the if/else branches are here so i can test without having to auto commit changes
            if (db.connectionState() == null) {
                logger.log(Level.FINER, "Current database is null, so opening a new connection.");
                db.openConnection();

                //clearing the database through the database object
                db.clearTables();

                db.closeConnection(true);
            }
            else {
                //clearing the database through the database object
                db.clearTables();
            }
            return new ClearResult(true, "Clear succeeded.");
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, "Error generated while trying to Clear all tables", e);
            db.closeConnection(false);
            return new ClearResult(false, "Error: " + e.getMessage());
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

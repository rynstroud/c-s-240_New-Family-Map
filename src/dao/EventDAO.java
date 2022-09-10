package dao;

import model.Event;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * the class interacting between the Event class and the database
 */
public class EventDAO {
    private final Connection conn;
    private static Logger logger;

    public EventDAO(Connection conn)
    {
        logger = Logger.getLogger("familymapserver");
        this.conn = conn;
    }

    /**
     *
     * @param event The event the client wants to insert into the database
     * @throws DataAccessException
     */
    public void insert(Event event) throws DataAccessException {
        logger.log(Level.FINEST, "Inserting event through dao");
        //We can structure our string to be similar to a sql command, but if we insert question
        //marks we can change them later with help from the statement
        String sql = "INSERT INTO Events (EventID, AssociatedUsername, PersonID, Latitude, Longitude, " +
                "Country, City, EventType, Year) VALUES(?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            //Using the statements built-in set(type) functions we can pick the question mark we want
            //to fill in and give it a proper value. The first argument corresponds to the first
            //question mark found in our sql String
            stmt.setString(1, event.getEventID());
            stmt.setString(2, event.getUsername());
            stmt.setString(3, event.getPersonID());
            stmt.setFloat(4, event.getLatitude());
            stmt.setFloat(5, event.getLongitude());
            stmt.setString(6, event.getCountry());
            stmt.setString(7, event.getCity());
            stmt.setString(8, event.getEventType());
            stmt.setInt(9, event.getYear());

            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new DataAccessException("Error encountered while inserting into the database");
        }
    }

    /**
     *
     * @param eventID the event the client wants to find
     * @return
     * @throws DataAccessException
     */
    public Event find(String eventID) throws DataAccessException {
        logger.log(Level.FINEST, "Finding event through dao");
        Event event;
        ResultSet rs = null;
        String sql = "SELECT * FROM Events WHERE EventID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, eventID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                event = new Event(rs.getString("EventID"), rs.getString("AssociatedUsername"),
                        rs.getString("PersonID"), rs.getFloat("Latitude"), rs.getFloat("Longitude"),
                        rs.getString("Country"), rs.getString("City"), rs.getString("EventType"),
                        rs.getInt("Year"));
                return event;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to find event", e);
            throw new DataAccessException("Error encountered while finding event");
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     *
     * @param personID the user the client wants the birthdate of. This returns the year a client was born
     * @return
     * @throws DataAccessException
     */
    public Event findPersonBirth(String personID) throws DataAccessException {
        logger.log(Level.FINER, "Finding birth year of user through dao");
        Event event;
        ResultSet rs = null;
        String sql = "SELECT * FROM Events WHERE PersonID = ? AND EventType = 'Birth';";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, personID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                event = new Event(rs.getString("EventID"), rs.getString("AssociatedUsername"),
                        rs.getString("PersonID"), rs.getFloat("Latitude"), rs.getFloat("Longitude"),
                        rs.getString("Country"), rs.getString("City"), rs.getString("EventType"),
                        rs.getInt("Year"));
                return event;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to find event", e);
            throw new DataAccessException("Error encountered while finding event");
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     * generates a unique event id for a new event
     * @return the generated event id
     * @throws DataAccessException
     */
    public String generateEventID() throws DataAccessException {
        String temp = UUID.randomUUID().toString();
        while (find(temp) != null) {
            temp = UUID.randomUUID().toString();
        }
        return temp;
    }

    /**
     * clears the event table in the given database
     * @param db the provided database
     * @throws DataAccessException
     */
    public void clear(Database db) throws DataAccessException {
        db.clear("EVENTS");
    }

    /**
     * clears all events relating to a given user
     * @param username the associatedUsername of the user whose events are being cleared
     * @throws DataAccessException
     */
    public void clearEventsRelatingToUser(String username) throws DataAccessException {
        logger.log(Level.FINEST, "Deleting events related to user through dao");
        String sql = "DELETE FROM Events WHERE AssociatedUsername = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to delete events relating to user", e);
            throw new DataAccessException("Error encountered while clearing events relating to user");

        }
    }

    /**
     *
     * @return a list of all the events in the current database
     * @throws SQLException
     */
    public List<Event> returnAllEvents(String id) throws SQLException {
        String query = "SELECT * FROM Events WHERE AssociatedUsername = ?";
        List<Event> list = new ArrayList<Event>();
        Event event = null;
        ResultSet rs = null;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, id);
            rs = stmt.executeQuery();
            while (rs.next()) {
                event = new Event(rs.getString("EventID"), rs.getString("AssociatedUsername"),
                        rs.getString("PersonID"), rs.getFloat("Latitude"), rs.getFloat("Longitude"),
                        rs.getString("Country"), rs.getString("City"), rs.getString("EventType"),
                        rs.getInt("Year"));

                list.add(event);
            }
        } finally {
            rs.close();
        }
        return list;
    }

}

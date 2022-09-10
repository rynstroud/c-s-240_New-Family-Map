package dao;

import model.Event;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {

    private Database db;
    final String CONNECTION_URL = "jdbc:sqlite:familymap.sqlite";
    private Connection conn;

    @BeforeEach
    void setup() throws DataAccessException {
        db = new Database();
        conn = db.openConnection();
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        if (db.connectionState() != null) db.closeConnection(false);
        db = null;
        conn = null;
    }

    @Test
    void openConnectionPass() {
        assertNotNull(conn);
    }

    @Test
    void getConnectionPass() throws DataAccessException {
        conn = db.getConnection();
        assertNotNull(conn);
        assertNotNull(db.connectionState());
        assertEquals(db.connectionState(), conn);
    }

    @Test
    void closeConnection() throws DataAccessException {
        db.closeConnection(false);
        conn = db.connectionState();
        assertNull(conn);
    }

    @Test
    void clearTablesPass() throws DataAccessException {
        Event event = new Event("Biking_123A", "Gale", "Gale123A",
                35.9f, 140.1f, "Japan", "Ushiku",
                "Biking_Around", 2016);
        EventDAO eventDAO = new EventDAO(conn);
        eventDAO.insert(event);
        Event event_test = eventDAO.find(event.getEventID());
        assertNotNull(event_test);
        db.clearTables();
        event_test = eventDAO.find(event.getEventID());
        assertNull(event_test);
    }
}
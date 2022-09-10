package dao;

import model.Event;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

//We will use this to test that our insert method is working and failing in the right ways
public class EventDAOTest {
    private Database db;
    private Event bestEvent;
    private Event testEvent;
    private EventDAO eDao;

    @BeforeEach
    public void setUp() throws DataAccessException
    {
        db = new Database();
        bestEvent = new Event("Biking_123A", "Gale", "Gale123A",
                35.9f, 140.1f, "Japan", "Ushiku",
                "Biking_Around", 2016);

        testEvent = new Event("test_eventID", "test_username", "test_personID",
                0, 0, "test_country", "test_city",
                "test_type", 0);
        Connection conn = db.getConnection();
        db.clearTables();
        eDao = new EventDAO(conn);
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        if (db.connectionState() != null) db.closeConnection(false);
        db = null;
    }

    @Test
    public void insertOneEventPass() throws DataAccessException {
        eDao.insert(bestEvent);
        Event compareTest = eDao.find(bestEvent.getEventID());
        assertNotNull(compareTest);
        assertEquals(bestEvent, compareTest);
    }

    @Test
    public void insertTwoDiffEventsPass() throws DataAccessException {
        eDao.insert(bestEvent);
        eDao.insert(testEvent);
        Event compareTest = eDao.find(bestEvent.getEventID());
        assertNotNull(compareTest);
        assertEquals(bestEvent, compareTest);
    }

    @Test
    public void insertDuplicateEventsFail() throws DataAccessException {
        eDao.insert(bestEvent);
        assertThrows(DataAccessException.class, ()-> eDao.insert(bestEvent));
    }

    @Test
    public void findOneEventPass() throws DataAccessException {
        eDao.insert(bestEvent);
        Event compareTest = eDao.find(bestEvent.getEventID());
        assertNotNull(compareTest);
        assertEquals(bestEvent, compareTest);
    }

    @Test
    public void findTwoDiffEventsPass() throws DataAccessException {
        eDao.insert(bestEvent);
        eDao.insert(testEvent);
        Event compareTest1 = eDao.find(bestEvent.getEventID());
        assertNotNull(compareTest1);
        assertEquals(bestEvent, compareTest1);
        Event compareTest2 = eDao.find(testEvent.getEventID());
        assertNotNull(compareTest2);
        assertEquals(testEvent, compareTest2);
    }

    @Test
    public void findFakeEventFail() throws DataAccessException {
        assertNull(eDao.find("fake_event"));
    }

    @Test
    public void findInClosedDatabaseFail() throws DataAccessException {
        db.closeConnection(false);
        assertThrows(DataAccessException.class, ()-> eDao.find(bestEvent.getEventID()));
    }

    @Test
    public void clearEventTablePass() throws DataAccessException {
        eDao.insert(bestEvent);
        Event compareTest1 = eDao.find(bestEvent.getEventID());
        assertNotNull(compareTest1);
        assertEquals(bestEvent, compareTest1);

        db.clearTables();
        Event compareTest2 = eDao.find(bestEvent.getEventID());
        assertNull(compareTest2);
    }

    @Test
    public void clearEventTableClosedDatabaseFail() throws DataAccessException {
        eDao.insert(bestEvent);
        Event compareTest1 = eDao.find(bestEvent.getEventID());
        assertNotNull(compareTest1);
        assertEquals(bestEvent, compareTest1);
        db.closeConnection(false);
        assertThrows(NullPointerException.class, ()-> eDao.clear(db));
    }

    @Test
    @DisplayName("Clear events based on associatedUsername pass")
    public void clearEventsBasedOnUserPass() throws DataAccessException {
        eDao.insert(bestEvent);
        eDao.insert(testEvent);
        String username = bestEvent.getUsername();
        eDao.clearEventsRelatingToUser(username);
        assertNull(eDao.find(bestEvent.getEventID()));
        assertNotNull(eDao.find(testEvent.getEventID()));
    }

    @Test
    @DisplayName("Clear events based on associatedUsername fail")
    public void clearEventsBasedOnUserFail() throws DataAccessException {
        eDao.insert(testEvent);
        String username = bestEvent.getUsername();
        eDao.clearEventsRelatingToUser(username);
        assertNotNull(eDao.find(testEvent.getEventID()));
    }

    @Test
    @DisplayName("Generate unique eventID default pass")
    public void generateEventIDPass() throws DataAccessException {
        assertNull(eDao.find(eDao.generateEventID()));
    }

    @Test
    @DisplayName("Generate two different eventID pass")
    public void generateTwoDifferentEventIDPass() throws DataAccessException {
        String id1 = eDao.generateEventID();
        String id2 = eDao.generateEventID();
        assertNotEquals(id1, id2);
    }

    @Test
    @DisplayName("Return all events pass")
    public void returnAllEventsBasedOnUserPass() throws DataAccessException, SQLException {
        eDao.insert(bestEvent);
        testEvent.setUsername(bestEvent.getUsername());
        eDao.insert(testEvent);
        assert(eDao.returnAllEvents(bestEvent.getUsername()).contains(bestEvent));
        assert(eDao.returnAllEvents(bestEvent.getUsername()).contains(testEvent));
    }

    @Test
    @DisplayName("Return all events empty database fail")
    public void returnAllPeopleFail() throws DataAccessException, SQLException {
        eDao.clear(db);
        assertFalse(eDao.returnAllEvents(bestEvent.getUsername()).contains(bestEvent));
        assertFalse(eDao.returnAllEvents(bestEvent.getUsername()).contains(testEvent));
    }
}

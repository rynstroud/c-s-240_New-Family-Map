package service;

import dao.DataAccessException;
import dao.Database;
import dao.EventDAO;
import model.Event;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class EventsServiceTest {

    private Event event;
    private Event event2;
    private EventDAO dao;
    private Database db;
    private EventsService service;

    @BeforeEach
    void setUp() throws DataAccessException {
        service = new EventsService();
        db = service.getCurrDatabase();
        Connection conn = db.getConnection();
        event = new Event("test_eventID", "test_username", "test_personID",
                0, 0, "test_country", "test_city",
                "test_type", 0);
        event2 = new Event("test2_eventID", "test2_username", "test2_personID",
                0, 0, "test2_country", "test2_city",
                "test2_type", 0);
        db.clearTables();
        dao = new EventDAO(conn);
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        if (db.connectionState() != null) db.closeConnection(false);
        db = null;
    }

    @Test
    @DisplayName("Return all Event pass")
    public void returnAllEventPass() throws DataAccessException {
        dao.insert(event);
        event2.setUsername(event.getUsername());
        dao.insert(event2);
        assert(Arrays.asList(service.findAllEvents(event.getUsername()).getData()).contains(event));
        assert(Arrays.asList(service.findAllEvents(event.getUsername()).getData()).contains(event2));
    }

    @Test
    @DisplayName("Return all Event empty database fail")
    public void returnAllEventFail() throws DataAccessException {
        dao.clear(db);
        assertFalse(Arrays.asList(service.findAllEvents(event.getUsername()).getData()).contains(event));
        assertFalse(Arrays.asList(service.findAllEvents(event.getUsername()).getData()).contains(event2));
    }

}
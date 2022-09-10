package service;

import dao.DataAccessException;
import dao.Database;
import dao.EventDAO;
import model.Event;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import request_result.RegisterRequest;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class EventServiceTest {
    private Event event;
    private Event event2;
    //    private User user;
    private EventDAO dao;
    private Database db;
    private EventService service;

    @BeforeEach
    void setUp() throws DataAccessException {
        service = new EventService();
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
    @DisplayName("Return Event pass")
    public void returnEventPass() throws DataAccessException {
        db.closeConnection(true);
        RegisterRequest request = new RegisterRequest("test_username", "test_password", "test_email", "test_firstname", "test_lastname", 'g');
        RegisterService registerService = new RegisterService(request);
        registerService.register();
        db.getConnection();
        db.getEventDAO().insert(event);
        service.setDb(db);
        assertNotNull(service.findEvent("test_eventID", "test_username"));
        //assert(service.findEvent(event.getEventID(), "test_username")).getAssociatedUsername().equals("test_username");
    }

    @Test
    @DisplayName("Return Event empty database fail")
    public void returnEventFail() throws DataAccessException {
        dao.clear(db);
        assertFalse(service.findEvent(event.getEventID(), event.getUsername()).isSuccess());
    }
}
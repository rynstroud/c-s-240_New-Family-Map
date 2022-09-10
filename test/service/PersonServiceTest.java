package service;

import dao.DataAccessException;
import dao.Database;
import dao.PersonDAO;
import model.Person;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import request_result.RegisterRequest;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class PersonServiceTest {
    private Person person;
    private Person person2;
//    private User user;
    private PersonDAO dao;
    private Database db;
    private PersonService service;

    @BeforeEach
    void setUp() throws DataAccessException {
        service = new PersonService();
        db = service.getCurrDatabase();
        Connection conn = db.getConnection();
        person = new Person("test_ID","test_username", "test_firstname", "test_lastname",
                'o',  "test_father", "test_mother", "test_spouse");
        person2 = new Person("2_ID","2_username", "2_firstname", "2_lastname",
                'o',  "2_father", "2_mother", "2_spouse");
        db.clearTables();
        dao = new PersonDAO(conn);
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        if (db.connectionState() != null) db.closeConnection(false);
        db = null;
    }

    @Test
    @DisplayName("Return person pass")
    public void returnPersonPass() throws DataAccessException {
        db.closeConnection(true);
        RegisterRequest request = new RegisterRequest("test_username", "test_password", "test_email", "test_firstname", "test_lastname", 'g');
        RegisterService registerService = new RegisterService(request);
        String personID = registerService.register().getPersonID();
        db.getConnection();
        assertNotNull(service.findPerson(personID, "test_username"));
        assert(service.findPerson(personID, "test_username")).getAssociatedUsername().equals("test_username");
    }

    @Test
    @DisplayName("Return person empty database fail")
    public void returnPersonFail() throws DataAccessException {
        dao.clear(db);
        assertFalse(service.findPerson(person.getPersonID(), person.getAssociatedUsername()).isSuccess());
    }
}

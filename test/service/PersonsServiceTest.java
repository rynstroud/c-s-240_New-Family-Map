package service;

import dao.DataAccessException;
import dao.Database;
import dao.PersonDAO;
import model.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PersonsServiceTest {

    private Person person;
    private Person person2;
    private PersonDAO dao;
    private Database db;
    private PersonsService service;

    @BeforeEach
    void setUp() throws DataAccessException {
        service = new PersonsService();
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
    @DisplayName("Return all people pass")
    public void returnAllPeoplePass() throws DataAccessException {
        dao.insert(person);
        person2.setAssociatedUsername(person.getAssociatedUsername());
        dao.insert(person2);
        assert(Arrays.asList(service.findAllPersons(person.getAssociatedUsername()).getData()).contains(person));
        assert(Arrays.asList(service.findAllPersons(person.getAssociatedUsername()).getData()).contains(person2));
    }

    @Test
    @DisplayName("Return all people empty database fail")
    public void returnAllPeopleFail() throws DataAccessException {
        dao.clear(db);
        assertFalse(Arrays.asList(service.findAllPersons(person.getAssociatedUsername()).getData()).contains(person));
        assertFalse(Arrays.asList(service.findAllPersons(person.getAssociatedUsername()).getData()).contains(person2));
    }
}
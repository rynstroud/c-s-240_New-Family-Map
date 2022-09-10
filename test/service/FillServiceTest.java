package service;

import dao.DataAccessException;
import dao.Database;
import dao.UserDAO;
import model.Person;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FillServiceTest {

    private User user;
    private Person person;
    private Database db;
    private UserDAO dao;
    private FillService service;

    @BeforeEach
    void setUp() throws DataAccessException {
        db = new Database();
        db.getConnection();
        user = new User("test_username", "test_password","test_email",
                "test_firstname", "test_lastname", 'o', "test_ID");
        person = new Person("test_ID","test_username", "test_firstname", "test_lastname",
                'o',  "test_father", "test_mother", "test_spouse");
        db.clearTables();
        dao = db.getUserDAO();
        service = new FillService();
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        if (db.connectionState() != null) db.closeConnection(false);
    }

    @Test
    void fillPass() throws DataAccessException {
        dao.insert(user);
        db.getPersonDAO().insert(person);
        db.closeConnection(true);
        service.fill(user.getAssociatedUsername(), 3);
        db.getConnection();
        assertNotNull(db.getPersonDAO().find(user.getPersonID()).getMother());
        assertNotNull(db.getPersonDAO().find(user.getPersonID()).getFather());
    }

    @Test
    void fillFakeUserFail() throws DataAccessException {
        db.closeConnection(true);
        boolean success = service.fill(user.getAssociatedUsername(), 3).isSuccess();
        assertFalse(success);
    }
}
package dao;

import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {

    private User user;
    private User user2;
    private UserDAO dao;
    private Database db;

    @BeforeEach
    void setUp() throws DataAccessException {
        db = new Database();
        Connection conn = db.getConnection();
        user = new User("test_username", "test_password","test_email",
                "test_firstname", "test_lastname", 'o', "test_ID");
        user2 = new User("diff_username", "diff_password","diff_email",
                "diff_firstname", "diff_lastname", 'o', "diff_ID");
        db.clearTables();
        dao = new UserDAO(conn);
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        if (db.connectionState() != null) db.closeConnection(false);
        db = null;
    }

    @Test
    public void findOneUserPass() throws DataAccessException {
        dao.insert(user);
        User compareTest = dao.find(user.getAssociatedUsername());
        assertNotNull(compareTest);
        assertEquals(user, compareTest);
    }

    @Test
    public void findTwoDiffUsersPass() throws DataAccessException {
        dao.insert(user);
        dao.insert(user2);
        User compareTest = dao.find(user.getAssociatedUsername());
        assertNotNull(compareTest);
        assertEquals(user, compareTest);
    }

    @Test
    public void findFakeUserFail() throws DataAccessException {
        assertNull(dao.find("fake_user"));
    }

    @Test
    public void findInClosedDatabaseFail() throws DataAccessException {
        db.closeConnection(false);
        assertThrows(DataAccessException.class, ()-> dao.find(user.getPersonID()));
    }

    @Test
    public void findNullUserFail() throws DataAccessException {
        assertNull(dao.find(null));
    }

    @Test
    public void insertOneUserPass() throws DataAccessException {
        dao.insert(user);
        User compareTest = dao.find(user.getAssociatedUsername());
        assertNotNull(compareTest);
        assertEquals(user, compareTest);
    }

    @Test
    public void insertTwoDiffUsersPass() throws DataAccessException {
        dao.insert(user);
        dao.insert(user2);
        User compareTest = dao.find(user.getAssociatedUsername());
        assertNotNull(compareTest);
        assertEquals(user, compareTest);
    }

    @Test
    public void insertDuplicateUsersFail() throws DataAccessException {
        //the second person already exists in the database, so it should throw a data access exception
        dao.insert(user);
        assertThrows(DataAccessException.class, ()-> dao.insert(user));
    }

    @Test
    public void insertDuplicateNonconsecutiveUsersFail() throws DataAccessException {
        //the second person already exists in the database, so it should throw a data access exception
        dao.insert(user);
        dao.insert(user2);
        assertThrows(DataAccessException.class, ()-> dao.insert(user));
    }

    @Test
    public void insertNullUserFail() {
        assertThrows(NullPointerException.class, ()-> dao.insert(null));
    }

    @Test
    public void insertIntoClosedDatabaseFail() throws DataAccessException {
        db.closeConnection(false);
        assertThrows(DataAccessException.class, ()-> dao.insert(user));
    }

    @Test
    public void clearUserTablePass() throws DataAccessException {
        dao.insert(user);
        User compareTest1 = dao.find(user.getAssociatedUsername());
        assertNotNull(compareTest1);
        assertEquals(user, compareTest1);

        dao.clear(db);
        User compareTest2 = dao.find(user.getAssociatedUsername());
        assertNull(compareTest2);
    }

    @Test
    public void clearUserTableClosedDatabaseFail() throws DataAccessException {
        dao.insert(user);
        User compareTest1 = dao.find(user.getAssociatedUsername());
        assertNotNull(compareTest1);
        assertEquals(user, compareTest1);
        db.closeConnection(false);
        assertThrows(NullPointerException.class, ()-> dao.clear(db));
    }
}
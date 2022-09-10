package dao;

import model.AuthToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class AuthTokenDAOTest {

    private AuthToken auth_token;
    private AuthToken auth_token2;
    private AuthTokenDAO dao;
    private Database db;

    @BeforeEach
    void setUp() throws DataAccessException {
        db = new Database();
        Connection conn = db.getConnection();
        auth_token = new AuthToken("test_token", "test_username", "test_timestamp");
        auth_token2 = new AuthToken("diff_token", "diff_username", "diff_timestamp");
        db.clearTables();
        dao = new AuthTokenDAO(conn);
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        if (db.connectionState() != null) db.closeConnection(false);
        db = null;
    }

    @Test
    public void findOneAuthTokenPass() throws DataAccessException {
        dao.insert(auth_token);
        AuthToken compareTest = dao.find(auth_token.getToken());
        assertNotNull(compareTest);
        assertEquals(auth_token, compareTest);
    }

    @Test
    public void findTwoDiffAuthTokensPass() throws DataAccessException {
        dao.insert(auth_token);
        dao.insert(auth_token2);
        AuthToken compareTest = dao.find(auth_token.getToken());
        assertNotNull(compareTest);
        assertEquals(auth_token, compareTest);
        AuthToken compareTest2 = dao.find(auth_token2.getToken());
        assertNotNull(compareTest2);
        assertEquals(auth_token2, compareTest2);
    }

    @Test
    public void findFakeAuthTokenFail() throws DataAccessException {
        assertNull(dao.find("fake_token"));
    }

    @Test
    public void findInClosedDatabaseFail() throws DataAccessException {
        db.closeConnection(false);
        assertThrows(DataAccessException.class, ()-> dao.find(auth_token.getToken()));
    }

    @Test
    public void findNullAuthTokenFail() throws DataAccessException {
        assertNull(dao.find(null));
    }

    @Test
    public void insertOneAuthTokenPass() throws DataAccessException {
        dao.insert(auth_token);
        AuthToken compareTest = dao.find(auth_token.getToken());
        assertNotNull(compareTest);
        assertEquals(auth_token, compareTest);
    }

    @Test
    public void insertTwoDiffAuthTokensPass() throws DataAccessException {
        dao.insert(auth_token);
        dao.insert(auth_token2);
        AuthToken compareTest = dao.find(auth_token.getToken());
        assertNotNull(compareTest);
        assertEquals(auth_token, compareTest);
        AuthToken compareTest2 = dao.find(auth_token2.getToken());
        assertNotNull(compareTest2);
        assertEquals(auth_token2, compareTest2);
    }

    @Test
    public void insertDuplicateAuthTokensFail() throws DataAccessException {
        dao.insert(auth_token);
        assertThrows(DataAccessException.class, ()-> dao.insert(auth_token));
    }

    @Test
    public void insertDuplicateNonconsecutiveAuthTokensFail() throws DataAccessException {
        dao.insert(auth_token);
        dao.insert(auth_token2);
        assertThrows(DataAccessException.class, ()-> dao.insert(auth_token));
    }

    @Test
    public void insertNullAuthTokenFail() {
        assertThrows(NullPointerException.class, ()-> dao.insert(null));
    }

    @Test
    public void insertIntoClosedDatabaseFail() throws DataAccessException {
        db.closeConnection(false);
        assertThrows(DataAccessException.class, ()-> dao.insert(auth_token));
    }

    @Test
    public void clearAuthTokenTablePass() throws DataAccessException {
        dao.insert(auth_token);
        AuthToken compareTest1 = dao.find(auth_token.getToken());
        assertNotNull(compareTest1);
        assertEquals(auth_token, compareTest1);

        dao.clear(db);
        AuthToken compareTest2 = dao.find(auth_token.getToken());
        assertNull(compareTest2);
    }

    @Test
    public void clearAuthTokenTableClosedDatabaseFail() throws DataAccessException {
        dao.insert(auth_token);
        AuthToken compareTest1 = dao.find(auth_token.getToken());
        assertNotNull(compareTest1);
        assertEquals(auth_token, compareTest1);
        db.closeConnection(false);
        assertThrows(NullPointerException.class, ()-> dao.clear(db));
    }

    @Test
    @DisplayName("Generate unique authtoken default pass")
    public void generateAuthTokenPass() throws DataAccessException {
        assertNull(dao.find(dao.generateAuthToken()));
    }

    @Test
    @DisplayName("Generate two different authtoken pass")
    public void generateTwoDifferentAuthTokenPass() throws DataAccessException {
        String id1 = dao.generateAuthToken();
        String id2 = dao.generateAuthToken();
        assertNotEquals(id1, id2);
    }
}
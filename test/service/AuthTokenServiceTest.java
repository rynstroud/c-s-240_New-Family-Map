package service;

import dao.AuthTokenDAO;
import dao.DataAccessException;
import dao.Database;
import model.AuthToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthTokenServiceTest {

    AuthTokenService service;
    AuthToken auth_token;
    AuthTokenDAO dao;
    Database db;

    @BeforeEach
    void setUp() throws DataAccessException {
        auth_token = new AuthToken("test_token", "test_username", "test_timestamp");
        service = new AuthTokenService();
        db = null;
        dao = null;
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        //db.closeConnection(false);
    }

    @Test
    @DisplayName("Default find Auth Token Pass")
    void findAuthTokenPass() throws DataAccessException {
        assertNull(service.findAuthToken("test_token"));
        db = new Database();
        db.openConnection();
        dao = db.getAuthTokenDAO();
        service.setDb(db);
        dao.insert(auth_token);
        assertNotNull(service.findAuthToken("test_token"));
    }

    @Test
    @DisplayName("Find null auth token fail")
    void findAuthTokenFail() throws DataAccessException {
        assertNull(service.findAuthToken("bad_token"));
    }

}
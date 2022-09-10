package service;

import dao.AuthTokenDAO;
import dao.DataAccessException;
import dao.Database;
import model.AuthToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTest {

    ClearService service;
    AuthToken auth_token;
    AuthTokenDAO dao;
    Database db;

    @BeforeEach
    void setUp() throws DataAccessException {
        auth_token = new AuthToken("test_token", "test_username", "test_timestamp");
        service = new ClearService();
        db = service.getCurrDatabase();
        db.openConnection();
        dao = db.getAuthTokenDAO();
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        db.closeConnection(false);
    }

    @Test
    void clearPass() throws DataAccessException {
        dao.insert(auth_token);
        service.clear();
        assertNull(dao.find(auth_token.getToken()));
    }

    @Test
    void clearEmptyPass() throws DataAccessException {
        service.clear();
        assertNull(dao.find(auth_token.getToken()));
    }
}
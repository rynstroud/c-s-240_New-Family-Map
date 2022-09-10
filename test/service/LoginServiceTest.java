package service;

import dao.DataAccessException;
import dao.Database;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request_result.LoginRequest;
import request_result.LoginResult;
import request_result.RegisterRequest;

import static org.junit.jupiter.api.Assertions.*;

class LoginServiceTest {

    private Database db;

    @BeforeEach
    void setUp() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("test_username", "test_password", "test_email", "test_firstname", "test_lastname", 'g');
        RegisterService registerService = new RegisterService(request);
        registerService.register().getPersonID();
        db = new Database();
        db.getConnection();
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        if (db.connectionState() != null) db.closeConnection(false);
    }

    @Test
    void loginPass() {
        LoginRequest request = new LoginRequest("test_username", "test_password");
        LoginService service = new LoginService(request);
        LoginResult result = service.login();
        assertEquals("test_username", result.getUsername());
        assertNotNull(result.getAuthtoken());
    }

    @Test
    void loginFail() {
        LoginRequest request = new LoginRequest("test_username", "invalid_password");
        LoginService service = new LoginService(request);
        LoginResult result = service.login();
        assertNull(result.getUsername());
        assertNull(result.getAuthtoken());
        assert(result.getMessage().contains("Error"));
    }
}
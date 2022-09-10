package service;

import dao.DataAccessException;
import dao.Database;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request_result.RegisterRequest;

import javax.xml.crypto.Data;

import static org.junit.jupiter.api.Assertions.*;

class RegisterServiceTest {

    private Database db;

    @BeforeEach
    void setUp() throws DataAccessException {
        db = new Database();
        db.getConnection();
        db.clearTables();
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        if (db.connectionState() != null) db.closeConnection(false);
    }

    @Test
    void registerPass() throws DataAccessException {
        db.closeConnection(true);
        RegisterRequest request = new RegisterRequest("test_username", "test_password", "test_email", "test_firstname", "test_lastname", 'g');
        RegisterService registerService = new RegisterService(request);
        String personID = registerService.register().getPersonID();
        db.getConnection();
        assertNotNull(db.getPersonDAO().find(personID));
        assert(db.getPersonDAO().find(personID)).getAssociatedUsername().equals("test_username");
    }

    @Test
    void reregisterFail() throws DataAccessException {
        db.closeConnection(true);
        RegisterRequest request = new RegisterRequest("test_username", "test_password", "test_email", "test_firstname", "test_lastname", 'g');
        RegisterService registerService = new RegisterService(request);
        RegisterRequest request2 = new RegisterRequest("test_username", "test_password", "_email", "_firstname", "_lastname", 'g');
        registerService.setR(request2);
        assert(registerService.register().isSuccess());
    }
}
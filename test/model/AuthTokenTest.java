package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthTokenTest {

    AuthToken auth_token;
    AuthToken auth_token2;

    @BeforeEach
    void setup() {
        auth_token = new AuthToken("test_token", "test_username", "test_timestamp");
        auth_token2 = new AuthToken("diff_token", "diff_username", "diff_timestamp");
    }

    @Test
    public void equalsDiffNamesPass() {
        AuthToken auth_token3 = new AuthToken("test_token", "test_username", "test_timestamp");
        assertTrue(auth_token.equals(auth_token3));
        assertEquals(auth_token, auth_token3);
    }

    @Test
    public void equalsSameNamePass() {
        assertTrue(auth_token.equals(auth_token));
        assertEquals(auth_token, auth_token);
    }

    @Test
    public void equalsDiffObjFail() {
        assertFalse(auth_token.equals(auth_token2));
        assertNotEquals(auth_token, auth_token2);
    }

    @Test
    public void equalsSimilarObjFail() {
        AuthToken auth_token3 = new AuthToken("test_token", "test3_username", "test_timestamp");
        assertFalse(auth_token3.equals(auth_token));
        assertNotEquals(auth_token3, auth_token);
    }

}
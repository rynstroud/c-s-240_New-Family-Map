package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;
    private User user2;

    @BeforeEach
    void setup() {
        user = new User("test_username", "test_password","test_email",
                "test_firstname", "test_lastname", 'o', "test_ID");
        user2 = new User("diff_username", "diff_password","diff_email",
                "diff_firstname", "diff_lastname", 'o', "diff_ID");
    }

    @Test
    public void equalsDiffNamesPass() {
        User user3 = new User("test_username", "test_password","test_email",
                "test_firstname", "test_lastname", 'o', "test_ID");

        // Both of these test cases to assure that the equals method returns correctly
        assertTrue(user.equals(user3));
        assertEquals(user, user3);
    }

    @Test
    public void equalsSameNamePass() {
        assertTrue(user.equals(user));
        assertEquals(user, user);
    }

    @Test
    public void equalsDiffObjFail() {
        assertFalse(user.equals(user2));
        assertNotEquals(user, user2);
    }

    @Test
    public void equalsSimilarObjFail() {
        User user3 = new User("test_username", "test_password","test_email",
                "test_firstname", "test_lastname", 'o', "test3_ID");

        assertFalse(user.equals(user3));
        assertNotEquals(user, user3);
    }

}
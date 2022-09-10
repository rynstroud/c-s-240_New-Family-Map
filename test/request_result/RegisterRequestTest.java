package request_result;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestTest {

    private RegisterRequest request;

    @BeforeEach
    void setUp() {
        request = new RegisterRequest("ashyke", "rynee",
                "jace_hamilton@fake_email.net", "Jace", "Hamilton", 'm');
    }

    @Test
    public void equalsDiffNamesPass() {
        RegisterRequest request2 = new RegisterRequest("ashyke", "rynee",
                "jace_hamilton@fake_email.net", "Jace", "Hamilton", 'm');
        assertEquals(request2,request);
    }

    @Test
    public void equalsSameNamePass() {
        assertEquals(request, request);
    }

    @Test
    public void equalsDiffObjFail() {
        RegisterRequest request2 = new RegisterRequest("newfortnitemap", "rynee",
                "ryn@fake_email.net", "Ryn", "Stroud", 'f');
        assertNotEquals(request, request2);
    }

    @Test
    public void equalsSimilarObjFail() {
        RegisterRequest request2 = new RegisterRequest("ashyke2", "rynee",
                "jace_hamilton@fake_email.net", "Jace", "Hamilton", 'm');
        assertNotEquals(request2,request);
    }

}
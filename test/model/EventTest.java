package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EventTest {

    private Event event;

    @BeforeEach
    void setup() {
        event = new Event("test_eventID", "test_username", "test_personID",
                0, 0, "test_country", "test_city", "test_eventType", 0);
    }

    @Test
    void testEquals() {
    }
}
package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PersonTest {

    private Person person;
    private Person person2;

    @BeforeEach
    void setup() {
        person = new Person("test_ID","test_username", "test_firstname", "test_lastname",
                'o',  "test_father", "test_mother", "test_spouse");
        person2 = new Person("2_ID","2_username", "2_firstname", "2_lastname",
                'o',  "2_father", "2_mother", "2_spouse");
    }

    @Test
    public void equalsDiffNamesPass() {
        Person person3 = new Person("test_ID","test_username", "test_firstname", "test_lastname",
                'o',  "test_father", "test_mother", "test_spouse");
        assertTrue(person.equals(person3));
        assertEquals(person, person3);
    }

    @Test
    public void equalsSameNamePass() {
        assertTrue(person.equals(person));
        assertEquals(person, person);
    }

    @Test
    public void equalsDiffObjFail() {
        assertFalse(person.equals(person2));
        assertNotEquals(person, person2);
    }

    @Test
    public void equalsSimilarObjFail() {
        Person person3 = new Person("test_ID","test_username", "test_firstname", "test_lastname",
                'o',  "test_father", "test3_mother", "test_spouse");
        assertFalse(person.equals(person3));
        assertNotEquals(person, person3);
    }

}
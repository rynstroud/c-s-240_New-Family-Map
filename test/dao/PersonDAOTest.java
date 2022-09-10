package dao;

import model.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class PersonDAOTest {

    private Person person;
    private Person person2;
    private PersonDAO dao;
    private Database db;

    @BeforeEach
    void setup() throws DataAccessException {
        db = new Database();
        Connection conn = db.getConnection();
        person = new Person("test_ID","test_username", "test_firstname", "test_lastname",
                'o',  "test_father", "test_mother", "test_spouse");
        person2 = new Person("2_ID","2_username", "2_firstname", "2_lastname",
                'o',  "2_father", "2_mother", "2_spouse");
        db.clearTables();
        dao = new PersonDAO(conn);
    }

    //Here we close the connection to the database file so it can be opened elsewhere.
    //We will leave commit to false because we have no need to save the changes to the database
    //between test cases
    @AfterEach
    public void tearDown() throws DataAccessException {
        if (db.connectionState() != null) db.closeConnection(false);
        db = null;
    }

    @Test
    @DisplayName("Find one person pass")
    public void findOnePersonPass() throws DataAccessException {
        dao.insert(person);
        Person compareTest = dao.find(person.getPersonID());
        assertNotNull(compareTest);
        assertEquals(person, compareTest);
        assertTrue(person.equals(compareTest));
    }

    @Test
    @DisplayName("Find 2 people pass")
    public void findTwoDiffPeoplePass() throws DataAccessException {
        dao.insert(person);
        dao.insert(person2);
        Person compareTest = dao.find(person.getPersonID());
        assertNotNull(compareTest);
        assertEquals(person, compareTest);
    }

    @Test
    @DisplayName("Find fake person fail")
    public void findFakePersonFail() throws DataAccessException {
        assertNull(dao.find("fake_person"));
    }

    @Test
    @DisplayName("Find in closed database fail")
    public void findInClosedDatabaseFail() throws DataAccessException {
        db.closeConnection(false);
        assertThrows(DataAccessException.class, ()-> dao.find(person.getPersonID()));
    }

    @Test
    @DisplayName("Find null person fail")
    public void findNullPersonFail() throws DataAccessException {
        assertNull(dao.find(null));
    }

//    @Test
//    @DisplayName("Find person from cleared db fail")
//    public void findPersonClearedFail() throws DataAccessException {
//        db.clearTables();
//        assertNull(dao.find(null));
//    }

    @Test
    @DisplayName("Insert people default pass")
    public void insertPassOverall() throws DataAccessException {
        dao.insert(person);
        Person compareTest = dao.find(person.getPersonID()); //technically tests find as well
        assertNotNull(compareTest);
        assertEquals(person, compareTest);
    }


    @Test
    @DisplayName("Insert duplicate people fail")
    public void insertDuplicatePeopleFail() throws DataAccessException {
        //the second person already exists in the database, so it should throw a data access exception
        dao.insert(person);
        assertThrows(DataAccessException.class, ()-> dao.insert(person));
    }


    @Test
    @DisplayName("Insert similar people different parents fail")
    public void insertTwoPeopleDiffParentsFail() throws DataAccessException {
        Person person3 = new Person("test_ID","test_username", "test_firstname", "test_lastname",
                'o',  "test3_father", "test3_mother", "test_spouse");
        dao.insert(person);
        assertThrows(DataAccessException.class, ()-> dao.insert(person3));
    }

    @Test
    @DisplayName("Insert similar people different spouses fail")
    public void insertTwoPeopleDiffSpousesFail() throws DataAccessException {
        Person person3 = new Person("test_ID","test_username", "test_firstname", "test_lastname",
                'o',  "test_father", "test_mother", "test3_spouse");
        dao.insert(person);
        assertThrows(DataAccessException.class, ()-> dao.insert(person3));
    }

    @Test
    @DisplayName("Insert a null person fail")
    public void insertNullPersonFail() {
        assertThrows(NullPointerException.class, ()-> dao.insert(null));
    }

    @Test
    @DisplayName("Insert into closed database fail")
    public void insertIntoClosedDatabaseFail() throws DataAccessException {
        db.closeConnection(false);
        assertThrows(DataAccessException.class, ()-> dao.insert(person));
    }

    @Test
    @DisplayName("Clear person pass")
    public void clearPersonTablePass() throws DataAccessException {
        dao.insert(person);
        Person compareTest1 = dao.find(person.getPersonID());
        assertNotNull(compareTest1);
        assertEquals(person, compareTest1);

        dao.clear(db);
        Person compareTest2 = dao.find(person.getPersonID());
        assertNull(compareTest2);
    }

//    @Test
//    @DisplayName("Clear person from a closed database fail")
//    public void clearPersonTableClosedDatabaseFail() throws DataAccessException {
//        dao.insert(person);
//        Person compareTest1 = dao.find(person.getPersonID());
//        assertNotNull(compareTest1);
//        assertEquals(person, compareTest1);
//        db.closeConnection(false);
//        assertThrows(NullPointerException.class, ()-> dao.clear(db));
//    }

//    @Test
//    @DisplayName("Clear people based on personID pass")
//    public void clearPeopleBasedOnPersonIDPass() throws DataAccessException {
//        dao.insert(person);
//        dao.insert(person2);
////        person2.setSpouse(person.getPersonID());
////        person.setSpouse(person2.getPersonID());
//        person2.setMother(person.getPersonID());
//        person.setMother(person2.getPersonID());
//        dao.clearPeopleRelatingToUser(person2.getPersonID());
//        assertNotNull(dao.find(person.getPersonID()));
//        assertNull(dao.find(person2.getPersonID()));
//    }

    @Test
    @DisplayName("Generate unique personID default pass")
    public void generatePersonIDPass() throws DataAccessException {
        assertNull(dao.find(dao.generatePersonID()));
    }

    @Test
    @DisplayName("Generate two different personID pass")
    public void generateTwoDifferentPersonIDPass() throws DataAccessException {
       String id1 = dao.generatePersonID();
       String id2 = dao.generatePersonID();
       assertNotEquals(id1, id2);
    }

//    @Test
//    @DisplayName("Return all people pass")
//    public void returnAllPeoplePass() throws DataAccessException, SQLException {
//        dao.insert(person);
//        dao.insert(person2);
//        assert(dao.returnAllPeople(person.getPersonID()).contains(person));
//        assert(dao.returnAllPeople(person.getPersonID()).contains(person2));
//    }
//
//    @Test
//    @DisplayName("Return all people empty database fail")
//    public void returnAllPeopleFail() throws DataAccessException, SQLException {
//        dao.clear(db);
//        assertFalse(dao.returnAllPeople(person.getPersonID()).contains(person));
//        assertFalse(dao.returnAllPeople(person.getPersonID()).contains(person2));
//    }


}
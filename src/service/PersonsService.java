package service;

import dao.DataAccessException;
import dao.Database;
import model.Person;
import request_result.PersonsResult;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * holds the algorithm to find all the family of the current user
 * without interacting directly with the database
 */
public class PersonsService {

    private static Logger logger;
    private Database database;

    public PersonsService() {
        database = null;
        logger = Logger.getLogger("familymapserver");
    }

    /**
     *
     * @return a single person object with the specified personID (contained in the PersonResult)
     */
    public PersonsResult findAllPersons(String username) throws DataAccessException {
        logger.log(Level.FINER, "Finding all persons through PersonsService");
        Database db = getCurrDatabase();

        try {
            PersonsResult personsResult = new PersonsResult();
            String personID;

            if (db.connectionState() == null) {
                logger.log(Level.FINE, "OPENING CONNECTION IN FIND ALL PERSONS");
                db.getConnection();
                List<Person> allPersons = db.getPersonDAO().returnPeopleRelatingToUser(username);
                Person[] allPeople = allPersons.toArray(new Person[0]);
                personsResult.addData(allPeople);
                personsResult.setSuccess(true);
                logger.log(Level.FINE, "CLOSING CONNECTION IN FIND ALL PERSONS");
                db.closeConnection(true);
            }
            else {
                List<Person> allPersons = db.getPersonDAO().returnPeopleRelatingToUser(username);
                Person[] allPeople = allPersons.toArray(new Person[0]);
                personsResult.addData(allPeople);
                personsResult.setSuccess(true);
            }
            return personsResult;
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, "Error generated while trying to Find all Persons", e);
            logger.log(Level.SEVERE, "CLOSING BAD CONNECTION IN FIND ALL PERSONS");
            db.closeConnection(false);
            return new PersonsResult(false, "Error: " + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error generated while trying to Find all Persons", e);
            logger.log(Level.SEVERE, "CLOSING BAD CONNECTION IN FIND ALL PERSONS");
            db.closeConnection(false);
            return new PersonsResult(false, "Error: " + e.getMessage());
        }
    }

    public Database getCurrDatabase() {
        if (database == null) database = new Database();
        return database;
    }

    public void setDb(Database db) {
        this.database = db;
    }

//    private void addAllRelated(List<Person> allPersons, String personID, String username) throws DataAccessException {
//        // if the personID is from the current user, then they are related
//        Person person = getCurrDatabase().getPersonDAO().find(personID);
//        person.setUsername(username);
//        getCurrDatabase().getPersonDAO().deletePerson(person);
//        getCurrDatabase().getPersonDAO().insert(person);
//        allPersons.add(person);
//
//        Person spouse = getCurrDatabase().getPersonDAO().find(person.getSpouse());
//        spouse.setUsername(username);
//        allPersons.add(spouse);
//
//        relatedHelper(person, personID, allPersons, username);
//    }
//
//    private void relatedHelper(Person person, String personID, List<Person> allPersons, String username) throws DataAccessException {
//        // if the personID is from the current person, then they are related
//        if (person.getPersonID().equals(personID)) allPersons.add(person);
//
//        // check if personID is related on mother's side
//        if (person.getMother() != null) {
//            Person mother = getCurrDatabase().getPersonDAO().find(person.getMother());
//            mother.setUsername(username);
//            getCurrDatabase().getPersonDAO().deletePerson(mother);
//            getCurrDatabase().getPersonDAO().insert(mother);
//            //allPersons.add(mother);
//
//            relatedHelper(database.getPersonDAO().find(person.getMother()), person.getMother(), allPersons, username);
//        }
//
//        // check if personID is related on father's side
//        if (person.getFather() != null) {
//            Person father = getCurrDatabase().getPersonDAO().find(person.getFather());
//            father.setUsername(username);
//            getCurrDatabase().getPersonDAO().deletePerson(father);
//            getCurrDatabase().getPersonDAO().insert(father);
//            //allPersons.add(father);
//
//            relatedHelper(database.getPersonDAO().find(person.getFather()), person.getFather(), allPersons, username);
//        }
//    }
}

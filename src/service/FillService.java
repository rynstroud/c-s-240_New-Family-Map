package service;

import dao.DataAccessException;
import dao.Database;
import model.Event;
import model.Person;
import random_data.Location;
import random_data.LocationData;
import random_data.Names;
import request_result.FillResult;
import serialize_deserialize.Json_Converter;

import java.io.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * holds the algorithm to fill the database with fake info
 * without interacting directly with the database
 */
public class FillService {

    private static Logger logger;
    private Location[] locations;
    private String[] fnames;
    private String[] mnames;
    private String[] snames;
    private final static int PARENT_AGE = 20;
    private final static int MARRIAGE_AGE = 2; //difference between marriage age and age they had a child
    private final static int DEATH_AGE = 60; //number of years after the child was born that the parent died
    private int numEvents;
    private int numPeople;

    public FillService() {

        numEvents = 0;
        numPeople = 1;
        logger = Logger.getLogger("familymapserver");
        try {
            locations = getGeneratedInfo("json/locations.json", LocationData.class).getData();
            logger.log(Level.FINEST, "made it past deserializing locations");
            fnames = getGeneratedInfo("json/fnames.json", Names.class).getData();
            logger.log(Level.FINEST, "made it past deserializing fnames");
            mnames = getGeneratedInfo("json/mnames.json", Names.class).getData();
            logger.log(Level.FINEST, "made it past deserializing mnames");
            snames = getGeneratedInfo("json/snames.json", Names.class).getData();
            logger.log(Level.FINEST, "made it past deserializing snames");
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, e.getMessage());
            return;
        }
    }

    /**
     *
     * @param username
     * @param numGenerations
     * @return the result of trying to fill the database
     * @throws DataAccessException
     */
    public FillResult fill(String username, int numGenerations) {
        logger.log(Level.FINEST, "Filling database through FillService");
        Database db = new Database();

        try {
            db.getConnection();

            //deleting all events and family members who have the associated username (param)
            logger.log(Level.FINEST, "Deleting info relating to user");
            deleteInfoRelatingToUser(username, db);

            FillResult fillResult = new FillResult();

            //getting the person who has the provided username
            Person person = db.getPersonDAO().find(db.getUserDAO().find(username).getPersonID());

            //adding a birth event so i can base the parents ages off that
            if (db.getEventDAO().findPersonBirth(person.getPersonID()) == null) {
                addEvent(person, db, "Birth", 2000);
            }

            //filling the database with the number of generations provided (param)
            addNewParents(numGenerations, person, db);

            //setting the result object
            String message = "Successfully added " + numPeople + " persons and " + numEvents + " events to the database.";
            fillResult.setSuccess(true);
            fillResult.setMessage(message);

            db.closeConnection(true);
            return fillResult;
        } catch (Exception e) {
            try {
                db.closeConnection(false);
            } catch (DataAccessException ex) {
                logger.log(Level.SEVERE, "Error found in closing connection: " + ex.getMessage(), ex);
            }
            logger.log(Level.SEVERE, "Error: " + e.getMessage(), e);
            return new FillResult(false, "Error: " + e.getMessage());
        }
    }

    //adding the parents to a given user
    private void addNewParents(int generationsLeft, Person person, Database db) throws DataAccessException {
        logger.log(Level.FINEST, "inserting new parents into db");
        try {
            if (person == null) logger.log(Level.SEVERE, "THE PERSON IS NULL SO THIS WON'T WORK");

            String username = person.getAssociatedUsername();

            //calling the addMom function to add the person's fake mother
            Person mother = addMom(person, db, username);

            //calling the addDad function to add the person's fake father
            Person father = addDad(person, db, username);

            //making the parents married to eachother
            updateParentsMarriage(mother, father, person, db);

            if (generationsLeft - 1 > 0) {
                //Call for mom
                addNewParents(generationsLeft - 1, mother, db);

                //Call for dad
                addNewParents(generationsLeft - 1, father, db);
            }
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, "Error generated while trying to Fill", e);
            db.closeConnection(false);
            return;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error generated while trying to Fill", e);
            db.closeConnection(false);
            return;
        }


    }

    // deletes mother and father who are not married and adds the ones who are married
    private void updateParentsMarriage(Person mother, Person father, Person child, Database db) throws DataAccessException {
        mother.setSpouse(father.getPersonID());
        father.setSpouse(mother.getPersonID());
        db.getPersonDAO().deletePerson(mother);
        db.getPersonDAO().deletePerson(father);
        db.getPersonDAO().insert(mother);
        db.getPersonDAO().insert(father);
        addMarriage(mother, father, db, db.getEventDAO().findPersonBirth(child.getPersonID()).getYear() - MARRIAGE_AGE);
    }

    private Person addMom(Person person, Database db, String username) throws DataAccessException {
        Person mother = new Person(db.getPersonDAO().generatePersonID(), username, getName(fnames),
                getName(snames), 'f', null, null, null);
        db.getPersonDAO().insert(mother);

        // updating the database with the addition of the person's mom
        db.getPersonDAO().deletePerson(person);
        person.setMother(mother.getPersonID());
        db.getPersonDAO().insert(person);

        //incrementing the number of people added
        ++numPeople;
        logger.log(Level.FINEST, "numPeople = " + numPeople);

        //adding a birth and death to the mother
        addAllEvents(mother, db, person);
        return mother;
    }

    private Person addDad(Person person, Database db, String username) throws DataAccessException {
        Person father = new Person(db.getPersonDAO().generatePersonID(), username, getName(mnames),
                person.getLastName(), 'f', null, null, person.getMother());
        db.getPersonDAO().insert(father);

        // updating the database with the addition of the person's dad
        db.getPersonDAO().deletePerson(person);
        person.setFather(father.getPersonID());
        db.getPersonDAO().insert(person);

        //incrementing the number of people added
        ++numPeople;
        logger.log(Level.FINEST, "numPeople = " + numPeople);

        //adding a birth and death to the father
        addAllEvents(father, db, person);
        return father;
    }

    //adding a birth and death for the given person
    private void addAllEvents(Person person, Database db, Person child) throws DataAccessException {
        try {
            addEvent(person, db, "Birth", db.getEventDAO().findPersonBirth(child.getPersonID()).getYear() - PARENT_AGE);
            addEvent(person, db, "Death", db.getEventDAO().findPersonBirth(child.getPersonID()).getYear() + DEATH_AGE);
            numEvents += 2;
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, "Error generated while trying to Fill", e);
            db.closeConnection(false);
            return;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error generated while trying to Fill", e);
            db.closeConnection(false);
            return;
        }

    }

    //adding a specific event for a person
    private void addEvent(Person person, Database db, String eventType, int year) throws DataAccessException {
        try {
            Location loc = getLocation();
            Event newEvent = new Event(db.getEventDAO().generateEventID(), person.getAssociatedUsername(), person.getPersonID(),
                    loc.getLatitude(), loc.getLongitude(), loc.getCountry(), loc.getCity(), eventType, year);
            db.getEventDAO().insert(newEvent);
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, "Error generated while trying to Fill", e);
            db.closeConnection(false);
            return;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error generated while trying to Fill", e);
            db.closeConnection(false);
            return;
        }

    }

    //adding a marriage event for a couple
    private void addMarriage(Person mother, Person father, Database db, int year) throws DataAccessException {
        try {
            // Getting the location from the random locations file
            Location loc = getLocation();

            // Adding marriage event for father
            Event dadMarriage = new Event(db.getEventDAO().generateEventID(), father.getAssociatedUsername(), father.getPersonID(),
                    loc.getLatitude(), loc.getLongitude(), loc.getCountry(), loc.getCity(), "Marriage", year);
            db.getEventDAO().insert(dadMarriage);

            //Adding marriage event for mother
            Event momMarriage = new Event(db.getEventDAO().generateEventID(), mother.getAssociatedUsername(), mother.getPersonID(),
                    loc.getLatitude(), loc.getLongitude(), loc.getCountry(), loc.getCity(), "Marriage", year);
            db.getEventDAO().insert(momMarriage);

            numEvents += 2;
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, "Error generated while trying to Fill", e);
            db.closeConnection(false);
            return;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error generated while trying to Fill", e);
            db.closeConnection(false);
            return;
        }

    }

    //getting a name randomly picked from a file
    private String getName(String[] names) {
        Date date = new Date();
        int indexOfName = (int) (date.getTime() % names.length);
        return names[indexOfName];
    }

    //getting a location randomly picked from a file
    private Location getLocation() {
        Date date = new Date();
        int indexOfName = (int) (date.getTime() % locations.length);
        return locations[indexOfName];
    }

    //getting the generated object arrays in the files
    private <T> T getGeneratedInfo(String fileName, Class<T> returnType) throws FileNotFoundException {
        InputStream inputStream = new FileInputStream(fileName);
        Json_Converter converter = new Json_Converter();
        try {
            return converter.deserialize(inputStream, returnType);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
            return null;
        }
    }

    //deleting all connections to family and any personal events
    private void deleteInfoRelatingToUser(String username, Database db) throws DataAccessException {
//        try {
        // clearing user's related events
        db.getEventDAO().clearEventsRelatingToUser(username);

        // deleting person's connections to family without affecting other users
        if (db.getUserDAO().find(username) == null) throw new DataAccessException();
        Person person = db.getPersonDAO().find(db.getUserDAO().find(username).getPersonID());
        person.setMother(null);
        person.setFather(null);
        db.getPersonDAO().deletePerson(person);
        db.getPersonDAO().insert(person);

//        } catch (DataAccessException e) {
//            logger.log(Level.SEVERE, "Error generated while trying to Fill\n\t" + e.getMessage(), e);
//            db.closeConnection(false);
//            return;
//        } catch (Exception e) {
//            logger.log(Level.SEVERE, "Error generated while trying to Fill" + e.getMessage(), e);
//            db.closeConnection(false);
//            return;
//        }
    }


}

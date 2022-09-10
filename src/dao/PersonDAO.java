package dao;

import model.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * the class interacting between the Person class and the database
 */
public class PersonDAO {
    private final Connection conn;
    private static Logger logger;

    public PersonDAO(Connection conn)
    {
        logger = Logger.getLogger("familymapserver");
        this.conn = conn;
    }

    /**
     *
     * @param person the person the client wants to insert into the database
     * @throws DataAccessException
     */
    public void insert(Person person) throws DataAccessException {
        logger.log(Level.FINEST, "Inserting person into db through dao");
        //We can structure our string to be similar to a sql command, but if we insert question
        //marks we can change them later with help from the statement
        String sql = "INSERT INTO Persons (PersonID, AssociatedUsername, FirstName, LastName, Gender, " +
                "Father, Mother, Spouse) VALUES(?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            //Using the statements built-in set(type) functions we can pick the question mark we want
            //to fill in and give it a proper value. The first argument corresponds to the first
            //question mark found in our sql String
            stmt.setString(1, person.getPersonID());
            stmt.setString(2, person.getAssociatedUsername());
            stmt.setString(3, person.getFirstName());
            stmt.setString(4, person.getLastName());
            stmt.setString(5, Character.toString(person.getGender()));
            stmt.setString(6, person.getFather());
            stmt.setString(7, person.getMother());
            stmt.setString(8, person.getSpouse());

            logger.log(Level.FINEST, "Executing update to database");
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new DataAccessException("Error encountered while inserting into the database");
        }
    }

    /**
     *
     * @param personID the id of the person to be found in the database
     * @return the person if they are found; else, return null
     * @throws DataAccessException
     */
    public Person find(String personID) throws DataAccessException {
        logger.log(Level.FINEST, "Finding personID through dao");
        Person person;
        ResultSet rs = null;
        String sql = "SELECT * FROM Persons WHERE PersonID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, personID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                person = new Person(rs.getString("PersonID"), rs.getString("AssociatedUsername"),
                        rs.getString("FirstName"), rs.getString("LastName"),
                        rs.getString("Gender").charAt(0), rs.getString("Father"),
                        rs.getString("Mother"), rs.getString("Spouse"));
                return person;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to find person", e);
            throw new DataAccessException("Error encountered while finding person");
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }


    /**
     * generates a unique personID for a new person in the database
     * @return the personID generated
     * @throws DataAccessException
     */
    public String generatePersonID() throws DataAccessException {
        String temp = UUID.randomUUID().toString();
        while (find(temp) != null) {
            temp = UUID.randomUUID().toString();
        }
        return temp;
    }

    /**
     * clears the person table in the database
     * @param db the database to clear
     * @throws DataAccessException
     */
    public void clear(Database db) throws DataAccessException {
        db.clear("PERSONS");
    }

    /**
     * returns all people relating to a certain user
     * @param username of the person whose relatives are being returned
     * @throws DataAccessException
     */
    public List<Person> returnPeopleRelatingToUser(String username) throws DataAccessException {
        logger.log(Level.FINEST, "Deleting persons related to user through dao");
        List<Person> list = new ArrayList<Person>();
        Person person = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM Persons WHERE AssociatedUsername = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            while (rs.next()) {
                person = new Person(rs.getString("PersonID"), rs.getString("AssociatedUsername"),
                        rs.getString("FirstName"), rs.getString("LastName"),
                        rs.getString("Gender").charAt(0), rs.getString("Father"),
                        rs.getString("Mother"), rs.getString("Spouse"));

                list.add(person);
            }
            return list;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to delete persons relating to user", e);
            throw new DataAccessException("Error encountered while clearing");
        }
    }

    /**
     * clears all people relating to a certain user
     * @param person whose relatives are being cleared
     * @throws DataAccessException
     */
    public void deletePerson(Person person) throws DataAccessException {
        logger.log(Level.FINEST, "Deleting person through dao");
        String sql = "DELETE FROM Persons WHERE PersonID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, person.getPersonID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to delete person", e);
            throw new DataAccessException("Error encountered while clearing");
        }
    }
}

package dao;

import model.User;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * the class interacting between the User class and the database
 */
public class UserDAO {

    private final Connection conn;
    private static Logger logger;

    public UserDAO(Connection conn) {
        this.conn = conn;
        logger = Logger.getLogger("familymapserver");
    }

    /**
     *
     * @param user user to be inserted into the database
     * @throws DataAccessException
     */
    public void insert(User user) throws DataAccessException {
        logger.log(Level.FINEST, "Inserting User through UserDAO");
        //We can structure our string to be similar to a sql command, but if we insert question
        //marks we can change them later with help from the statement
        String sql = "INSERT INTO Users (AssociatedUsername, Password, Email, FirstName, LastName, Gender, " +
                "PersonID) VALUES(?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            //Using the statements built-in set(type) functions we can pick the question mark we want
            //to fill in and give it a proper value. The first argument corresponds to the first
            //question mark found in our sql String
            stmt.setString(1, user.getAssociatedUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getFirstName());
            stmt.setString(5, user.getLastName());
            stmt.setString(6, Character.toString(user.getGender()));
            stmt.setString(7, user.getPersonID());

            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "INSERT FAILED: Error encountered while inserting into the database", e);
            throw new DataAccessException("Error encountered while inserting into the database");
        }
    }

    /**
     *
     * @param userID the id of the user to be found in the database
     * @return the user if found, else null
     */
    public User find(String userID) throws DataAccessException {
        User user;
        ResultSet rs = null;
        String sql = "SELECT * FROM Users WHERE AssociatedUsername = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User(rs.getString("AssociatedUsername"), rs.getString("Password"),
                        rs.getString("Email"), rs.getString("FirstName"),
                        rs.getString("LastName"), rs.getString("Gender").charAt(0),
                        rs.getString("PersonID"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding person");
        } finally {
            if(rs != null) {
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
     * clears the User table in the given database
     * @param db
     * @throws DataAccessException
     */
    public void clear(Database db) throws DataAccessException {
        db.clear("USERS");
    }
}

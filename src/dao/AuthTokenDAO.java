package dao;

import model.AuthToken;

import java.sql.*;
import java.util.Random;
import java.util.UUID;

/**
 * the class interacting between the AuthToken class and the database
 */
public class AuthTokenDAO {
    private final Connection conn;

    public AuthTokenDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     *
     * @param token the token of the authorization token to be found (gives associatedUsername in auth token as well)
     * @return the auth token found
     */
    public AuthToken find(String token) throws DataAccessException {
        AuthToken authToken;
        ResultSet rs = null;
        String sql = "SELECT * FROM Authorization_Token WHERE Token = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            rs = stmt.executeQuery();
            if (rs.next()) {
                authToken = new AuthToken(rs.getString("Token"), rs.getString("AssociatedUsername"),
                        rs.getString("TimeStamp"));
                return authToken;
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
     *
     * @param token the auth token to be inserted into the database
     */
    public void insert(AuthToken token) throws DataAccessException {
        //We can structure our string to be similar to a sql command, but if we insert question
        //marks we can change them later with help from the statement
        String sql = "INSERT INTO Authorization_Token (Token, AssociatedUsername, " +
                "TimeStamp)  VALUES(?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            //Using the statements built-in set(type) functions we can pick the question mark we want
            //to fill in and give it a proper value. The first argument corresponds to the first
            //question mark found in our sql String
            stmt.setString(1, token.getToken());
            stmt.setString(2, token.getUsername());
            stmt.setString(3, token.getTimestamp());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting into the database\n" + e.getMessage());
        }

    }

    /**
     * generates a new auth token for a user's session
     * @return the auth token generated
     * @throws DataAccessException
     */
    public String generateAuthToken() throws DataAccessException {
        String temp = UUID.randomUUID().toString();
        while (find(temp) != null) {
            temp = UUID.randomUUID().toString();
        }
        return temp;
    }

    /**
     * clears the given database of the Authorization_Token table
     * @param db the given database
     * @throws DataAccessException
     */
    public void clear(Database db) throws DataAccessException {
        db.clear("AUTHORIZATION_TOKEN");
    }
}

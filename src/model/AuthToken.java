package model;

import java.util.Random;

/**
 * the class storing information about the authorization token
 * table in the database. It is only in use when the dao's temporarily
 * store information from the database
 */
public class AuthToken {

    private String token;
    private String username;
    private String timestamp;

    /**
     *  @param token the special unique code for authorization
     * @param username the chosen associatedUsername of the user
     * @param timestamp
     */
    public AuthToken(String token, String username, String timestamp) {
        this.token = token;
        this.username = username;
        this.timestamp = timestamp;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) { this.token = token; }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (o instanceof AuthToken) {
            AuthToken oToken = (AuthToken) o;
            return  oToken.getToken().equals(getToken()) &&
                    oToken.getUsername().equals(getUsername()) &&
                    oToken.getTimestamp().equals(getTimestamp());
        } else {
            return false;
        }
    }
}

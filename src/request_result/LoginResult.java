package request_result;

import model.AuthToken;

/**
 * holds the result of the login attempt
 */
public class LoginResult extends Base_Result {
    private String message;
    private boolean success;
    private String authtoken;
    private String username;
    private String personID;

    public LoginResult(boolean s) {
        success = s;
    }

    public LoginResult(boolean s, String m) {
        success = s;
        message = m;
    }

    public LoginResult(String t, String u, String p, boolean s) {
        authtoken = t;
        username = u;
        personID = p;
        success = s;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getAuthtoken() {
        return authtoken;
    }

    public void setAuthtoken(String authtoken) {
        this.authtoken = authtoken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public String getInvalid_val() {
        return invalid_val;
    }
}

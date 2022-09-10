package request_result;

/**
 * holds the result of the client's attempt to register a new user
 */
public class RegisterResult extends Base_Result {

    private String authtoken;
    private String username;
    private String personID;
    private String message;
    private String fatherID;
    private String motherID;
    private boolean success;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public RegisterResult(String authtoken, String username, String personID, boolean success) {
        this.authtoken = authtoken;
        this.username = username;
        this.personID = personID;
        this.success = success;
    }

    public RegisterResult(String authtoken, String username, String personID, String fatherID, String motherID, boolean success) {
        this.authtoken = authtoken;
        this.username = username;
        this.personID = personID;
        this.fatherID = fatherID;
        this.motherID = motherID;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFatherID() {
        return fatherID;
    }

    public void setFatherID(String fatherID) {
        this.fatherID = fatherID;
    }

    public String getMotherID() {
        return motherID;
    }

    public void setMotherID(String motherID) {
        this.motherID = motherID;
    }

    public RegisterResult(String m, boolean s) {
        message = m;
        success = s;
    }


    public String getInvalid_val() {
        return invalid_val;
    }

    public String getUnavailable_username() {
        return unavailable_username;
    }

}

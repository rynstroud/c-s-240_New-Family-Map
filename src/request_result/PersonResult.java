package request_result;

/**
 * holds the result of trying to find a specific person
 */
public class PersonResult extends Base_Result {
    private String associatedUsername;
    private String personID;
    private String firstName;
    private String lastName;
    private String gender;
    private String fatherID;
    private String motherID;
    private String spouseID;
    private boolean success;
    private String message;

    public PersonResult() {
        associatedUsername = null;
        personID = null;
        firstName = null;
        lastName = null;
        gender = null;
        fatherID = null;
        motherID = null;
        spouseID = null;
        success = false;
        message = null;
    }

    public PersonResult(boolean s, String m) {
        success = s;
        message = m;
        associatedUsername = null;
        personID = null;
        firstName = null;
        lastName = null;
        gender = null;
        fatherID = null;
        motherID = null;
        spouseID = null;

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAssociatedUsername() {
        return associatedUsername;
    }

    public void setAssociatedUsername(String associatedUsername) {
        this.associatedUsername = associatedUsername;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = String.valueOf(gender);
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

    public String getSpouseID() {
        return spouseID;
    }

    public void setSpouseID(String spouseID) {
        this.spouseID = spouseID;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getInvalid_auth() {
        return invalid_auth;
    }

    public String getInvalid_personID_param() {
        return invalid_personID_param;
    }

    public String getUser_not_contain_person() {
        return user_not_contain_person;
    }
}

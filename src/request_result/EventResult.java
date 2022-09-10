package request_result;

/**
 * holds the result of the request to find a specific event
 */
public class EventResult extends Base_Result {
    private String eventID;
    private String associatedUsername;
    private String personID;
    private float latitude;
    private float longitude;
    private String country;
    private String city;
    private String eventType;
    private String year;
    private String message;
    private boolean success;

    public EventResult() {
        eventID = null;
        associatedUsername = null;
        personID = null;
        country = null;
        city = null;
        eventType = null;
        year = null;
        message = null;
        success = false;
    }

    public EventResult(boolean s, String m) {
        eventID = null;
        associatedUsername = null;
        personID = null;
        country = null;
        city = null;
        eventType = null;
        year = null;
        success = s;
        message = m;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
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

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = Integer.toString(year);
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
}

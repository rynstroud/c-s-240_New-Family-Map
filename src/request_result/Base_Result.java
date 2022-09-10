package request_result;

/**
 * holds the basic error messages and result strings
 * and is implemented in each result class
 */
public class Base_Result {
    protected transient final String invalid_val = "Request property missing or has invalid value";
    protected transient final String unavailable_username = "Username already taken by another user";
    protected transient final String internal_error = "Internal server error";
    protected transient final String invalid_param = "Invalid associatedUsername or generations parameter";
    protected transient final String invalid_auth = "Invalid auth token";
    protected transient final String invalid_personID_param = "Invalid personID parameter";
    protected transient final String user_not_contain_person = "Requested person does not belong to this user";
    protected transient final String invalid_eventID_param = "Invalid eventID parameter";
    protected transient final String user_not_contain_event = "Requested event does not belong to this user";

    public String getInternal_Error() {
        return internal_error;
    }

}

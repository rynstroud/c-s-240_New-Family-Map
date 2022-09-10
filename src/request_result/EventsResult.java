package request_result;

import model.Event;
import model.Person;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * holds the result of the FindAllEventsRequest
 */
public class EventsResult extends Base_Result {
    private Event[] data;
    private boolean success;
    private String message;

    public EventsResult() {
    }

    public EventsResult(boolean s, String m) {
        success = s;
        message = m;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void addData(Event[] data) {
        this.data = data;
    }

    public Event[] getData() {
        return data;
    }

    public String getInvalid_auth() {
        return invalid_auth;
    }
}

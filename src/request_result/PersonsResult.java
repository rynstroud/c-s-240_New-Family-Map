package request_result;

import model.Person;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * holds the result of the PersonsService which tries to return all the
 * family members of the user
 */
public class PersonsResult extends Base_Result {
    private Person[] data;
    private boolean success;
    private String message;

    public PersonsResult() {
    }

    public PersonsResult(boolean s, String m) {
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

    public void addData(Person[] data) {
        this.data = data;
    }

    public Person[] getData() {
        return data;
    }

}

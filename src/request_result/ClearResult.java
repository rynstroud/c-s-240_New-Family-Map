package request_result;

/**
 * holds the result of the clear service
 */
public class ClearResult extends Base_Result {
    String message;
    boolean success;

    public ClearResult() {
        message = null;
        success = false;
    }

    public ClearResult(boolean success) {
        if (success) message = "Clear succeeded";
        this.success = success;
    }

    public ClearResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}

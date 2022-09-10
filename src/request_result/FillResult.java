package request_result;

/**
 * holds the result of the client's request to fill the database
 */
public class FillResult extends Base_Result {
    private String message;
    private boolean success;

    public FillResult() {
        message = null;
        success = false;
    }

    public FillResult(boolean success) {
        if (success) message = "Fill succeeded";
        this.success = success;
    }

    public FillResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

}

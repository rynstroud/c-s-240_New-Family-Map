package request_result;

/**
 * holds the result of trying to load the entire family history
 */
public class LoadResult extends Base_Result {
    private String message;
    private boolean success;

    public LoadResult() {
        message = null;
        success = false;
    }

    public LoadResult(boolean success) {
        if (success) {
            message = "Fill succeeded";
            this.success = success;
        }
        else {
            message = getInternal_Error();
            this.success = success;
        }

    }

    public LoadResult(boolean success, String message) {
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

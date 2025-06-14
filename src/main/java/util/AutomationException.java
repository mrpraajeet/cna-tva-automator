package util;

import data.ErrorCode;

public class AutomationException extends RuntimeException {
    private final ErrorCode errorCode;

    public AutomationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public AutomationException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public AutomationException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    public AutomationException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

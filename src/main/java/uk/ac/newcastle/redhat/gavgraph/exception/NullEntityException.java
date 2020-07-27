package uk.ac.newcastle.redhat.gavgraph.exception;

import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class NullEntityException extends RuntimeException{

    private static final String defaultMsg = "Bad request with null entity";

    private final Map<String, String> reasons;

    private final HttpStatus status;

    public NullEntityException() {
        super(defaultMsg);
        this.reasons = new HashMap<>();
        this.status = HttpStatus.BAD_REQUEST;
    }

    public NullEntityException(String message, HttpStatus status) {
        super(message);
        this.reasons = new HashMap<>();
        this.status = status;
    }

    public NullEntityException(String message, Map<String, String> reasons, HttpStatus status) {
        super(message);
        this.reasons = new HashMap<>();
        this.status = status;
    }

    public Map<String, String> getReasons() {
        return reasons;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static String getDefaultMsg() {
        return defaultMsg;
    }
}

package uk.ac.newcastle.redhat.gavgraph.exception;

import lombok.Getter;

@Getter
public enum ResultCode {
    SUCCESS(1000, "Success"),
    FAILED(1001, "Failed"),
    VALIDATE_FAILED(1002, "validate failed"),
    ERROR(5000, "Unknown Error");

    private int code;
    private String msg;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}

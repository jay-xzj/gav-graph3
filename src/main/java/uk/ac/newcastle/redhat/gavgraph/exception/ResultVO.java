package uk.ac.newcastle.redhat.gavgraph.exception;

import lombok.Getter;

@Getter
public class ResultVO<T> {

    /* status code */
    private int code;
    /* response msg */
    private String msg;
    /* response data */
    private T data;

    public ResultVO(T data){
        this(ResultCode.SUCCESS,data);
    }

    public ResultVO(ResultCode resultCode, T data) {
        this.code = resultCode.getCode();
        this.msg = resultCode.getMsg();
        this.data = data;
    }
}

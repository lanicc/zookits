package io.github.lanicc.zookits.command;

import java.util.Objects;

/**
 * Created on 2022/7/14.
 *
 * @author lan
 */
public class Response<T> extends Command {

    public final static int SUCCESS_CODE = 200;
    public final static int FAIL_CODE = 400;

    public final static int ERROR_CODE = 500;

    private T data;

    private boolean success;

    private String msg;

    private int code;

    public static <T> Response<T> success(T t) {
        return new Response<>(t, true, null, SUCCESS_CODE);
    }

    public static <T> Response<T> fail(String msg) {
        return new Response<>(null, false, msg, FAIL_CODE);
    }

    public static <T> Response<T> error(String msg) {
        return new Response<>(null, false, msg, ERROR_CODE);
    }

    public boolean isError() {
        return !isSuccess() && Objects.equals(code, ERROR_CODE);
    }

    public Response() {
    }

    public Response(T data, boolean success, String msg, int code) {
        this.data = data;
        this.success = success;
        this.msg = msg;
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public Response<T> setData(T data) {
        this.data = data;
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public Response<T> setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public Response<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public int getCode() {
        return code;
    }

    public Response<T> setCode(int code) {
        this.code = code;
        return this;
    }
}

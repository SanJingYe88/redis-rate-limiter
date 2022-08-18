package it.com.ratelimiter.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.com.ratelimiter.constant.ResponseCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class ServerResponseUtil implements Serializable {

    private static final long serialVersionUID = 7498483649536881777L;

    private Integer status;

    private String msg;

    private Object data;

    private ServerResponseUtil() {
    }

    public ServerResponseUtil(Integer status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    @JsonIgnore
    public boolean isSuccess() {
        return Objects.equals(this.status, ResponseCode.SUCCESS.getCode());
    }

    public static ServerResponseUtil success() {
        return new ServerResponseUtil(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMsg(), null);
    }

    public static ServerResponseUtil success(String msg) {
        return new ServerResponseUtil(ResponseCode.SUCCESS.getCode(), msg, null);
    }

    public static ServerResponseUtil success(Object data) {
        return new ServerResponseUtil(ResponseCode.SUCCESS.getCode(), null, data);
    }

    public static ServerResponseUtil success(String msg, Object data) {
        return new ServerResponseUtil(ResponseCode.SUCCESS.getCode(), msg, data);
    }

    public static ServerResponseUtil error(String msg) {
        return new ServerResponseUtil(ResponseCode.ERROR.getCode(), msg, null);
    }

    public static ServerResponseUtil error(Object data) {
        return new ServerResponseUtil(ResponseCode.ERROR.getCode(), null, data);
    }

    public static ServerResponseUtil error(String msg, Object data) {
        return new ServerResponseUtil(ResponseCode.ERROR.getCode(), msg, data);
    }
}

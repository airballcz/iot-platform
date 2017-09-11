package org.opensource.demo.iot.server.exception;

/**
 * 认证异常
 *
 * Created by zchen@idelan.cn on 2017/9/11.
 */
public class AuthenticationException extends RuntimeException {

    public AuthenticationException() {

    }

    public AuthenticationException(String msg) {
        super(msg);
    }

    public AuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }

}

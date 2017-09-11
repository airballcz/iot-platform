package org.opensource.demo.iot.server.exception;

/**
 * MQTT连接异常
 *
 * Created by zchen@idelan.cn on 2017/9/11.
 */
public class MqttConnectionException extends RuntimeException {

    public MqttConnectionException() {

    }

    public MqttConnectionException(String msg) {
        super(msg);
    }

    public MqttConnectionException(String msg, Throwable cause) {
        super(msg, cause);
    }

}

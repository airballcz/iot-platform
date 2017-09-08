package org.opensource.demo.iot.server.handler;

/**
 * Created by zchen@idelan.cn on 2017/9/7.
 */
public class MqttPublishHandler {
    private static MqttPublishHandler ourInstance = new MqttPublishHandler();

    public static MqttPublishHandler getInstance() {
        return ourInstance;
    }

    private MqttPublishHandler() {
    }
}

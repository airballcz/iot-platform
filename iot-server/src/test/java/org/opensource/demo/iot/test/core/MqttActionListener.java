package org.opensource.demo.iot.test.core;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zchen@idelan.cn on 2017/5/11.
 */
public class MqttActionListener implements IMqttActionListener {

    private final Logger logger = LoggerFactory.getLogger(MqttActionListener.class);

    public MqttActionListener() {

    }

    public void onSuccess(IMqttToken asyncActionToken) {
        logger.debug("连接Mqtt服务成功");
    }

    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        logger.warn("连接Mqtt服务异常:{}", exception.getMessage());
    }
}

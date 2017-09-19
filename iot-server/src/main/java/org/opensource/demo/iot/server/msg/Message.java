package org.opensource.demo.iot.server.msg;

import io.netty.handler.codec.mqtt.MqttQoS;

/**
 * 消息格式
 * Created by zchen@idelan.cn on 2017/9/19.
 */
public class Message {

    private String topic;

    private String payload;

    private MqttQoS qoS;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public MqttQoS getQoS() {
        return qoS;
    }

    public void setQoS(MqttQoS qoS) {
        this.qoS = qoS;
    }
}

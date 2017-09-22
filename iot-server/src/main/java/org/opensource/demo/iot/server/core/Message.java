package org.opensource.demo.iot.server.core;

import io.netty.handler.codec.mqtt.MqttQoS;

/**
 * MQTT消息格式
 * <p>
 * Created by zchen@idelan.cn on 2017/9/22.
 */
public class Message {

    private int id;

    private String topicName;

    private String payload;

    private MqttQoS qoS;

    private boolean isRetain;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
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

    public boolean getIsRetain() {
        return isRetain;
    }

    public void setIsRetain(boolean isRetain) {
        isRetain = isRetain;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Message)) {
            return false;
        }

        return this.id == ((Message) obj).id;
    }

    @Override
    public int hashCode() {
        return this.id;
    }
}

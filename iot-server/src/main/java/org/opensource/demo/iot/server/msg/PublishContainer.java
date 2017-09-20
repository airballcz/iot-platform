package org.opensource.demo.iot.server.msg;

import io.netty.handler.codec.mqtt.MqttQoS;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 发布消息容器，用来存储所有主题对应的消息内容、质量等级等内容
 * <p>
 * Created by zchen@idelan.cn on 2017/9/19.
 */
public class PublishContainer {

    // TODO: 2017/9/20 未考虑通配符的情况
    private static final ConcurrentHashMap<String, Message> container = new ConcurrentHashMap<>();

    public static void put(String topic, String payload, MqttQoS qoS) {
        if (qoS == MqttQoS.AT_MOST_ONCE) {
            // TODO: 2017/9/19 查找是否存在此主题，并从消息列表删除

        }

        Message message = new Message();
        message.setTopic(topic);
        message.setPayload(payload);
        message.setQoS(qoS);

        container.put(topic, message);
    }

    public static Message get(String topic) {
        return container.get(topic);
    }

    public static class Message {

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

}

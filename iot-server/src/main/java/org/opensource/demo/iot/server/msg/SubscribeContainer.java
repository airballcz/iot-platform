package org.opensource.demo.iot.server.msg;

import io.netty.handler.codec.mqtt.MqttQoS;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * 订阅消息容器，用于保存主题和sessionId(Channel)关系
 * 数据结构：key=topic,value=LinkedList,LinkedList=SubscribeMessage1,SubscribeMessage2...SubscribeMessageN
 * <p>
 * Created by zchen@idelan.cn on 2017/9/20.
 */
public class SubscribeContainer {

    private static final Map<String, LinkedList<Message>> container = new HashMap<>();

    public static void put(String topic, String sessionId, MqttQoS qos) {
        Message message = new Message();
        message.setSessionId(sessionId);
        message.setQoS(qos);

        LinkedList<Message> messages;
        if (!container.containsKey(topic)) {
            messages = new LinkedList<>();
            messages.add(message);
            container.put(topic, messages);
        } else {
            messages = container.get(topic);
            if (!messages.contains(message)) {
                messages.add(message);
            }
        }
    }

    public static Iterator<Message> get(String topic) {
        LinkedList<Message> messages = container.get(topic);
        if (messages != null) {
            messages.iterator();
        }

        return null;
    }

    /**
     * 订阅消息结构
     */
    public static class Message {

        private String sessionId;

        private MqttQoS qoS;

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public MqttQoS getQoS() {
            return qoS;
        }

        public void setQoS(MqttQoS qoS) {
            this.qoS = qoS;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }

            if (obj == this) {
                return true;
            }

            if (!(obj instanceof SubscribeContainer.Message)) {
                return false;
            }

            return this.sessionId.equals(((SubscribeContainer.Message) obj).getSessionId());
        }

        @Override
        public int hashCode() {
            if (this.sessionId == null) {
                return 0;
            }

            return this.sessionId.hashCode();
        }
    }

}

package org.opensource.demo.iot.server.msg;

import io.netty.handler.codec.mqtt.MqttQoS;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息处理工具，用来存储所有主题对应的消息内容、质量等级等内容
 * <p>
 * Created by zchen@idelan.cn on 2017/9/19.
 */
public class MessageHandler {

    private static final ConcurrentHashMap<String, Message> MESSAGE_MAP = new ConcurrentHashMap<>();

    public static void put(String topic, String payload, MqttQoS qoS) {
        if(qoS==MqttQoS.AT_MOST_ONCE) {
            // TODO: 2017/9/19 查找是否存在此主题，并从消息列表删除

        }

        Message message = new Message();
        message.setTopic(topic);
        message.setPayload(payload);
        message.setQoS(qoS);

        MESSAGE_MAP.put(topic, message);
    }

}

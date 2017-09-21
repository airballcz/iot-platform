package org.opensource.demo.iot.server.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;
import org.opensource.demo.iot.server.msg.PublishContainer;
import org.opensource.demo.iot.server.msg.SubscribeContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by zchen@idelan.cn on 2017/9/8.
 */
public class MqttSubscribeHandler {

    private static final Logger logger = LoggerFactory.getLogger(MqttSubscribeHandler.class);

    private static MqttSubscribeHandler ourInstance = new MqttSubscribeHandler();

    public static MqttSubscribeHandler getInstance() {
        return ourInstance;
    }

    private MqttSubscribeHandler() {
    }

    public MqttMessage doMessage(Channel channel, MqttMessage msg) {
        logger.debug("MQTT SUBSCRIBE");
        MqttFixedHeader fixedHeader;

        MqttSubscribePayload subscribePayload = (MqttSubscribePayload) msg.payload();
        List<MqttTopicSubscription> topicSubscriptions = subscribePayload.topicSubscriptions();
        MqttTopicSubscription topicSubscription;
        MqttQoS qos;
        String topic, sessionId;

        PublishContainer.Message message;
        MqttPublishVariableHeader publishVariableHeader;
        MqttPublishMessage publishMessage;
        for (int i = 0; i < topicSubscriptions.size(); i++) {
            topicSubscription = topicSubscriptions.get(i);
            qos = topicSubscription.qualityOfService();
            topic = topicSubscription.topicName();
            sessionId = channel.id().asLongText();

            // 保存订阅相关信息
            SubscribeContainer.put(topic, sessionId, qos);

            // publish符合主题的消息至当前channel
            message = PublishContainer.get(topic);
            if (message != null) {
                fixedHeader = new MqttFixedHeader(MqttMessageType.PUBLISH, false, message.getQoS(), true, 0);
                publishVariableHeader = new MqttPublishVariableHeader(message.getTopic(), 1);
                publishMessage = new MqttPublishMessage(fixedHeader, publishVariableHeader, Unpooled.copiedBuffer(message.getPayload().getBytes()));

                channel.writeAndFlush(publishMessage);
            }
        }

        fixedHeader = new MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.AT_LEAST_ONCE, true, 0);
        MqttMessageIdVariableHeader messageIdVariableHeader = MqttMessageIdVariableHeader.from(1);
        MqttSubAckPayload subAckPayload = new MqttSubAckPayload(MqttQoS.AT_LEAST_ONCE.value());
        MqttSubAckMessage subAckMessage = new MqttSubAckMessage(fixedHeader, messageIdVariableHeader, subAckPayload);

        return subAckMessage;
    }

}

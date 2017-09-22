package org.opensource.demo.iot.server.handler.mqtt;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import org.opensource.demo.iot.server.msg.ManagerHandler;
import org.opensource.demo.iot.server.msg.PublishContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by zchen@idelan.cn on 2017/9/8.
 */
public class SubscribeHandler {

    private static final Logger logger = LoggerFactory.getLogger(SubscribeHandler.class);

    private static SubscribeHandler ourInstance = new SubscribeHandler();

    public static SubscribeHandler getInstance() {
        return ourInstance;
    }

    private SubscribeHandler() {
    }

    public MqttMessage doMessage(Channel channel, MqttMessage msg) {
        String channelId = channel.id().asLongText();
        logger.debug("MQTT SUBSCRIBE " + channelId);

        // --可变报头--
        MqttMessageIdVariableHeader messageIdVariableHeader = (MqttMessageIdVariableHeader) msg.variableHeader();
        int messageId = messageIdVariableHeader.messageId();

        // --有效载荷--
        MqttSubscribePayload subscribePayload = (MqttSubscribePayload) msg.payload();

        MqttQoS qos;
        String topicName;
        MqttTopicSubscription topicSubscription;
        PublishContainer.Message message;

        List<MqttTopicSubscription> topicSubscriptions = subscribePayload.topicSubscriptions();
        for (int i = 0; i < topicSubscriptions.size(); i++) {
            topicSubscription = topicSubscriptions.get(i);
            qos = topicSubscription.qualityOfService();
            topicName = topicSubscription.topicName();

            // publish符合主题的消息至当前channel
            message = PublishContainer.get(topicName);
            if (message != null) {
                ManagerHandler.subscribeMessage(topicName, Unpooled.copiedBuffer(message.getPayload().getBytes()), false, qos, true, channel);
            }
        }

        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.AT_LEAST_ONCE, true, 0);
        messageIdVariableHeader = MqttMessageIdVariableHeader.from(messageId);
        MqttSubAckPayload subAckPayload = new MqttSubAckPayload(MqttQoS.AT_LEAST_ONCE.value());
        MqttSubAckMessage subAckMessage = new MqttSubAckMessage(fixedHeader, messageIdVariableHeader, subAckPayload);

        return subAckMessage;
    }

}

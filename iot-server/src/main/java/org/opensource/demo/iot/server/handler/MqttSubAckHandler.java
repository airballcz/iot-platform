package org.opensource.demo.iot.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;
import org.opensource.demo.iot.server.core.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zchen@idelan.cn on 2017/9/8.
 */
public class MqttSubAckHandler {

    private static final Logger logger = LoggerFactory.getLogger(MqttSubAckHandler.class);

    private static MqttSubAckHandler ourInstance = new MqttSubAckHandler();

    public static MqttSubAckHandler getInstance() {
        return ourInstance;
    }

    private MqttSubAckHandler() {
    }

    public MqttMessage doMessage(ChannelHandlerContext ctx, MqttMessage msg) {
        logger.debug("MQTT SUBSCRIBE");

        MqttSubscribePayload subscribePayload = (MqttSubscribePayload) msg.payload();
        List<MqttTopicSubscription> topicSubscriptions = subscribePayload.topicSubscriptions();
        MqttTopicSubscription topicSubscription;
        for (int i = 0; i < topicSubscriptions.size(); i++) {
            topicSubscription = topicSubscriptions.get(i);
            ApplicationContext.setContext(topicSubscription.topicName(), ctx);
        }

        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.AT_LEAST_ONCE, true, 0);
        MqttMessageIdVariableHeader messageIdVariableHeader = MqttMessageIdVariableHeader.from(1);
        MqttSubAckPayload subAckPayload = new MqttSubAckPayload(MqttQoS.AT_LEAST_ONCE.value());
        MqttSubAckMessage subAckMessage = new MqttSubAckMessage(fixedHeader, messageIdVariableHeader, subAckPayload);

        return subAckMessage;
    }

}

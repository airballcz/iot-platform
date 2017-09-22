package org.opensource.demo.iot.server.handler.mqtt;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 取消订阅Handler
 * <p>
 * Created by zchen@idelan.cn on 2017/9/20.
 */
public class UnSubscribeHandler {

    private static final Logger logger = LoggerFactory.getLogger(UnSubscribeHandler.class);

    private static UnSubscribeHandler ourInstance = new UnSubscribeHandler();

    public static UnSubscribeHandler getInstance() {
        return ourInstance;
    }

    private UnSubscribeHandler() {
    }

    public MqttMessage doMessage(Channel channel, MqttMessage msg) {
        String channelId = channel.id().asLongText();
        logger.debug("MQTT UNSUBSCRIBE " + channelId);

        // --可变报头--
        MqttMessageIdVariableHeader messageIdVariableHeader = (MqttMessageIdVariableHeader) msg.variableHeader();
        int messageId = messageIdVariableHeader.messageId();

        // --有效载荷--
        MqttUnsubscribePayload mqttUnsubscribePayload = (MqttUnsubscribePayload) msg.payload();
        List<String> topics = mqttUnsubscribePayload.topics();
        for (String topic : topics) {
            // TODO: 2017/9/22 删除订阅相关信息

        }

        // --响应报文--
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.UNSUBACK, false, MqttQoS.AT_LEAST_ONCE, true, 0);
        messageIdVariableHeader = MqttMessageIdVariableHeader.from(messageId);
        MqttUnsubAckMessage unsubAckMessage = new MqttUnsubAckMessage(mqttFixedHeader, messageIdVariableHeader);

        return unsubAckMessage;
    }

}

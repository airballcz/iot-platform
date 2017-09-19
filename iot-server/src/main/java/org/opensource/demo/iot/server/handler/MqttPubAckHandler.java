package org.opensource.demo.iot.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import org.opensource.demo.iot.server.core.ApplicationContext;
import org.opensource.demo.iot.server.msg.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zchen@idelan.cn on 2017/9/7.
 */
public class MqttPubAckHandler {

    private static final Logger logger = LoggerFactory.getLogger(MqttConAckHandler.class);

    private Map<String, String> topics = new HashMap<String, String>();

    private static MqttPubAckHandler ourInstance = new MqttPubAckHandler();

    public static MqttPubAckHandler getInstance() {
        return ourInstance;
    }

    private MqttPubAckHandler() {
    }

    public MqttMessage doMessage(MqttMessage msg) {
        logger.debug("MQTT PUBLISH");

        MqttFixedHeader fixedHeader = msg.fixedHeader();

        MqttQoS qoS = fixedHeader.qosLevel();
        // TODO: 2017/9/19 对于非AT_MOST_ONCE，如何重发消息，设置处理逻辑
        if (qoS != MqttQoS.AT_MOST_ONCE && fixedHeader.isDup()) {

        }

        boolean isRetain = fixedHeader.isRetain();  // 是否保留此次发布信息

        MqttPublishVariableHeader publishVariableHeader = (MqttPublishVariableHeader) msg.variableHeader();
        String topicName = publishVariableHeader.topicName();
        ByteBuf payload = (ByteBuf) msg.payload();
        if(isRetain) {      // 保存相关内容
            MessageHandler.put(topicName, payload.toString(Charset.forName("UTF-8")), fixedHeader.qosLevel());
            // TODO: 2017/9/19 设法保存主题和订阅者相关信息

        }
        logger.debug("topicName:" + topicName + ",payload:" + payload.toString(Charset.forName("UTF-8")));

        // 发布消息至相关订阅
        Channel channel = ApplicationContext.getContext(topicName);
        if (channel != null) {
            fixedHeader = new MqttFixedHeader(MqttMessageType.PUBLISH, false, MqttQoS.AT_LEAST_ONCE, true, 0);
            publishVariableHeader = new MqttPublishVariableHeader(topicName, 1);
            MqttPublishMessage publishMessage = new MqttPublishMessage(fixedHeader, publishVariableHeader, payload);
            channel.writeAndFlush(publishMessage);
        }

        // 消息响应反馈
        fixedHeader = new MqttFixedHeader(MqttMessageType.PUBACK, false, MqttQoS.AT_LEAST_ONCE, true, 0);
        MqttPubAckMessage pubAckMessage = new MqttPubAckMessage(fixedHeader, MqttMessageIdVariableHeader.from(1));

        return pubAckMessage;
    }

}

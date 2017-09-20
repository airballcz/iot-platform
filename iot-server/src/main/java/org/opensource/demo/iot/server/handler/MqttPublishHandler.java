package org.opensource.demo.iot.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import org.opensource.demo.iot.server.core.ApplicationContext;
import org.opensource.demo.iot.server.msg.PublishContainer;
import org.opensource.demo.iot.server.msg.SubscribeContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by zchen@idelan.cn on 2017/9/7.
 */
public class MqttPublishHandler {

    private static final Logger logger = LoggerFactory.getLogger(MqttConnectHandler.class);

    private Map<String, String> topics = new HashMap<String, String>();

    private static MqttPublishHandler ourInstance = new MqttPublishHandler();

    public static MqttPublishHandler getInstance() {
        return ourInstance;
    }

    private MqttPublishHandler() {
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
        if (isRetain) {      // 保存需要retain的主题
            PublishContainer.put(topicName, payload.toString(Charset.forName("UTF-8")), fixedHeader.qosLevel());
        }
        logger.debug("topicName:" + topicName + ",payload:" + payload.toString(Charset.forName("UTF-8")));

        // 发布消息至相关符合主题的订阅channel
        MqttPublishMessage publishMessage;
        Iterator<SubscribeContainer.Message> messages = SubscribeContainer.get(topicName);
        SubscribeContainer.Message message;
        Channel channel;
        if (messages != null) {
            while (messages.hasNext()) {
                message = messages.next();

                fixedHeader = new MqttFixedHeader(MqttMessageType.PUBLISH, false, message.getQoS(), true, 0);
                publishVariableHeader = new MqttPublishVariableHeader(topicName, 1);
                publishMessage = new MqttPublishMessage(fixedHeader, publishVariableHeader, payload);

                channel = ApplicationContext.getChannelBySessionId(message.getSessionId());
                if (channel != null) {
                    channel.write(publishMessage);
                }
            }
        }

        // 消息响应反馈
        fixedHeader = new MqttFixedHeader(MqttMessageType.PUBACK, false, MqttQoS.AT_LEAST_ONCE, true, 0);
        MqttPubAckMessage pubAckMessage = new MqttPubAckMessage(fixedHeader, MqttMessageIdVariableHeader.from(1));

        return pubAckMessage;
    }

}

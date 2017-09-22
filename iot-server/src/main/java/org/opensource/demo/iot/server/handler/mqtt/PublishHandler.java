package org.opensource.demo.iot.server.handler.mqtt;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.mqtt.*;
import org.opensource.demo.iot.server.msg.PublishContainer;
import org.opensource.demo.iot.server.msg.ManagerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

/**
 * Created by zchen@idelan.cn on 2017/9/7.
 */
public class PublishHandler {

    private static final Logger logger = LoggerFactory.getLogger(PublishHandler.class);

    private static PublishHandler ourInstance = new PublishHandler();

    public static PublishHandler getInstance() {
        return ourInstance;
    }

    private PublishHandler() {
    }

    public MqttMessage doMessage(MqttMessage msg) {
        logger.debug("MQTT PUBLISH");

        // --固定报头--
        MqttFixedHeader fixedHeader = msg.fixedHeader();

        boolean isRetain = fixedHeader.isRetain();  // 1-保留主题；0-不保留主题；
        MqttQoS qoS = fixedHeader.qosLevel();

        // --可变报头--
        MqttPublishVariableHeader publishVariableHeader = (MqttPublishVariableHeader) msg.variableHeader();
        String topicName = publishVariableHeader.topicName();
        ByteBuf payload = (ByteBuf) msg.payload();
        logger.debug("topicName:" + topicName + ",payload:" + payload.toString(Charset.forName("UTF-8")));

        ManagerHandler.publishMessage(topicName, payload, false, qoS, false);

        if (isRetain) {      // 保存需要retain的主题
            PublishContainer.put(topicName, payload.toString(Charset.forName("UTF-8")), fixedHeader.qosLevel());
        }

        if (qoS == MqttQoS.AT_LEAST_ONCE) {         // 回复PUBACK报文
            fixedHeader = new MqttFixedHeader(MqttMessageType.PUBACK, false, MqttQoS.AT_LEAST_ONCE, true, 0);
            MqttPubAckMessage pubAckMessage = new MqttPubAckMessage(fixedHeader, MqttMessageIdVariableHeader.from(1));
            return pubAckMessage;

        } else if (qoS == MqttQoS.EXACTLY_ONCE) {   // 回复PUBREC报文
            // TODO: 2017/9/22 QOS=2 回复未实现
            return null;
        }

        return null;
    }

}

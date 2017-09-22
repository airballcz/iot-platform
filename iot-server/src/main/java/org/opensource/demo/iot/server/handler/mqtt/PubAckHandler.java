package org.opensource.demo.iot.server.handler.mqtt;

import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import org.opensource.demo.iot.server.msg.ManagerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Qos=1时，客户端对服务端PUBLISH的响应报文
 * 根据packetId，从发送消息队列中删除对应消息
 *
 * Created by zchen@idelan.cn on 2017/9/22.
 */
public class PubAckHandler {

    private static final Logger logger = LoggerFactory.getLogger(PubAckHandler.class);

    private static PubAckHandler ourInstance = new PubAckHandler();

    public static PubAckHandler getInstance() {
        return ourInstance;
    }

    private PubAckHandler() {
    }

    public MqttMessage doMessage(MqttMessage msg) {
        logger.debug("MQTT PUBACK");

        MqttPublishVariableHeader publishVariableHeader = (MqttPublishVariableHeader) msg.variableHeader();
        int packetId = publishVariableHeader.packetId();
        ManagerHandler.removeSendedMessage(packetId);

        return null;
    }

}

package org.opensource.demo.iot.server.handler;

import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PING响应Handler
 *
 * Created by zchen@idelan.cn on 2017/9/8.
 */
public class MqttPingRespHandler {

    private static final Logger logger = LoggerFactory.getLogger(MqttPingRespHandler.class);

    private static MqttPingRespHandler ourInstance = new MqttPingRespHandler();

    public static MqttPingRespHandler getInstance() {
        return ourInstance;
    }

    private MqttPingRespHandler() {
    }

    public MqttMessage doMessage(MqttMessage msg) {
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PINGRESP, false, MqttQoS.AT_LEAST_ONCE, true, 0);
        MqttMessage message = new MqttMessage(fixedHeader);

        return message;
    }

}

package org.opensource.demo.iot.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import org.opensource.demo.iot.server.core.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PING响应Handler
 * <p>
 * Created by zchen@idelan.cn on 2017/9/8.
 */
public class MqttPingReqHandler {

    private static final Logger logger = LoggerFactory.getLogger(MqttPingReqHandler.class);

    private static MqttPingReqHandler ourInstance = new MqttPingReqHandler();

    public static MqttPingReqHandler getInstance() {
        return ourInstance;
    }

    private MqttPingReqHandler() {
    }

    public MqttMessage doMessage(Channel channel, MqttMessage msg) {
        String channelId = channel.id().asLongText();
        logger.debug("MQTT PINGREQ " + channelId);

        // 更新最新连接时间
        ApplicationContext.updateChannelConTime(channelId);

        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PINGRESP, false, MqttQoS.AT_LEAST_ONCE, true, 0);
        MqttMessage message = new MqttMessage(fixedHeader);

        return message;
    }

}

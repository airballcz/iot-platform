package org.opensource.demo.iot.server.handler;

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
public class MqttPingRespHandler {

    private static final Logger logger = LoggerFactory.getLogger(MqttPingRespHandler.class);

    private static MqttPingRespHandler ourInstance = new MqttPingRespHandler();

    public static MqttPingRespHandler getInstance() {
        return ourInstance;
    }

    private MqttPingRespHandler() {
    }

    public MqttMessage doMessage(ChannelHandlerContext ctx, MqttMessage msg) {
        logger.debug("MQTT PINGREQ " + ctx.channel().id().asLongText());

        // 更新最新连接时间
        ApplicationContext.updateChannelBySessionId(ctx.channel().id().asLongText());

        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PINGRESP, false, MqttQoS.AT_LEAST_ONCE, true, 0);
        MqttMessage message = new MqttMessage(fixedHeader);

        return message;
    }

}

package org.opensource.demo.iot.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 断开连接Handler
 *
 * Created by zchen@idelan.cn on 2017/9/20.
 */
public class MqttDisconnectHandler {

    private static final Logger logger = LoggerFactory.getLogger(MqttDisconnectHandler.class);

    private static MqttDisconnectHandler ourInstance = new MqttDisconnectHandler();

    public static MqttDisconnectHandler getInstance() {
        return ourInstance;
    }

    private MqttDisconnectHandler() {
    }

    public MqttMessage doMessage(ChannelHandlerContext ctx, MqttMessage msg) {

        return null;
    }

}

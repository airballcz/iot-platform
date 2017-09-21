package org.opensource.demo.iot.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 取消订阅Handler
 * <p>
 * Created by zchen@idelan.cn on 2017/9/20.
 */
public class MqttUnSubscribeHandler {

    private static final Logger logger = LoggerFactory.getLogger(MqttUnSubscribeHandler.class);

    private static MqttUnSubscribeHandler ourInstance = new MqttUnSubscribeHandler();

    public static MqttUnSubscribeHandler getInstance() {
        return ourInstance;
    }

    private MqttUnSubscribeHandler() {
    }

    public MqttMessage doMessage(Channel channel, MqttMessage msg) {

        return null;
    }

}

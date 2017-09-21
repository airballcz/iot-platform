package org.opensource.demo.iot.server.handler;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import org.opensource.demo.iot.server.core.ApplicationContext;
import org.opensource.demo.iot.server.msg.SubscribeContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 断开连接Handler
 * <p>
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

    public void doMessage(Channel channel) {
        logger.debug("MQTT DISCONNECT " + channel.id().asLongText());

        String sessionId = channel.id().asLongText();
        ApplicationContext.closeChannelBySessionId(sessionId);  // remove上下文环境channel
        SubscribeContainer.removeSessionId(sessionId);          // remove订阅消息容器sessionId
    }

}

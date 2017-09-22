package org.opensource.demo.iot.server.handler.mqtt;

import io.netty.channel.Channel;
import org.opensource.demo.iot.server.core.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 断开连接Handler
 * <p>
 * Created by zchen@idelan.cn on 2017/9/20.
 */
public class DisconnectHandler {

    private static final Logger logger = LoggerFactory.getLogger(DisconnectHandler.class);

    private static DisconnectHandler ourInstance = new DisconnectHandler();

    public static DisconnectHandler getInstance() {
        return ourInstance;
    }

    private DisconnectHandler() {
    }

    public void doMessage(Channel channel) {
        String channelId = channel.id().asLongText();
        logger.debug("MQTT DISCONNECT " + channelId);

        ApplicationContext.removeChannelFromContext(channelId);  // remove上下文环境channel
//        SubscribeContainer.removeClientId();          // remove订阅消息容器sessionId
    }

}

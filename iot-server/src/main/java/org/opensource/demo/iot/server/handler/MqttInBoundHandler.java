package org.opensource.demo.iot.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.MqttMessage;
import org.opensource.demo.iot.server.core.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 总控制中心
 *
 * Created by zchen@idelan.cn on 2017/9/6.
 */
public class MqttInBoundHandler extends SimpleChannelInboundHandler<MqttMessage> {

    private static final Logger logger = LoggerFactory.getLogger(MqttInBoundHandler.class);

    protected void channelRead0(ChannelHandlerContext ctx, MqttMessage msg) throws Exception {
        logger.debug(msg.toString());

        MqttMessage mqttMessage = null;
        switch (msg.fixedHeader().messageType()) {
            case CONNECT:       // 连接
                mqttMessage = MqttConnectHandler.getInstance().doMessage(ctx, msg);
                break;

            case PUBLISH:       // 发布
                mqttMessage = MqttPublishHandler.getInstance().doMessage(msg);
                break;

            case SUBSCRIBE:     // 订阅
                mqttMessage = MqttSubscribeHandler.getInstance().doMessage(ctx, msg);
                break;

            case UNSUBSCRIBE:   // 取消订阅
                mqttMessage = MqttUnSubscribeHandler.getInstance().doMessage(ctx, msg);
                break;

            case PINGREQ:       // PING-心跳
                mqttMessage = MqttPingReqHandler.getInstance().doMessage(ctx, msg);
                break;

            default:
                break;
        }

        ctx.channel().writeAndFlush(mqttMessage);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.debug("mqtt exception:" + cause.getMessage());
        ApplicationContext.removeChannelBySessionId(ctx.channel().id().asLongText());
        ctx.close();
    }

}

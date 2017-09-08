package org.opensource.demo.iot.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import io.netty.handler.codec.mqtt.MqttSubAckMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zchen@idelan.cn on 2017/9/6.
 */
public class MqttInBoundHandler extends SimpleChannelInboundHandler<MqttMessage> {

    private static final Logger logger = LoggerFactory.getLogger(MqttInBoundHandler.class);

    protected void channelRead0(ChannelHandlerContext ctx, MqttMessage msg) throws Exception {
        logger.debug(msg.toString());

        switch (msg.fixedHeader().messageType()) {
            case CONNECT:       // 连接
                MqttConnAckMessage connAckMessage = (MqttConnAckMessage) MqttConAckHandler.getInstance().doMessage(msg);
                ctx.channel().writeAndFlush(connAckMessage);
                break;

            case PUBLISH:       // 发布
                MqttPubAckMessage pubAckMessage = (MqttPubAckMessage) MqttPubAckHandler.getInstance().doMessage(msg);
                ctx.channel().writeAndFlush(pubAckMessage);
                break;

            case SUBSCRIBE:     // 订阅
                MqttSubAckMessage subAckMessage = (MqttSubAckMessage) MqttSubAckHandler.getInstance().doMessage(ctx, msg);
                ctx.channel().writeAndFlush(subAckMessage);
                break;

            case PINGREQ:       // PING反馈
                MqttMessage message = MqttPingRespHandler.getInstance().doMessage(msg);
                ctx.channel().writeAndFlush(message);
                break;

            default:
                break;
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

}

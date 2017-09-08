package org.opensource.demo.iot.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.*;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zchen@idelan.cn on 2017/9/6.
 */
public class MqttInBoundHandler extends SimpleChannelInboundHandler<MqttMessage> {

    private Map<String, String> topics = new HashMap<String, String>();

    protected void channelRead0(ChannelHandlerContext ctx, MqttMessage msg) throws Exception {
        System.out.println(">>>>>>>>>>>>>>>>>>>>" + msg.toString());

        MqttFixedHeader fixedHeader;
        switch (msg.fixedHeader().messageType()) {
            case CONNECT:
                MqttConnAckMessage connAckMessage = MqttConnectHandler.getInstance().doMessage(msg);
                ctx.channel().writeAndFlush(connAckMessage);
                break;

            case PUBLISH:
                System.out.println(">>>>>>>>>>>>>>>>PUBLISH");
                MqttPublishVariableHeader publishVariableHeader = (MqttPublishVariableHeader) msg.variableHeader();
                String topicName = publishVariableHeader.topicName();
                ByteBuf payload = (ByteBuf) msg.payload();
                topics.put(topicName, payload.toString(Charset.forName("UTF-8")));
                System.out.println("topicName:" + topicName + ",payload:" + payload.toString(Charset.forName("UTF-8")));

                fixedHeader = new MqttFixedHeader(MqttMessageType.PUBACK, false, MqttQoS.AT_LEAST_ONCE, true, 0);
                MqttPubAckMessage pubAckMessage = new MqttPubAckMessage(fixedHeader, MqttMessageIdVariableHeader.from(1));
                ctx.channel().writeAndFlush(pubAckMessage);
                break;
            case SUBSCRIBE:
                System.out.println(">>>>>>>>>>>>>>>>>>>SUBSCRIBE");
                MqttSubscribePayload subscribePayload= (MqttSubscribePayload)msg.payload();
                List<MqttTopicSubscription> topicSubscriptions = subscribePayload.topicSubscriptions();
                MqttTopicSubscription topicSubscription;
                for (int i=0;i<topicSubscriptions.size();i++) {
                    topicSubscription = topicSubscriptions.get(i);
                    topics.get(topicSubscription.topicName());


                }
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

package org.opensource.demo.iot.server.msg;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import org.opensource.demo.iot.server.core.ApplicationContext;
import org.opensource.demo.iot.server.core.Message;
import org.opensource.demo.iot.server.core.PackageIdGenerator;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 用于发送消息，根据不同的服务质量发送消息
 * <p>
 * Created by zchen@idelan.cn on 2017/9/19.
 */
public class ManagerHandler {

    private static final List<Message> SENDED_MESSAGE_LIST = new LinkedList<>();

    private static final ThreadPoolExecutor SEND_MESSAGE_POOL = new ThreadPoolExecutor(1, 1, -1, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

    /**
     * publish命令，发送消息
     *
     * @param topicName
     * @param payload
     * @param isDup
     * @param qoS
     * @param isRetain  作为匹配已存在订阅发送时-0，作为新订阅匹配发送时-1
     */
    public static void publishMessage(final String topicName, final ByteBuf payload, final boolean isDup, final MqttQoS qoS, final boolean isRetain) {
        // 查找符合规范的channel
        Iterator<SubscribeContainer.Message> messages = SubscribeContainer.get(topicName);
        SubscribeContainer.Message subMessage;
        if (messages != null) {
            while (messages.hasNext()) {
                subMessage = messages.next();
                Channel channel = ApplicationContext.getChannelFromContext(subMessage.getClientId());
                if (channel != null) {
                    sendMessage(topicName, payload, isDup, qoS, isRetain, channel);
                }
            }
        }
    }

    /**
     * subscribe命令，发送消息
     *
     * @param topicName
     * @param payload
     * @param isDup
     * @param qoS
     * @param isRetain
     * @param channel
     */
    public static void subscribeMessage(final String topicName, final ByteBuf payload, final boolean isDup, final MqttQoS qoS, final boolean isRetain, Channel channel) {
        sendMessage(topicName, payload, isDup, qoS, isRetain, channel);
        // 保存订阅相关信息
        SubscribeContainer.put(topicName, channel.id().asLongText(), qoS);
    }

    private static void sendMessage(final String topicName, final ByteBuf payload, final boolean isDup, final MqttQoS qoS, final boolean isRetain, Channel channel) {
        int packetId = PackageIdGenerator.generator();
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PUBLISH, isDup, qoS, isRetain, 0);
        MqttPublishVariableHeader publishVariableHeader = new MqttPublishVariableHeader(topicName, packetId);
        MqttPublishMessage publishMessage = new MqttPublishMessage(fixedHeader, publishVariableHeader, payload);

        SEND_MESSAGE_POOL.execute(new SendMessageWork(channel, publishMessage));
        // 对qos>0，加入消息队列
        if (qoS.value() > 0) {
            Message message = new Message();
            message.setId(packetId);
            message.setTopicName(topicName);
            message.setPayload(payload.toString(Charset.forName("UTF-8")));
            message.setQoS(qoS);
            SENDED_MESSAGE_LIST.add(message);
        }
    }

    /**
     * 从队列删除已发送并返回确认列表
     *
     * @param pckId
     */
    public static void removeSendedMessage(int pckId) {
        Message message = new Message();
        message.setId(pckId);
        SENDED_MESSAGE_LIST.remove(message);
    }

    private static class SendMessageWork implements Runnable {

        private Channel channel;

        private MqttPublishMessage message;

        public SendMessageWork(Channel channel, MqttPublishMessage message) {
            this.channel = channel;
            this.message = message;
        }

        @Override
        public void run() {
            channel.writeAndFlush(message);
        }
    }

}

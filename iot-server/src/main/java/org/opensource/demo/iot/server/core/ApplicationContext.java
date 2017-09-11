package org.opensource.demo.iot.server.core;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 存放上下文环境
 * <p>
 * Created by zchen@idelan.cn on 2017/9/8.
 */
public class ApplicationContext {

    private static final ConcurrentHashMap<String, Channel> CLIENTID_CHANNEL = new ConcurrentHashMap<String, Channel>();

    private static final ConcurrentHashMap<String, Channel> CONTEXT_CONCURRENT_HASH_MAP = new ConcurrentHashMap<String, Channel>();

    public static void setContext(String topic, Channel channel) {
        CONTEXT_CONCURRENT_HASH_MAP.put(topic, channel);
    }

    public static Channel getContext(String topic) {
        return CONTEXT_CONCURRENT_HASH_MAP.get(topic);
    }

    public static void setClientIdChannel(String clientId, Channel channel) {
        CLIENTID_CHANNEL.put(clientId, channel);
    }

}

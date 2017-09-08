package org.opensource.demo.iot.server.core;

import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 存放上下文环境
 *
 * Created by zchen@idelan.cn on 2017/9/8.
 */
public class ApplicationContext {

    private static final ConcurrentHashMap<String, ChannelHandlerContext> CONTEXT_CONCURRENT_HASH_MAP = new ConcurrentHashMap<String, ChannelHandlerContext>();

    public static void setContext(String topic, ChannelHandlerContext ctx) {
        CONTEXT_CONCURRENT_HASH_MAP.put(topic, ctx);
    }

    public static ChannelHandlerContext getContext(String topic) {
        return CONTEXT_CONCURRENT_HASH_MAP.get(topic);
    }

}

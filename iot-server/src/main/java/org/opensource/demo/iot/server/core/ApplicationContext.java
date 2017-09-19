package org.opensource.demo.iot.server.core;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 存放上下文环境
 * <p>
 * Created by zchen@idelan.cn on 2017/9/8.
 */
public class ApplicationContext {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationContext.class);

    // application context
    // include connect time
    private static final ConcurrentHashMap<String, Session> SESSIONID_CONTEXT_MAP = new ConcurrentHashMap<String, Session>();

    private static final ScheduledThreadPoolExecutor SESSIONID_CONTEXT_SCHEDULED_POOL = new ScheduledThreadPoolExecutor(1);


    private static final ConcurrentHashMap<String, Channel> CONTEXT_CONCURRENT_HASH_MAP = new ConcurrentHashMap<String, Channel>();

    public static void setContext(String topic, Channel channel) {
        CONTEXT_CONCURRENT_HASH_MAP.put(topic, channel);
    }

    public static Channel getContext(String topic) {
        return CONTEXT_CONCURRENT_HASH_MAP.get(topic);
    }

    /**
     * 设置上下文环境
     *
     * @param sessionId
     * @param session
     */
    public static void setChannelBySessionId(String sessionId, Session session) {
        // TODO: 2017/9/18 若clientId已存在如何处理？等问题逻辑实现，遵循isClean标志位
        SESSIONID_CONTEXT_MAP.put(sessionId, session);

        // 规则，在keepAliveTime*1.5时间内未收到消息，服务端断开连接
        // 加入定时任务，是否出现超时问题
        SESSIONID_CONTEXT_SCHEDULED_POOL.schedule(new CheckTimeoutWork(sessionId), session.getKeepAliveTime() * 1500, TimeUnit.MILLISECONDS);
    }

    public static void updateChannelBySessionId(String sessionId) {
        Session session = SESSIONID_CONTEXT_MAP.get(sessionId);
        session.setConnectTime(System.currentTimeMillis());

        // 规则，在keepAliveTime*1.5时间内未收到消息，服务端断开连接
        // 加入定时任务，是否出现超时问题
        SESSIONID_CONTEXT_SCHEDULED_POOL.schedule(new CheckTimeoutWork(sessionId), session.getKeepAliveTime() * 1500, TimeUnit.MILLISECONDS);
    }

    // 删除上下文环境中sessionId对应的channel
    public static void removeChannelBySessionId(String sessionId) {
        SESSIONID_CONTEXT_MAP.remove(sessionId);
    }

    // 定时检查超时任务并从上下文环境删除
    private static class CheckTimeoutWork implements Runnable {

        private String sessionId;

        public CheckTimeoutWork(String sessionId) {
            this.sessionId = sessionId;
        }

        private ThreadLocal<String> local = ThreadLocal.withInitial(() -> sessionId);

        @Override
        public void run() {
            String sessionId = local.get();
            Session threadSession = SESSIONID_CONTEXT_MAP.get(sessionId);
            long difTime = System.currentTimeMillis() - threadSession.getConnectTime() + 10;
            logger.debug("sessionId[{}] with difTime[{}]", sessionId, difTime);
            if (difTime > (threadSession.getKeepAliveTime() * 1500)) {
                logger.warn("close clientId[{}] channel", sessionId);
                // 发生超时，关闭连接以及相关清理工作
                threadSession.getChannel().close();
                SESSIONID_CONTEXT_MAP.remove(sessionId);
            }
        }
    }

}

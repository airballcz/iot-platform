package org.opensource.demo.iot.server.core;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
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

    // 应用上下文
    private static final ConcurrentHashMap<String, Session> CONTEXT_MAP = new ConcurrentHashMap<String, Session>();

    // 设置检查超时连接池
    private static final ScheduledThreadPoolExecutor CONTEXT_SCHEDULED_POOL = new ScheduledThreadPoolExecutor(1);

    /**
     * 设置上下文环境
     * 若已经存在，则替换相关参数并关闭原channel通道
     *
     * @param clientId
     * @param session
     * @return boolean
     */
    public static boolean setChannelToContext(String clientId, Session session) {
        boolean sessionPresent = false;
        session.setConnectTime(System.currentTimeMillis());
        Session oSession = CONTEXT_MAP.get(clientId);
        if (oSession != null) {
            oSession.getChannel().close();
            sessionPresent = true;
        }
        CONTEXT_MAP.put(clientId, session);

        // 加入定时任务，是否出现超时问题
        CONTEXT_SCHEDULED_POOL.schedule(new CheckTimeoutWork(session.getChannelId()), session.getKeepAliveTime() * 1500, TimeUnit.MILLISECONDS);
        return sessionPresent;
    }

    /**
     * 根据clientId返回对应channel
     *
     * @param clientId
     * @return
     */
    public static Channel getChannelFromContext(String clientId) {
        Session session = CONTEXT_MAP.get(clientId);
        if (session != null) {
            return session.getChannel();
        }

        return null;
    }

    /**
     * 根据channelId判断channel是否存在
     *
     * @param channelId
     * @return
     */
    public static boolean checkChannelIsExisted(String channelId) {
        Session session = getSessionByChannelId(channelId);
        return session == null ? false : true;
    }

    /**
     * 上下文环境删除channelId对应信息
     *
     * @param channelId
     */
    public static void removeChannelFromContext(String channelId) {
        Session session = getSessionByChannelId(channelId);
        if (session != null && session.getChannel() != null) {
            session.getChannel().close();
            CONTEXT_MAP.remove(session.getClientId());
        }
    }

    // 更新channel对应连接时间
    public static void updateChannelConTime(String channelId) {
        Session session = getSessionByChannelId(channelId);
        if (session != null) {
            // 规则，在keepAliveTime*1.5时间内未收到消息，服务端断开连接
            CONTEXT_SCHEDULED_POOL.schedule(new CheckTimeoutWork(channelId), session.getKeepAliveTime() * 1500, TimeUnit.MILLISECONDS);
        }
    }

    // 根据channelId找到上下文
    private static Session getSessionByChannelId(String channelId) {
        Session session = null;

        Iterator<Session> iterator = CONTEXT_MAP.values().iterator();
        while (iterator.hasNext()) {
            session = iterator.next();
            if (session.getChannelId().equals(channelId)) {
                session.setConnectTime(System.currentTimeMillis());
                break;
            }
        }

        return session;
    }

    // 定时检查超时任务并从上下文环境删除
    private static class CheckTimeoutWork implements Runnable {

        private String channelId;

        public CheckTimeoutWork(String channelId) {
            this.channelId = channelId;
        }

        private ThreadLocal<String> local = ThreadLocal.withInitial(() -> channelId);

        @Override
        public void run() {
            String channelId = local.get();
            Session threadSession = getSessionByChannelId(channelId);
            if (threadSession == null) {   // 未在上下文环境找到
                logger.debug("can't found channelId[" + channelId + "] in context");
                return;
            }

            long difTime = System.currentTimeMillis() - threadSession.getConnectTime() + 10;
            logger.debug("clientId[{}] with difTime[{}]", threadSession.getClientId(), difTime);
            if (difTime > (threadSession.getKeepAliveTime() * 1500)) {
                logger.warn("close clientId[{}] channel", threadSession.getClientId());
                // 发生超时，关闭连接以及相关清理工作
                threadSession.getChannel().close();
                CONTEXT_MAP.remove(threadSession.getClientId());
            }
        }
    }

}

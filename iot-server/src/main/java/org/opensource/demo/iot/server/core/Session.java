package org.opensource.demo.iot.server.core;

import io.netty.channel.Channel;

/**
 * Channel的Session状态信息
 *
 * Created by zchen@idelan.cn on 2017/9/11.
 */
public class Session {

    private String clientId;

    private int keepAliveTime;

    private Channel channel;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public int getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(int keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}

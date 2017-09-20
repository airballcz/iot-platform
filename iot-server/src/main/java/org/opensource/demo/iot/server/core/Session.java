package org.opensource.demo.iot.server.core;

import io.netty.channel.Channel;

/**
 * Channel的Session状态信息
 * <p>
 * Created by zchen@idelan.cn on 2017/9/11.
 */
public class Session {

    private String clientId;    // N:1 clientId与sessionId关系

    private int keepAliveTime;  // unit "second"

    private long connectTime;   // unit "millisecond", init connect time or any other operator time;

    private Channel channel;    // 1:1 channel与sessionId关系

    private String username;

    private byte[] password;

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

    public long getConnectTime() {
        return connectTime;
    }

    public void setConnectTime(long connectTime) {
        this.connectTime = connectTime;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "clientId=" + this.clientId + ",username=" + this.username + ",keepAliveTime=" + this.keepAliveTime;
    }
}

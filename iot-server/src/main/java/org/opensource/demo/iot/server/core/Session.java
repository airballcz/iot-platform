package org.opensource.demo.iot.server.core;

import io.netty.channel.Channel;

import java.util.Arrays;

/**
 * Channel对应的回话状态信息
 * <p>
 * Created by zchen@idelan.cn on 2017/9/11.
 */
public class Session {

    private String clientId;    // 客户端唯一标识

    private String channelId;   // 通道对应ID

    private int keepAliveTime;  // unit "second"

    private boolean isCleanSession;  // 会话状态保持位

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

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public int getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(int keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public boolean getIsCleanSession() {
        return isCleanSession;
    }

    public void setIsCleaned(boolean isCleanSession) {
        this.isCleanSession = isCleanSession;
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
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Session)) {
            return false;
        }

        return this.channelId.equals(((Session) obj).getChannelId());
    }

    @Override
    public int hashCode() {
        if (this.channelId == null) {
            return 0;
        }

        return this.channelId.hashCode();
    }

    @Override
    public String toString() {
        return "Session{" +
                "clientId='" + clientId + '\'' +
                ", channelId='" + channelId + '\'' +
                ", keepAliveTime=" + keepAliveTime +
                ", isCleanSession=" + isCleanSession +
                ", connectTime=" + connectTime +
                ", channel=" + channel +
                ", username='" + username + '\'' +
                ", password=" + Arrays.toString(password) +
                '}';
    }

}

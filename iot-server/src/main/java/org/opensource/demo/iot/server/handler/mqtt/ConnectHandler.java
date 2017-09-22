package org.opensource.demo.iot.server.handler.mqtt;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import org.opensource.demo.iot.server.auth.AuthStrategy;
import org.opensource.demo.iot.server.core.ApplicationContext;
import org.opensource.demo.iot.server.core.Session;
import org.opensource.demo.iot.server.exception.AuthenticationException;
import org.opensource.demo.iot.server.exception.MqttConnectionException;
import org.opensource.demo.iot.server.msg.SubscribeContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 连接响应Handler
 * <p>
 * Created by zchen@idelan.cn on 2017/9/7.
 */
public class ConnectHandler {

    private static final Logger logger = LoggerFactory.getLogger(ConnectHandler.class);

    private static ConnectHandler ourInstance = new ConnectHandler();

    public static ConnectHandler getInstance() {
        return ourInstance;
    }

    private ConnectHandler() {
    }

    public MqttMessage doMessage(Channel channel, MqttMessage msg) throws MqttConnectionException {
        String channelId = channel.id().asLongText();   // get "channelId"
        boolean sessionPresent = true;                  // 当前会话标志
        logger.debug("MQTT CONNECT " + channelId);

        // 重复发送连接命令，关闭已存在的channel
        if (ApplicationContext.checkChannelIsExisted(channelId)) {
            throw new MqttConnectionException("mqtt connect repeat");
        }

        // --可变报头--
        MqttConnectVariableHeader connectVariableHeader = (MqttConnectVariableHeader) msg.variableHeader();
        String protocolName = connectVariableHeader.name();
        if (!protocolName.equalsIgnoreCase("MQTT")) { // 非规范MQTT协议
            throw new MqttConnectionException("mqtt connection protocol[" + protocolName + "] not standard");   // 直接网络关闭连接
        }

        // 检查MQTT协议版本
        if (connectVariableHeader.version() != MqttVersion.MQTT_3_1_1.protocolLevel()) {    // MQTT协议版本不支持
            sessionPresent = false;
            return response(MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION, sessionPresent);
        }

        // --有效载荷--
        MqttConnectPayload connectPayload = (MqttConnectPayload) msg.payload();

        String clientId = connectPayload.clientIdentifier();
        // 非法clientId
        if (clientId == null || clientId.length() <= 0) {
            sessionPresent = false;
            return response(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED, sessionPresent);
        }

        // 登陆认证，用户名、密码
        if (!connectVariableHeader.hasUserName() || !connectVariableHeader.hasPassword()) {
            throw new AuthenticationException("mqtt connection need username and password to authentication");
        }
        String username = connectPayload.userName();
        byte[] password = connectPayload.passwordInBytes();
        if (!AuthStrategy.auth(username, password)) {    // 认证未通过，关闭网络连接
            sessionPresent = false;
            return response(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD, sessionPresent);
        }

        boolean isCleanSession = connectVariableHeader.isCleanSession();    // isClean标志

        // 判断clientId是否已经存在，若存在则关闭原对应channel并用当前替换
        Session session = new Session();
        session.setClientId(clientId);
        session.setChannelId(channelId);
        session.setChannel(channel);
        session.setUsername(username);
        session.setPassword(password);
        session.setIsCleaned(isCleanSession);
        session.setKeepAliveTime(connectVariableHeader.keepAliveTimeSeconds());
        sessionPresent = ApplicationContext.setChannelToContext(clientId, session);

        // TODO: 2017/9/8 关于clean session状态逻辑设置
        if (isCleanSession) {           // 1-删除clientId对应会话信息
            SubscribeContainer.removeClientId(clientId);
            sessionPresent = false;
        }

        return response(MqttConnectReturnCode.CONNECTION_ACCEPTED, sessionPresent);
    }

    // 设置响应报文
    private MqttConnAckMessage response(MqttConnectReturnCode connectReturnCode, boolean sessionPresent) {
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_LEAST_ONCE, true, 0);
        MqttConnAckVariableHeader connAckVariableHeader = new MqttConnAckVariableHeader(connectReturnCode, sessionPresent);
        MqttConnAckMessage connAckMessage = new MqttConnAckMessage(fixedHeader, connAckVariableHeader);

        return connAckMessage;
    }

}

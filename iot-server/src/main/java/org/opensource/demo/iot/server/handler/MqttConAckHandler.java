package org.opensource.demo.iot.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;
import org.opensource.demo.iot.server.Application;
import org.opensource.demo.iot.server.auth.AuthStrategy;
import org.opensource.demo.iot.server.core.ApplicationContext;
import org.opensource.demo.iot.server.core.Session;
import org.opensource.demo.iot.server.exception.AuthenticationException;
import org.opensource.demo.iot.server.exception.MqttConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 连接响应Handler
 * <p>
 * Created by zchen@idelan.cn on 2017/9/7.
 */
public class MqttConAckHandler {

    private static final Logger logger = LoggerFactory.getLogger(MqttConAckHandler.class);

    private static MqttConAckHandler ourInstance = new MqttConAckHandler();

    public static MqttConAckHandler getInstance() {
        return ourInstance;
    }

    private MqttConAckHandler() {
    }

    public MqttMessage doMessage(ChannelHandlerContext ctx, MqttMessage msg) throws MqttConnectionException {
        logger.debug("MQTT CONNECT " +  ctx.channel().id().asLongText());

        MqttConnectReturnCode connectReturnCode = MqttConnectReturnCode.CONNECTION_ACCEPTED;

        MqttConnectVariableHeader connectVariableHeader = (MqttConnectVariableHeader) msg.variableHeader();
        String protocolName = connectVariableHeader.name();
        if (!protocolName.equalsIgnoreCase("MQTT")) { // 非规范MQTT协议
            throw new MqttConnectionException("mqtt connection protocol not standard");   // 直接网络关闭连接
        }

        if (connectVariableHeader.version() != MqttVersion.MQTT_3_1_1.protocolLevel()) {    // MQTT版本不支持
            connectReturnCode = MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION;
        }

        MqttConnectPayload connectPayload = (MqttConnectPayload) msg.payload();

        // 用户名和密码认证
        if (!connectVariableHeader.hasUserName() || !connectVariableHeader.hasPassword()) {
            throw new AuthenticationException("mqtt connection need username and password to authentication");
        }
        String username = connectPayload.userName();
        byte[] password = connectPayload.passwordInBytes();
        if (!AuthStrategy.auth(username, password)) {    // 认证未通过，关闭网络连接
            connectReturnCode = MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD;
        }

        String clientId = connectPayload.clientIdentifier();

        // TODO: 2017/9/8 关于clean session状态逻辑设置
        boolean isCleanSession = connectVariableHeader.isCleanSession();
        boolean sessionPresent = true;
        if (isCleanSession) {           // 删除clientId对应的相关信息

            sessionPresent = false;
        }

        // 当前channel保存至上下文环境
        Session session = new Session();
        session.setClientId(clientId);
        session.setChannel(ctx.channel());
        session.setUsername(username);
        session.setPassword(password);
        session.setKeepAliveTime(connectVariableHeader.keepAliveTimeSeconds());
        session.setConnectTime(System.currentTimeMillis());
        ApplicationContext.setChannelBySessionId(ctx.channel().id().asLongText(), session);

        // 设置响应报文
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_LEAST_ONCE, true, 0);
        MqttConnAckVariableHeader connAckVariableHeader = new MqttConnAckVariableHeader(connectReturnCode, sessionPresent);
        MqttConnAckMessage connAckMessage = new MqttConnAckMessage(fixedHeader, connAckVariableHeader);

        return connAckMessage;
    }

}

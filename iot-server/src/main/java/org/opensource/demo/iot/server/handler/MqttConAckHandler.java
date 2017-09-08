package org.opensource.demo.iot.server.handler;

import io.netty.handler.codec.mqtt.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 连接响应Handler
 *
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

    public MqttMessage doMessage(MqttMessage msg) {
        logger.debug("MQTT CONNECT");
        MqttConnectReturnCode connectReturnCode = MqttConnectReturnCode.CONNECTION_ACCEPTED;

        MqttConnectVariableHeader connectVariableHeader = (MqttConnectVariableHeader) msg.variableHeader();
        String protocolName = connectVariableHeader.name();
        if (!protocolName.equalsIgnoreCase("MQTT")) { // 非规范MQTT协议
            // 直接网络关闭连接
        }

        if (connectVariableHeader.version() != MqttVersion.MQTT_3_1_1.protocolLevel()) {    // MQTT版本不支持
            connectReturnCode = MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION;
            // 视具体情况，是否关闭网络连接
        }

        MqttConnectPayload connectPayload = (MqttConnectPayload) msg.payload();

        // TODO: 2017/9/8 用户名和密码认证
        if(connectVariableHeader.hasUserName() && connectVariableHeader.hasPassword()) {
            String usernam = connectPayload.userName();
            byte[] password = connectPayload.passwordInBytes();

            // 认证未通过，关闭网络连接
        }

        // TODO: 2017/9/8  规则，在keepAliveTime*1.5时间内未收到消息，服务端断开连接
        String clientId = connectPayload.clientIdentifier();
        int keepAliveTime = connectVariableHeader.keepAliveTimeSeconds();

        // TODO: 2017/9/8 关于clean session状态逻辑设置
        boolean isCleanSession = connectVariableHeader.isCleanSession();
        boolean sessionPresent = true;
        if(isCleanSession) {
            sessionPresent = false;
        }

        // 设置响应报文
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_LEAST_ONCE, true, 0);
        MqttConnAckVariableHeader connAckVariableHeader = new MqttConnAckVariableHeader(connectReturnCode, sessionPresent);
        MqttConnAckMessage connAckMessage = new MqttConnAckMessage(fixedHeader, connAckVariableHeader);

        return connAckMessage;
    }

}

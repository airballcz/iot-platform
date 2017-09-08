package org.opensource.demo.iot.server.handler;

import io.netty.handler.codec.mqtt.*;

/**
 * Created by zchen@idelan.cn on 2017/9/7.
 */
public class MqttConnectHandler {

    private static MqttConnectHandler ourInstance = new MqttConnectHandler();

    public static MqttConnectHandler getInstance() {
        return ourInstance;
    }

    private MqttConnectHandler() {
    }

    public MqttConnAckMessage doMessage(MqttMessage msg) {
        System.out.println(">>>>>>>>>>>>>>CONNECT");
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_LEAST_ONCE, true, 0);
        MqttConnAckVariableHeader connAckVariableHeader = new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_ACCEPTED, true);
        MqttConnAckMessage connAckMessage = new MqttConnAckMessage(fixedHeader, connAckVariableHeader);

        return connAckMessage;
    }

}

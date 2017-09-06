package org.opensource.demo.iot.test.core;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Created by zchen@idelan.cn on 2017/5/11.
 */
public class MyMqttFactory implements MqttCallbackExtended {

    private static final Logger log = LoggerFactory.getLogger(MyMqttFactory.class);

    private MqttAsyncClient client;

    private MqttConnectOptions options;

    public MyMqttFactory() {

    }

    /**
     * 带账户密码登陆验证
     *
     * @param url
     * @param username
     * @param password
     * @throws MqttException
     */
    public void start(String url, String username, String password) throws MqttException {
        String uuid = UUID.randomUUID().toString();
        client = new MqttAsyncClient(url, uuid, new MemoryPersistence());
        client.setCallback(this);
        options = new MqttConnectOptions();
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setAutomaticReconnect(Boolean.TRUE);
        client.connect(options, this, new MqttActionListener());
        log.debug("[{}], 连接MQTT服务 [{}] 成功, 用户名 [{}], 密码 [{}].", uuid, url, username, password);
    }

    /**
     * 无账户密码登陆
     *
     * @param url
     * @throws MqttException
     */
    public void start(String url) throws MqttException {
        String uuid = UUID.randomUUID().toString();
        client = new MqttAsyncClient(url, uuid, new MemoryPersistence());
        client.setCallback(this);
        client.connect();
        log.debug("[{}], 连接MQTT服务 [{}] 成功.", uuid, url);
    }

    public void reConnect() {
        try {
            client.reconnect();
        } catch (Exception var2) {
            log.error("MQTT服务重连失败:{}", var2.getMessage());
        }
    }

    public boolean isConnected() {
        return client.isConnected();
    }

    public void disconnect() {
        try {
            client.disconnect();
        } catch (MqttException var2) {
            log.warn("MQTT服务连接丢失:{}", var2.getMessage());
        }

    }

    public void publish(String topicName, String payload) {
        try {
            MqttMessage message = new MqttMessage(payload.getBytes());
            client.publish(topicName, message);
            log.debug("Mqtt发布主题 [{}] 内容 [{}] 成功", topicName, payload);
        } catch (Exception e) {
            log.warn("Mqtt发布主题 [{}] 内容 [{}] 失败:{}", topicName, payload, e.getMessage());
        }

    }

    public void subscribe(String topicName) {
        try {
            IMqttToken token = client.subscribe(topicName, 1, this, new MqttActionListener());
            token.waitForCompletion();
            log.debug("Mqtt订阅主题 [{}] 成功", topicName);
        } catch (MqttException e) {
            log.warn("Mqtt订阅主题 [{}] 失败:{}", topicName, e.getMessage());
        }
    }

    public void connectComplete(boolean reconnect, String serverURI) {
        log.debug("Mqtt服务[{}]连接成功:{}", serverURI, reconnect);
    }

    public void connectionLost(Throwable cause) {
        log.warn("Mqtt服务连接丢失:{}", cause.getMessage());
    }

    public void messageArrived(String topic, MqttMessage message) throws Exception {
        log.debug("Mqtt主题[{}]，消息内容[{}]", topic, message);
    }

    public void deliveryComplete(IMqttDeliveryToken token) {
        log.debug("Mqtt消息分发完成");
    }
}

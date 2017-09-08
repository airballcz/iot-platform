package org.opensource.demo.iot.test;

import org.junit.Before;
import org.junit.Test;
import org.opensource.demo.iot.test.core.MyMqttFactory;

/**
 * 基于org.eclipse.paho.client.mqttv3客户端发布、订阅消息
 * <p>
 * Created by zchen@idelan.cn on 2017/9/6.
 */
public class MqttClient {

    private MyMqttFactory client;

    @Before
    public void initTest() {
        client = new MyMqttFactory();
        try {
            client.start("tcp://127.0.0.1:1883", "username", "password");
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void publishTest() {
        client.publish("/topic/test", "hello,world");
    }

    @Test
    public void subscribeTest() {
        client.subscribe("/topic/test");
    }

}

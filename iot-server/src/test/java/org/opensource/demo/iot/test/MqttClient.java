package org.opensource.demo.iot.test;

import org.junit.Test;
import org.opensource.demo.iot.test.core.MyMqttFactory;

/**
 * 基于org.eclipse.paho.client.mqttv3客户端发布、订阅消息
 * <p>
 * Created by zchen@idelan.cn on 2017/9/6.
 */
public class MqttClient {

    @Test
    public void initTest() {
        MyMqttFactory factory = new MyMqttFactory();
        try {
            factory.start("tcp://127.0.0.1:1883", "username", "password");

            Thread.sleep(Integer.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void publishTest() {
        MyMqttFactory factory = new MyMqttFactory();
        try {
            factory.start("tcp://127.0.0.1:1883");

            Thread.sleep(20000);
            factory.publish("/topic/test", "hello,world");

            Thread.sleep(Integer.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

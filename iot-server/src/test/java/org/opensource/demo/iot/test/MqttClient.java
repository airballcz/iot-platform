package org.opensource.demo.iot.test;

import org.junit.Test;
import org.omg.IOP.ExceptionDetailMessage;
import org.opensource.demo.iot.test.core.MyMqttFactory;

/**
 * 基于org.eclipse.paho.client.mqttv3客户端发布、订阅消息
 * <p>
 * Created by zchen@idelan.cn on 2017/9/6.
 */
public class MqttClient {

    private MyMqttFactory client;

    //    @Before
    public void initTest() {
        client = new MyMqttFactory();
        try {
            client.start("tcp://127.0.0.1:1883", "username", "password", 60, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void publishTest() {
        client.publish("/topic/test", "hello,world");

        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {

        }
    }

    @Test
    public void subscribeTest() {
        client.subscribe("/topic/test");

        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {

        }
    }

    @Test
    public void increaseTest() throws Exception {
        for (int i = 0; i < 50000; i++) {
            Thread thread = new Thread(() -> {
                try {
                    MyMqttFactory client = new MyMqttFactory();
                    client.start("tcp://127.0.0.1:1883", "username", "password", 60, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, "thread-" + i);
            thread.start();

            Thread.sleep(10);
        }

        Thread.sleep(Integer.MAX_VALUE);
    }

}

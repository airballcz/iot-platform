package org.opensource.demo.iot.server.core;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 报文标识符生成器
 * 按默认规律，生成无重复的编号，范围 >0 and <1<<32
 *
 * Created by zchen@idelan.cn on 2017/9/22.
 */
public class PackageIdGenerator {

    private static final AtomicInteger PACKETIDS = new AtomicInteger();

    // 默认自增1
    public static int generator() {
        return PACKETIDS.incrementAndGet();
    }

}

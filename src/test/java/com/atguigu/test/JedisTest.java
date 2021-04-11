package com.atguigu.test;

import org.junit.Test;
import redis.clients.jedis.Jedis;

/**
 * @author darren
 * @create 2021-03-26 20:29
 */
public class JedisTest {


    @Test
    public void test01(){
        Jedis jedis = new Jedis("192.168.6.4",6379,20000);
        String ping = jedis.ping();
        System.out.println("ping:"+ping);
    }
}

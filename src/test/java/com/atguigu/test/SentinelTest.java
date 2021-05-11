package com.atguigu.test;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

import java.util.HashSet;
import java.util.Set;

/**
 * @author darren
 * @create 2021-05-11 23:11
 */
public class SentinelTest {

    private static final String MASTER_NAME = "mymaster";


    @Test
    public void sentinelConnect(){
        Set<String> sentinels = new HashSet<>();
        sentinels.add("192.168.6.4:26379");
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        JedisSentinelPool jedisSentinelPool = new JedisSentinelPool(MASTER_NAME,sentinels,poolConfig,60000);

        Jedis jedis = jedisSentinelPool.getResource();

        jedis.set("wangwang","miaomiao");

        String value = jedis.get("haha");
        System.out.println("value:"+value);


    }
}

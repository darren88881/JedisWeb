package com.atguigu.test;

import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.Random;

public class InitDataTest {
	
	// http://192.168.0.173:8080/MySeckill/index.jsp
	// http://192.168.2.153:8080/MySeckill/index.jsp
	

	// 初始化库存的方法
	@Test
	public void test() {
		
		Jedis jedis =new Jedis("192.168.6.26",6379);
		
		System.out.println(jedis.ping());
		
		String productKey="sk:"+1001+":product";
		String userKey="sk:"+1001+":user";
		
		jedis.set(productKey, "500");
		
		jedis.del(userKey);
		
		String string = jedis.get(productKey);
		
		System.out.println(string);
		
		jedis.close();
	}
	
	@Test
	public void test1() {
		
		Jedis jedis =new Jedis("192.168.6.4",6379);
		
		System.out.println(jedis.ping());
		
		String qtkey="sk:"+1001+":product";
		String usersKey="sk:"+1001+":user";
		
		jedis.set(qtkey, "10");
		
		jedis.del(usersKey);
		
		String string = jedis.get(qtkey);
		
		
		System.out.println(string);
		
		jedis.close();
	}

	@Test
	public void test3(){
		String code = "";
		for(int i =0;i<6;i++){
			code+= new Random().nextInt(10);
		}
		System.out.println("code:"+code);
	}

}

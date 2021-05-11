package com.atguigu.utils;

import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.io.IOException;
import java.util.List;


/*
 * 每个用户最多可以秒杀成功一个产品！
 * 
 * 1. 明确秒杀逻辑涉及的数据及类型，相应的api
 * 		  产品库存：  
 * 				key :  productId
 *              value : 库存，string
 *       秒杀成功的用户信息：
 *       		key :   productId
 *              value :  用户名单，set
 *              
 * 2. 业务流程
 * 		①判断用户之前是否已经秒杀成功！  ismember()
 * 				已经成功，返回false
 * 				否则，进入秒杀流程
 * 
 *      ②检查库存合法性 ！  get()
 *      		a) null  ， 商品还没用被上架，商家还未初始化商品,false
 *              b) <=0 , 没有库存，false
 *              c) >0 , 继续秒杀
 *              
 *      ③秒杀流程
 *      		库存减一，  decr();
 *              加入用户到秒杀成功名单,sadd()  
 * 		
 * 				
   3. 使用压测工具模拟高并发的秒杀场景
   			ab  -n 3000  -c 1000 -p  /root/postarg  -T 'application/x-www-form-urlencoded' http://192.168.2.153:8080/MySeckill/doseckill
 *
 * 
 * 4. 遇到超卖问题，加锁解决
 * 			在java代码中使用同步代码块解决，使用悲观锁，缺点是不公平！
 *          还可以使用redis的乐观锁解决，乐观锁的缺点是造成资源浪费(多写场景)！存在不公平！
 *          
 * 5. 解决不公平
 * 			先发送的请求，先处理！
 * 			使用Lua脚本解决！  Lua脚本可以调用c/c++编写的函数，可以调用redis的api!
 * 			Lua脚本类似redis中的事务，有原子性，不会被其他操作打断！
 * 
 * 6.  redis 事务                                                      Lua脚本
 * 		 (multi)                         编写脚本script={set k1 v1;set k2 v2}
 *     (set k1 v1)                       将当前脚本script提交给redis执行！
 *      (set k2 v2)                       Lua脚本，一个脚本相当于一条命令，是一个原子！
 *       (exec)                           Lua脚本中，以脚本为单位，每个脚本中可以编写多条语句！
 *       redis事务中每条命令都是一个原子！
 *       事务中，以每条命令为单位！
 * 
 */

public class SecKill_redis {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SecKill_redis.class);

	// 秒杀的实际方法
	public static boolean doSecKill(String uid, String prodid) throws IOException {

		//产品的Key
		String productKey = "sk:"+prodid+":product";
		//用户的key
		String userKey = "sk:"+prodid+":user";

		JedisPool jedisPoolInstance = JedisPoolUtil.getJedisPoolInstance();
		Jedis jedis = jedisPoolInstance.getResource();

		// 判断用户是不是在已秒杀的set列表中
		if (jedis.sismember(userKey,uid)) {
			System.out.println(uid+"已经秒杀过");
			jedis.close();
			return false;
		}

		//为库存加锁
		jedis.watch(productKey);
		String productNum = jedis.get(productKey);

		if (productNum == null) {
			System.out.println("商品："+prodid+"没有上架！");
			jedis.close();
			return false;
		}

		int parseInt = Integer.parseInt(productNum);
		if (parseInt<=0) {
			System.out.println("商品："+prodid+"已售罄！");
			jedis.close();
			return false;
		}

		//开启事务
		Transaction multi = jedis.multi();
		// 记录秒杀过的用户
		multi.sadd(userKey, uid);

		// 商品数减一
		multi.decr(productKey);
		List<Object> exec = multi.exec();
		if(exec == null || exec.size() <2){
			System.out.println(uid+"秒杀失败");
			jedis.close();
			return false;
		}

		System.out.println("商品："+prodid+"已被用户："+uid+"秒到，还剩"+jedis.get(productKey)+"个商品");

		jedis.close();
		return true;

	}
}

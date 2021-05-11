package com.atguigu.utils;

import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;



public class SecKill_redisByScript {

	private static final  org.slf4j.Logger logger =LoggerFactory.getLogger(SecKill_redisByScript.class) ;

	static String secKillScript =
			"local userid=KEYS[1];\r\n" +
					"local prodid=KEYS[2];\r\n" +
					"local productKey='sk:'..prodid..\":product\";\r\n" +
					"local usersKey='sk:'..prodid..\":user\";\r\n" +
					"local userExists=redis.call(\"sismember\",usersKey,userid);\r\n" +
					"if tonumber(userExists)==1 then \r\n" +
					"   return 2;\r\n" +
					"end\r\n" +
					"local num= redis.call(\"get\" ,productKey);\r\n" +
					"if tonumber(num)<=0 then \r\n" +
					"   return 0;\r\n" +
					"else \r\n" +
					"   redis.call(\"decr\",productKey);\r\n" +
					"   redis.call(\"sadd\",usersKey,userid);\r\n" +
					"end\r\n" +
					"return 1" ;




	public static boolean doSecKill(String uid,String prodid) throws IOException {

		JedisPool jedispool =  JedisPoolUtil.getJedisPoolInstance();

		Jedis jedis=jedispool.getResource();

		//String sha1=  .secKillScript;

		//将脚本加入到脚本缓存中，但并不立即执行
		String sha1=  jedis.scriptLoad(secKillScript);

		//执行脚本
		Object result= jedis.evalsha(sha1, 2, uid,prodid);


		String reString=String.valueOf(result);
		if ("0".equals( reString )  ) {
			System.err.println("已抢空！！");
			jedis.close();
			return false;
		}



		if("2".equals( reString )  )  {
			System.err.println("该用户已抢过！！");
			jedis.close();
			return false;
		}

		if("1".equals( reString )  )  {
			System.out.println("抢购成功！！！！");
			jedis.close();
			return true;
		} else{
			System.err.println("抢购异常！！");
			jedis.close();
			return false;
		}
	}
}

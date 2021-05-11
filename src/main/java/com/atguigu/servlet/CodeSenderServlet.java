package com.atguigu.servlet;

import com.atguigu.utils.VerifyCodeConfig;
import redis.clients.jedis.Jedis;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Random;

public class CodeSenderServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1:获取手机号
        String phone_no = request.getParameter("phone_no");

        if (phone_no == null || phone_no == "") {
            response.getWriter().print(false);
            return;
        }
        // 2:生成key
        String key = VerifyCodeConfig.PHONE_PREFIX+phone_no+VerifyCodeConfig.PHONE_SUFFIX;
        // 3:生成value
        String value =getCode(6);
        // 4:存储到redis
        Jedis jedis = new Jedis(VerifyCodeConfig.HOST, VerifyCodeConfig.PORT, 20000);

        // 手机号发送短信不能超过3次
        // 生成次数key
        String count_key  = phone_no+VerifyCodeConfig.COUNT_SUFFIX;
        String count_value = jedis.get(count_key);
        if ( count_value != null) {
            int anInt = Integer.parseInt(count_value);
            if (anInt >=3) {
                response.getWriter().print("limit");
                return;
            } else {
                jedis.incr(count_key);
            }

        } else {
            jedis.setex(count_key,VerifyCodeConfig.SECONDS_PER_DAY,"1");
        }

        jedis.setex(key,VerifyCodeConfig.CODE_TIMEOUT,value);

        // 模拟发送短信
        System.out.println("key:"+key+";value:"+value);

        // 返回true
        response.getWriter().print(true);
    }

    /**
     * 生成验证码
     *
     * @param num 位数
     * @return String
     */
    public static String getCode(int num){

        String code = "";
        for(int i =0;i<num;i++){
            code+= new Random().nextInt(10);
        }
        return code;
    }

}

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

        // 获取手机号
        String phone_num = request.getParameter("phone_no");
        // 验证参数是否合法
        if (phone_num == null || phone_num.equals("")) {
            response.getWriter().print(false);
            return;
        }

        Jedis jedis = new Jedis(VerifyCodeConfig.HOST, VerifyCodeConfig.PORT, 20000);
        // 判断用户是否超过次数在生成验证码之前
        // 生成计数的key
        String count_key = phone_num + VerifyCodeConfig.COUNT_SUFFIX;
        String count_value = jedis.get(count_key);

        if (count_value != null) {
            int count_int = Integer.parseInt(count_value);
            //最多三次机会
            if (count_int >= 3) {
                jedis.close();
                response.getWriter().print("limit");
                return;
            } else {
                jedis.incr(count_key);
            }
        } else {
            //设置有效期为1天
            jedis.setex(count_key, VerifyCodeConfig.SECONDS_PER_DAY, "1");
        }

        // 生成验证码
        String code = genCode(6);
        // 生成key
        String key = VerifyCodeConfig.PHONE_PREFIX + phone_num + VerifyCodeConfig.PHONE_SUFFIX;
        // 存储验证码
        jedis.setex(key, VerifyCodeConfig.CODE_TIMEOUT, code);
        jedis.close();

        // 模拟发送短信
        System.out.println("尊敬的" + phone_num + "用户，您的验证码是：" + code + ",2分钟内有效，请不要告诉任何人！");
        System.out.println(key + ":" + code);
        response.getWriter().print(true);
    }


    //生成n位数字验证码
    private String genCode(int len) {
        String code = "";
        for (int i = 0; i < len; i++) {
            int rand = new Random().nextInt(10);
            code += rand;
        }
        return code;
    }


}

package com.atguigu.servlet;

import com.atguigu.utils.VerifyCodeConfig;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//验证手机验证码
public class CodeVerifyServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public CodeVerifyServlet() {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // 获取手机号
        String phone_num = request.getParameter("phone_no");
        String sender_code = request.getParameter("verify_code");

        // 验证参数是否合法
        if (phone_num == null || phone_num.equals("") || sender_code == null || sender_code.equals("")) {
            response.getWriter().print(false);
            return;
        }

        // 生成key
        String key = VerifyCodeConfig.PHONE_PREFIX + phone_num + VerifyCodeConfig.PHONE_SUFFIX;
        Jedis jedis = new Jedis(VerifyCodeConfig.HOST, VerifyCodeConfig.PORT, 20000);
        String query_code = jedis.get(key);
        jedis.close();

        if (sender_code.equals(query_code)) {
            response.getWriter().print(true);
        } else {
            response.getWriter().print(false);
        }
    }
}

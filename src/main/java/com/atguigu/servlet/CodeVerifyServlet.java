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

        String phone_no = request.getParameter("phone_no");
        String verify_code = request.getParameter("verify_code");
        if (phone_no == null || verify_code == null || phone_no == "" || verify_code == "") {
            response.getWriter().print(false);
            return;
        }
        Jedis jedis = new Jedis(VerifyCodeConfig.HOST, VerifyCodeConfig.PORT, 20000);

        String key = VerifyCodeConfig.PHONE_PREFIX+phone_no+VerifyCodeConfig.PHONE_SUFFIX;
        String value = jedis.get(key);

        if (verify_code.equals(value)) {
            response.getWriter().print(true);
        } else {
            response.getWriter().print(false);
        }

    }
}

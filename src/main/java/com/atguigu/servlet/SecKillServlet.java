package com.atguigu.servlet;

import com.atguigu.utils.SecKill_redisByScript;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Random;



public class SecKillServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	

    public SecKillServlet() {
    }

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//模拟生成一个用户id

		String userid = new Random().nextInt(50000) +"" ;

		String prodid =request.getParameter("prodid");

		//通过秒杀方法判断是否该用户秒杀成功
		//boolean if_success = SecKill_redis.doSecKill(userid, prodid);

		//通过lua脚本进行秒杀
		boolean if_success = SecKill_redisByScript.doSecKill(userid, prodid);
		response.getWriter().print(if_success);
	}
	

}

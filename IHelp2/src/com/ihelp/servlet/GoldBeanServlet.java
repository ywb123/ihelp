package com.ihelp.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONStringer;

import com.ihelp.dao.GoldBeanDao;
import com.ihelp.dao.UserDao;
import com.ihelp.domain.GoldBean;
import com.ihelp.domain.User;

public class GoldBeanServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public GoldBeanServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		this.doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		GoldBeanDao gbd=new GoldBeanDao();
		UserDao ud=new UserDao();
		String action=request.getParameter("action");
		System.out.println(action);
		if(action.equals("queryByNumber")){
			int number=Integer.parseInt(request.getParameter("number"));
			GoldBean gb=gbd.queryByNumber(number);
			//客户端查询对象信息，返回json数据格式
			//将List<GoldBean>组织成JSON字符串   
	        JSONStringer stringer = new JSONStringer();  
	        try{  
	            stringer.array();   
	            stringer.object();
	            stringer.key("number").value(gb.getNumber());
	            stringer.key("topic").value(gb.getTopic());
	           
	            stringer.endObject();
	            stringer.endArray();  
	        }  
	        catch(Exception e){}  
	        response.getOutputStream().write(stringer.toString().getBytes("UTF-8"));  
	        response.setContentType("text/json; charset=UTF-8");  //JSON的类型为text/json
			
		}
		/**********送金豆
		 * action为give
		 * number****送出的金豆数
		 * username**金豆送出者
		 * giveUsername****金豆获得者
		 * ****/
		else if(action.equals("give")){
			String result="";
			System.out.println("11111");
			int number=Integer.parseInt(request.getParameter("number"));//送出金豆数
			String username=request.getParameter("username");//金豆送出者
			String giveUsername=request.getParameter("giveUsername");//金豆获得者
			User user=ud.queryByUsername(username);
			
			User giveUser=ud.queryByUsername(giveUsername);
			System.out.println("11111");
			int userGoldBean=user.getGoldBeanNumber();
			int giveUserGoldBean=giveUser.getGoldBeanNumber();
			boolean b1=ud.updateNumberByUser(username, userGoldBean-number);
			boolean b2=ud.updateNumberByUser(giveUsername, giveUserGoldBean+number);
			if(b1&&b2){
				System.out.println("11111");
				result="SUCCESS";
			}else{
				System.out.println("22222");
				result="FAILURE";
			}
			PrintWriter out = response.getWriter();
			out.print(result);
			System.out.println(result);
		}
		
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}

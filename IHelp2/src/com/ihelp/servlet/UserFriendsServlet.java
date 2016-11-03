package com.ihelp.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ihelp.dao.UserFriendsDao;
import com.ihelp.domain.UserFriends;

public class UserFriendsServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public UserFriendsServlet() {
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

		UserFriendsDao ufd=new UserFriendsDao();
		String action=request.getParameter("action");
		if(action.equals("add")){
			//加好友
			String result="0";
			int userId=Integer.parseInt(request.getParameter("userId"));
			int friendId=Integer.parseInt(request.getParameter("friendId"));
			if(ufd.add(userId, friendId)){
				result="1";
			}
			System.out.println(result);
			PrintWriter out=response.getWriter();
			out.print(result);
		}
		else if(action.equals("calculate")){
			int userId1=Integer.parseInt(request.getParameter("userId1"));
			int userId2=Integer.parseInt(request.getParameter("userId2"));
			int count=0;
			count=ufd.calculate(userId1, userId2);
			PrintWriter out=response.getWriter();
			out.print(count+"");
		}
		else if(action.equals("queryByCommonFriendId")){
			int userId=Integer.parseInt(request.getParameter("userId"));
			ArrayList<UserFriends> ufs=new ArrayList<UserFriends>();
			ufs=ufd.queryByCommonFriendId(userId);
			JSONArray array=new JSONArray();
			for(int i=0;i<ufs.size();i++){
				try {
					JSONObject json=new JSONObject();
					json.put("id", ufs.get(i).getId());
					json.put("friendId", ufs.get(i).getFriendId());
					json.put("userId", ufs.get(i).getUserId());
					array.put(json);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			response.setContentType("text/plain");  
            response.setCharacterEncoding("UTF-8");  
            PrintWriter out = response.getWriter();
            out.print(array.toString()+"aaaa");
            System.out.println(array.toString()+"aaaa");
            out.flush();  
            out.close();  
		}
		else if(action.equals("queryByUserId")){
			int userId=Integer.parseInt(request.getParameter("userId"));
			ArrayList<UserFriends> ufs=new ArrayList<UserFriends>();
			ufs=ufd.queryByUserId(userId);
			System.out.println("aaaa");
			/*用json数组来传递数据*/
			JSONArray array=new JSONArray();
			for(int i=0;i<ufs.size();i++){
				try {
					JSONObject json=new JSONObject();
					json.put("id", ufs.get(i).getId());
					json.put("friendId", ufs.get(i).getFriendId());
					json.put("userId", ufs.get(i).getUserId());
					array.put(json);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(array.toString()+"aaaa");
				System.out.println(ufs.get(i).getUserId());
			}
			response.setContentType("text/plain");  
            response.setCharacterEncoding("UTF-8");  
            PrintWriter out = response.getWriter();
            out.print(array.toString());
            out.flush();  
            out.close();  
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

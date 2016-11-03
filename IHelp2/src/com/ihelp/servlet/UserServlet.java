package com.ihelp.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.ihelp.dao.UserDao;
import com.ihelp.domain.User;

public class UserServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor of the object.
	 */
	public UserServlet() {
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

		UserDao userDao=new UserDao();
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		String action=request.getParameter("action");
		
		if(action.equals("update")){
			PrintWriter out = response.getWriter();
			int userId=Integer.parseInt(request.getParameter("id"));
			String birthday=request.getParameter("birthday");
			String creditGrade=request.getParameter("creditGrade");
			String dormitoryNumber=request.getParameter("dormitoryNumber");
			String email=request.getParameter("email");
			int goldBeanNumber=Integer.parseInt(request.getParameter("goldBeanNumber"));
			String iAutography=request.getParameter("iAutography");
			String majorClass=request.getParameter("majorClass");
			String password=request.getParameter("password");
			String photoUrl=request.getParameter("photoUrl");
			String qqNumber=request.getParameter("qqNumber");
			String sex=request.getParameter("sex");
			boolean b=userDao.update(userId, birthday, creditGrade, dormitoryNumber, email, goldBeanNumber, iAutography, majorClass, password, photoUrl, qqNumber, sex);
			if(b){
				out.print("1");
			}
			else{
				out.print("0");
			}
		}
		else if(action.equals("queryBylikeUsername")){
			String username = URLDecoder.decode(request.getParameter("username"),"utf-8");
			ArrayList<User> users=new ArrayList<User>();
			users=userDao.queryBylikeUsername(username);
			JSONArray array=new JSONArray();
			for(int i=0;i<users.size();i++){
				try {
					JSONObject json=new JSONObject();
					json.put("ID", users.get(i).getId());
					json.put("Birthday", users.get(i).getBirthday());
					json.put("CreditGrade", users.get(i).getCreditGrade());
					json.put("DormitoryNumber", users.get(i).getDormitoryNumber());
					json.put("GoldBeanNumber", users.get(i).getGoldBeanNumber());
					json.put("IAutography", users.get(i).getiAutography());
					json.put("MajorClass", users.get(i).getMajorClass());
					json.put("PhotoUrl", users.get(i).getPhotoUrl());
					json.put("QQNumber", users.get(i).getQqNumber());
					json.put("Sex", users.get(i).getSex());
					json.put("Password", users.get(i).getPassword());
					json.put("Email", users.get(i).getEmail());
					json.put("Username", users.get(i).getUsername());
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
		else if(action.equals("queryByMajorClass")){
			int id=Integer.parseInt(request.getParameter("id"));
			ArrayList<User> users=new ArrayList<User>();
			users=userDao.queryByMajorClass(id);
			JSONArray array=new JSONArray();
			for(int i=0;i<users.size();i++){
				try {
					JSONObject json=new JSONObject();
					json.put("ID", users.get(i).getId());
					json.put("Birthday", users.get(i).getBirthday());
					json.put("CreditGrade", users.get(i).getCreditGrade());
					json.put("DormitoryNumber", users.get(i).getDormitoryNumber());
					json.put("GoldBeanNumber", users.get(i).getGoldBeanNumber());
					json.put("IAutography", users.get(i).getiAutography());
					json.put("MajorClass", users.get(i).getMajorClass());
					json.put("PhotoUrl", users.get(i).getPhotoUrl());
					json.put("QQNumber", users.get(i).getQqNumber());
					json.put("Sex", users.get(i).getSex());
					json.put("Password", users.get(i).getPassword());
					json.put("Email", users.get(i).getEmail());
					json.put("Username", users.get(i).getUsername());
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
		else if(action.equals("queryById")){
			int id=Integer.parseInt(request.getParameter("id"));
			User user=userDao.queryById(id);
			//客户端查询对象信息，返回json数据格式
			//将List<User>组织成JSON字符串   
			
	        JSONStringer stringer = new JSONStringer();  
	        try{  
	        	
	            stringer.array();   
	            stringer.object();
	            stringer.key("Birthday").value(user.getBirthday());
	            stringer.key("ID").value(user.getId());
	            stringer.key("CreditGrade").value(user.getCreditGrade());
	            stringer.key("DormitoryNumber").value(user.getDormitoryNumber());
	            stringer.key("Email").value(user.getEmail());
	            stringer.key("GoldBeanNumber").value(user.getGoldBeanNumber());
	            stringer.key("IAutography").value(user.getiAutography());
	            stringer.key("MajorClass").value(user.getMajorClass());
	            stringer.key("Password").value(user.getPassword());
	            stringer.key("PhotoUrl").value(user.getPhotoUrl());
	            stringer.key("QQNumber").value(user.getQqNumber());
	            stringer.key("Sex").value(user.getSex());
	            stringer.key("Username").value(user.getUsername());
	            System.out.println("dddd");
	            stringer.endObject();
	            stringer.endArray();  
	        }  
	        catch(Exception e){}  
	        
	        response.getOutputStream().write(stringer.toString().getBytes("UTF-8"));  
	        response.setContentType("text/json; charset=UTF-8");  //JSON的类型为text/json
	        System.out.println(stringer.toString().getBytes("UTF-8"));
		}else if(action.equals("queryByUsername")){
			request.setCharacterEncoding("utf-8");
			
			String username = URLDecoder.decode(request.getParameter("username"),"utf-8");
			User user=userDao.queryByUsername(username);
			//客户端查询对象信息，返回json数据格式
			//将List<User>组织成JSON字符串   
			System.out.println(request.getParameter("username"));
	        JSONStringer stringer = new JSONStringer();  
	        try{  
	            stringer.array();   
	            stringer.object();
	            stringer.key("Birthday").value(user.getBirthday());
	            stringer.key("ID").value(user.getId());
	            stringer.key("CreditGrade").value(user.getCreditGrade());
	            stringer.key("DormitoryNumber").value(user.getDormitoryNumber());
	            stringer.key("Email").value(user.getEmail());
	            stringer.key("GoldBeanNumber").value(user.getGoldBeanNumber());
	            stringer.key("IAutography").value(user.getiAutography());
	            stringer.key("MajorClass").value(user.getMajorClass());
	            stringer.key("Password").value(user.getPassword());
	            stringer.key("PhotoUrl").value(user.getPhotoUrl());
	            stringer.key("QQNumber").value(user.getQqNumber());
	            stringer.key("Sex").value(user.getSex());
	            stringer.key("Username").value(user.getUsername());
	            System.out.println(user.getId());
	            stringer.endObject();
	            stringer.endArray();  
	        }  
	        catch(Exception e){}  
	        
	        response.getOutputStream().write(stringer.toString().getBytes("UTF-8"));  
	        response.setContentType("text/json; charset=UTF-8");  //JSON的类型为text/json
	        System.out.println(stringer.toString().getBytes("UTF-8")+"_______________");
		}
		else if(action.equals("queryAllUser")){
			ArrayList<User> users=userDao.queryAllUser();
			System.out.println("aaaa");
	        /*用json数组来传递数据*/
			JSONArray array=new JSONArray();
			for(int i=0;i<users.size();i++){
				try {
					JSONObject json=new JSONObject();
					json.put("ID", users.get(i).getId());
					json.put("Birthday", users.get(i).getBirthday());
					json.put("CreditGrade", users.get(i).getCreditGrade());
					json.put("DormitoryNumber", users.get(i).getDormitoryNumber());
					json.put("Email", users.get(i).getEmail());
					json.put("GoldBeanNumber", users.get(i).getGoldBeanNumber());
					json.put("IAutography", users.get(i).getiAutography());
					json.put("MajorClass", users.get(i).getMajorClass());
					json.put("Password", users.get(i).getPassword());
					json.put("PhotoUrl", users.get(i).getPhotoUrl());
					json.put("QQNumber", users.get(i).getQqNumber());
					json.put("Sex", users.get(i).getSex());
					json.put("Username", users.get(i).getUsername());
					array.put(json);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(users.get(i).getUsername());
				
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

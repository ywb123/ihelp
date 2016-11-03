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

import com.ihelp.dao.GeographyUserDao;
import com.ihelp.domain.GeographyUser;

public class GeographyUserServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public GeographyUserServlet() {
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

		GeographyUserDao gud=new GeographyUserDao();
		String action=request.getParameter("action");
		if(action.equals("add")){//输出1添加成功，输出0添加失败
			String result="0";
			int userId=Integer.parseInt(request.getParameter("userId"));
			int geographyId=Integer.parseInt(request.getParameter("geographyId"));
			double latitude=Double.parseDouble(request.getParameter("latitude"));
			double longitude=Double.parseDouble(request.getParameter("longitude"));
			if(gud.add(geographyId, userId,latitude,longitude)){
				result="1";
			}
			PrintWriter out = response.getWriter();
			out.print(result);
		}
		else if(action.equals("queryByGeographyId")){
			int geographyId=Integer.parseInt(request.getParameter("geographyId"));
			
			ArrayList<GeographyUser> gus=gud.queryByGeographyId(geographyId);

			/*用json数组来传递数据*/
			JSONArray array=new JSONArray();
			for(int i=0;i<gus.size();i++){
				try {
					JSONObject json=new JSONObject();
					json.put("id", gus.get(i).getId());
					json.put("geographyId", gus.get(i).getGeographyId());
					json.put("userId", gus.get(i).getUserId());
					json.put("latitude", gus.get(i).getLatitude());
					json.put("longitude", gus.get(i).getLongitude());
					System.out.println(gus.get(i).getUserId()+"aaaaaaaaaaaaaaa");
					array.put(json);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			System.out.println("kkkkkkkkk");
			response.setContentType("text/plain");  
            response.setCharacterEncoding("UTF-8");  
            PrintWriter out = response.getWriter();
            out.print(array.toString());
            out.flush();  
            out.close();  
		}else if(action.equals("updateLocationByUserId")){
			int geographyId=Integer.parseInt(request.getParameter("geographyId"));
			int userId=Integer.parseInt(request.getParameter("userId"));
			double latitude=Double.parseDouble(request.getParameter("latitude"));
			double longitude=Double.parseDouble(request.getParameter("longitude"));
			boolean b=gud.updateLocationByUserId(geographyId, userId,latitude,longitude);
			PrintWriter out = response.getWriter();
			if(b){out.print("1");}
			else{out.print("0");}
		}
		else if(action.equals("query")){
			ArrayList<GeographyUser> gus=gud.query();

			/*用json数组来传递数据*/
			JSONArray array=new JSONArray();
			for(int i=0;i<gus.size();i++){
				try {
					JSONObject json=new JSONObject();
					json.put("id", gus.get(i).getId());
					json.put("geographyId", gus.get(i).getGeographyId());
					json.put("userId", gus.get(i).getUserId());
					json.put("latitude", gus.get(i).getLatitude());
					json.put("longitude", gus.get(i).getLongitude());
					System.out.println(gus.get(i).getUserId()+"aaaaaaaaaaaaaaa");
					array.put(json);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			System.out.println("kkkkkkkkk");
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

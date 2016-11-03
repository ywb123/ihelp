package com.ihelp.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONStringer;

import com.ihelp.dao.GeographyDao;
import com.ihelp.domain.Geography;


public class GeographyServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public GeographyServlet() {
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

		String action = request.getParameter("action");
		if(action.equals("query")){
			double latitude=Double.parseDouble(request.getParameter("latitude"));
			
			double longitude=Double.parseDouble(request.getParameter("longitude"));
			GeographyDao gd=new GeographyDao();
			Geography geography=gd.queryIdByLocation(latitude, longitude);
			//客户端查询对象信息，返回json数据格式
			//将List<Geography>组织成JSON字符串   
	        JSONStringer stringer = new JSONStringer();  
	        try{  
	            stringer.array();   
	            stringer.object();
	            stringer.key("geographyName").value(geography.getGeographyName());
	            stringer.key("ID").value(geography.getId());
	            stringer.key("latitude").value(geography.getLatitude());
	            stringer.key("longitude").value(geography.getLongitude());
	            stringer.endObject();
	            stringer.endArray();  
	        }  
	        catch(Exception e){
	        	
	        }  
	        response.getOutputStream().write(stringer.toString().getBytes("UTF-8"));  
	        response.setContentType("text/json; charset=UTF-8");  //JSON的类型为text/json
	        System.out.println(geography.getGeographyName());
			
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

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

import com.ihelp.dao.TreeCaveDao;
import com.ihelp.domain.TreeCave;
import com.ihelp.domain.User;

public class TreeCaveServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public TreeCaveServlet() {
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

		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		TreeCaveDao tcd=new TreeCaveDao();
		String action=request.getParameter("action");
		if(action.equals("queryAllMessage")){
			//查询全部留言
			ArrayList<TreeCave> tcs=new ArrayList<TreeCave>(); 
			tcs=tcd.query();
			
			/*用json数组来传递数据*/
			JSONArray array=new JSONArray();
			for(int i=0;i<tcs.size();i++){
				try {
					JSONObject json=new JSONObject();
					json.put("id", tcs.get(i).getId());
					json.put("commentNumber", tcs.get(i).getCommentNumber());
					json.put("praiseNumber", tcs.get(i).getPraiseNumber());
					json.put("commentTime", tcs.get(i).getCommentTime());
					json.put("keyword", tcs.get(i).getKeyword());
					json.put("message", tcs.get(i).getMessage());
					array.put(json);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			response.setContentType("text/plain");  
            response.setCharacterEncoding("UTF-8");  
            PrintWriter out = response.getWriter();
            out.print(array.toString());
            out.flush();  
            out.close();  
		}
		else if(action.equals("queryById")){
			int id=Integer.parseInt(request.getParameter("id"));
			TreeCave tc=tcd.queryById(id);
			//客户端查询对象信息，返回json数据格式
			//将List<User>组织成JSON字符串   
			
	        JSONStringer stringer = new JSONStringer();  
	        try{  
	        	
	            stringer.array();   
	            stringer.object();
	            stringer.key("commentTime").value(tc.getCommentTime());
	            stringer.key("praiseNumber").value(tc.getPraiseNumber());
	            stringer.key("commentNumber").value(tc.getCommentNumber());
	            stringer.key("keyword").value(tc.getKeyword());
	            stringer.key("message").value(tc.getMessage());
	            stringer.key("id").value(tc.getId());
	            
	            stringer.endObject();
	            stringer.endArray();  
	        }  
	        catch(Exception e){}  
	        
	        response.getOutputStream().write(stringer.toString().getBytes("UTF-8"));  
	        response.setContentType("text/json; charset=UTF-8");  //JSON的类型为text/json
	        System.out.println(stringer.toString().getBytes("UTF-8"));
		}else if(action.equals("insert")){
			String result="0";
			int commentNumber=Integer.parseInt(request.getParameter("commentNumber"));
			String commentTime=request.getParameter("commentTime");
			String message=URLDecoder.decode(request.getParameter("message"),"utf-8");
			String keyword=request.getParameter("keyword");
			int praiseNumber=Integer.parseInt(request.getParameter("praiseNumber"));
			TreeCave tc=new TreeCave();
			tc.setCommentNumber(commentNumber);
			tc.setCommentTime(commentTime);
			tc.setKeyword(keyword);
			tc.setMessage(message);
			tc.setPraiseNumber(praiseNumber);
			if(tcd.add(tc)){
				result="1";
			}
			PrintWriter out = response.getWriter();
			out.print(result);
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

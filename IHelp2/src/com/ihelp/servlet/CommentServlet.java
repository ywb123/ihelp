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
import org.json.JSONStringer;

import com.ihelp.dao.CommentDao;
import com.ihelp.dao.GeographyDao;
import com.ihelp.dao.TreeCaveDao;
import com.ihelp.domain.Comment;
import com.ihelp.domain.Geography;

public class CommentServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public CommentServlet() {
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
		CommentDao cd=new CommentDao();
		String action = request.getParameter("action");
		if(action.equals("queryByMessageId")){
			int messageId=Integer.parseInt(request.getParameter("messageId"));
			
			ArrayList<Comment> comments=new ArrayList<Comment>();
			comments=cd.queryByMessageId(messageId);
			/*用json数组来传递数据*/
			JSONArray array=new JSONArray();
			for(int i=0;i<comments.size();i++){
				try {
					JSONObject json=new JSONObject();
					json.put("id", comments.get(i).getId());
					json.put("commentUserId", comments.get(i).getCommentUserId());
					json.put("messageId", comments.get(i).getMessageId());
					json.put("commentTime", comments.get(i).getCommentTime());
					json.put("commentContent", comments.get(i).getCommentContent());
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
		if(action.equals("add")){
			TreeCaveDao tcd=new TreeCaveDao();
			String result="0";
			int messageId=Integer.parseInt(request.getParameter("messageId"));
			int commentUserId=Integer.parseInt(request.getParameter("commentUserId"));
			String commentContent=request.getParameter("commentContent");
			String commentTime=request.getParameter("commentTime");
			System.out.println(messageId+",\n"+commentUserId+",\n"+commentContent+",\n"+commentTime);
			Comment c=new Comment();
			c.setCommentContent(commentContent);
			c.setCommentTime(commentTime);
			c.setCommentUserId(commentUserId);
			c.setMessageId(messageId);
			if(cd.add(c)&&tcd.updateCommentNum(messageId)){
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

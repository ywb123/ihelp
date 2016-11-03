package com.ihelp.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ihelp.sql.DBUtil;

/**
 * Servlet implementation class RegisterServlet
 */
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("000000");
		request.setCharacterEncoding("utf-8");
		String userName = URLDecoder.decode(request.getParameter("username"),"utf-8");
		System.out.println(request.getParameter("username"));
		System.out.println(userName);
		String password = URLDecoder.decode(request.getParameter("password"),"UTF-8");
		String email = request.getParameter("email");
		String sex = request.getParameter("sex");
		String birthday = request.getParameter("birthday");
		String dormitoryNumber = request.getParameter("dormitoryNumber");
		String majorClass = request.getParameter("majorClass");
		String iAutography = request.getParameter("iAutography");
		String photoUrl = request.getParameter("photoUrl");
		String creditGrade = request.getParameter("creditGrade");
		String qqNumber = request.getParameter("qqNumber");
		String goldBeanNumber ="20";
		System.out.println("1111111");
		
		String sql2 = "select count(*) from UserInfo where Name='"+userName+"'";
		String sql1 = "select * from UserInfo where Name='"+userName+"'";
		String sql = "insert into UserInfo(Name,Password,Email,Sex,Birthday,DormitoryNumber,MajorClass,IAutography,PhotoUrl,CreditGrade,QQNumber,GoldBeanNumber)" +
				" values('"+userName+"','"+password+"','"+email+"','"+sex+"','"+birthday+"','"+dormitoryNumber+"','"+majorClass+"','"+iAutography+"','"+photoUrl+"','"+creditGrade+"','"+qqNumber+"',"+goldBeanNumber+")";
		DBUtil util = new DBUtil();
		Connection conn = util.openConnection();
		PrintWriter out = response.getWriter();
		String result = "0";
		System.out.println("2222222222222");
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql2);
			ResultSet rs = pstmt.executeQuery();
			int count=0;
			System.out.println("3333333333");
			while(rs.next()) {
				count=rs.getInt(1);
			}
			if(count!=0){
				result = "REPEAT0";
			}else{
				Statement stmt = conn.createStatement();
				stmt.executeUpdate(sql);
				DBUtil util1 = new DBUtil();
				Connection conn1 = util1.openConnection();
				PreparedStatement pstmt1 = conn1.prepareStatement(sql1);
				ResultSet rs1 = pstmt1.executeQuery();
				if(rs1.next()){
					int id=rs1.getInt(1);
					System.out.println(id);
					result = "SUCCESS"+id;
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = "0000000";
		}finally {
			util.closeConn(conn);
		}	
		out.print(result);
	}
}

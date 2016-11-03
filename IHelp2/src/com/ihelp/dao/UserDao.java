package com.ihelp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.ihelp.domain.User;
import com.ihelp.sql.DBUtil;

public class UserDao {

	
	DBUtil db=new DBUtil();
	public UserDao(){
		
	}
	public ArrayList<User> queryAllUser(){
		
		ArrayList<User> users=new ArrayList<User>();
		String sql = "select * from UserInfo;";
		Connection conn = db.openConnection();
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				User user=new User();
				System.out.println(rs.getString("Name"));
				user.setId(rs.getInt("ID"));
				user.setBirthday(rs.getString("Birthday"));
				user.setCreditGrade(rs.getString("CreditGrade"));
				user.setDormitoryNumber(rs.getString("DormitoryNumber"));
				user.setEmail(rs.getString("Email"));
				user.setGoldBeanNumber(rs.getInt("GoldBeanNumber"));
				user.setiAutography(rs.getString("IAutography"));
				user.setMajorClass(rs.getString("MajorClass"));
				user.setPassword(rs.getString("Password"));
				user.setPhotoUrl(rs.getString("PhotoUrl"));
				user.setQqNumber(rs.getString("QQNumber"));
				user.setSex(rs.getString("Sex"));
				user.setUsername(rs.getString("Name"));
				users.add(user);
			}
			
		}catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeConn(conn);
		}	
		return users;
		
	}
	public ArrayList<User> queryBylikeUsername(String username){
		ArrayList<User> users=new ArrayList<User>();
		String sql = "select * from UserInfo where Name like'%"+username+"%';";
		Connection conn = (Connection) db.openConnection();
		try {
			PreparedStatement pstmt = (PreparedStatement)conn.prepareStatement(sql);
			ResultSet rs = (ResultSet)pstmt.executeQuery();
			while(rs.next()) {
				User user=new User();
				System.out.println(rs.getString("Name"));
				user.setId(rs.getInt("ID"));
			    user.setBirthday(rs.getString("Birthday"));
				user.setCreditGrade(rs.getString("CreditGrade"));
				user.setDormitoryNumber(rs.getString("DormitoryNumber"));
				
				user.setGoldBeanNumber(rs.getInt("GoldBeanNumber"));
				user.setiAutography(rs.getString("IAutography"));
				user.setMajorClass(rs.getString("MajorClass"));
				
				user.setPhotoUrl(rs.getString("PhotoUrl"));
				user.setQqNumber(rs.getString("QQNumber"));
				user.setSex(rs.getString("Sex"));
				user.setPassword(rs.getString("Password"));
				user.setEmail(rs.getString("Email"));
				user.setUsername(rs.getString("Name"));
				users.add(user);
			}
			
		}catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeConn(conn);
		}	
		return users;
	}
	public ArrayList<User> queryByMajorClass(int userId){
		ArrayList<User> users=new ArrayList<User>();
		String sql = "select * from UserInfo where MajorClass in(select MajorClass from UserInfo where ID="+userId+")and ID!="+userId+";";
		Connection conn = (Connection) db.openConnection();
		try {
			PreparedStatement pstmt = (PreparedStatement)conn.prepareStatement(sql);
			ResultSet rs = (ResultSet)pstmt.executeQuery();
			while(rs.next()) {
				User user=new User();
				System.out.println(rs.getString("Name"));
				user.setId(rs.getInt("ID"));
			    user.setBirthday(rs.getString("Birthday"));
				user.setCreditGrade(rs.getString("CreditGrade"));
				user.setDormitoryNumber(rs.getString("DormitoryNumber"));
				user.setGoldBeanNumber(rs.getInt("GoldBeanNumber"));
				user.setiAutography(rs.getString("IAutography"));
				user.setMajorClass(rs.getString("MajorClass"));
				user.setPhotoUrl(rs.getString("PhotoUrl"));
				user.setQqNumber(rs.getString("QQNumber"));
				user.setSex(rs.getString("Sex"));
				user.setPassword(rs.getString("Password"));
				user.setEmail(rs.getString("Email"));
				user.setUsername(rs.getString("Name"));
				users.add(user);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeConn(conn);
		}	
		return users;
	}
	public User queryById(int userId){
		User user=new User();
		String sql = "select * from UserInfo where ID= "+userId+";";
		Connection conn = db.openConnection();
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				user.setId(rs.getInt("ID"));
				user.setBirthday(rs.getString("Birthday"));
				user.setCreditGrade(rs.getString("CreditGrade"));
				user.setDormitoryNumber(rs.getString("DormitoryNumber"));
				user.setEmail(rs.getString("Email"));
				user.setGoldBeanNumber(rs.getInt("GoldBeanNumber"));
				user.setiAutography(rs.getString("IAutography"));
				user.setMajorClass(rs.getString("MajorClass"));
				user.setPassword(rs.getString("Password"));
				user.setPhotoUrl(rs.getString("PhotoUrl"));
				user.setQqNumber(rs.getString("QQNumber"));
				user.setSex(rs.getString("Sex"));
				user.setUsername(rs.getString("Name"));
			}
			
		}catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeConn(conn);
		}	
		return user;
		
	}
	public boolean update(int userId,String birthday,String creditGrade,String dormitoryNumber,String email,int goldBeanNumber,
			String iAutography,String majorClass,String password,String photoUrl,String qqNumber,String sex){
		String sql="update UserInfo set Birthday='"+birthday+"',CreditGrade='"+creditGrade+"',DormitoryNumber='"+dormitoryNumber
				+"',GoldBeanNumber="+goldBeanNumber+",Email='"+email+"',IAutography='"+iAutography+"',MajorClass='"+majorClass
				+"',Password='"+password+"',PhotoUrl='"+photoUrl+"',QQNumber='"+qqNumber+"',Sex='"+sex+"'where ID="+userId;
		boolean b=false;
		Connection conn=db.openConnection();
		try {
			PreparedStatement ps=conn.prepareStatement(sql);
			int rs=ps.executeUpdate();
			
			if(rs!=0){
				b=true;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			db.closeConn(conn);
		}
		return b;
	}
	public boolean updateNumberByUser(String username,int goldBeanNumber){
		
		boolean b=false;
		String sql="update UserInfo set GoldBeanNumber="+goldBeanNumber +" where Name='"+username+"';";
		Connection conn=db.openConnection();
		try {
			PreparedStatement ps=conn.prepareStatement(sql);
			int rs=ps.executeUpdate();
			
			if(rs!=0){
				b=true;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			db.closeConn(conn);
		}
		return b;
	}
	public User queryByUsername(String username){
		User user=new User();
		String sql = "select * from UserInfo where Name= '"+username +"';";
		Connection conn = db.openConnection();
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				user.setId(rs.getInt("ID"));
				user.setBirthday(rs.getString("Birthday"));
				user.setCreditGrade(rs.getString("CreditGrade"));
				user.setDormitoryNumber(rs.getString("DormitoryNumber"));
				user.setEmail(rs.getString("Email"));
				user.setGoldBeanNumber(rs.getInt("GoldBeanNumber"));
				user.setiAutography(rs.getString("IAutography"));
				user.setMajorClass(rs.getString("MajorClass"));
				user.setPassword(rs.getString("Password"));
				user.setPhotoUrl(rs.getString("PhotoUrl"));
				user.setQqNumber(rs.getString("QQNumber"));
				user.setSex(rs.getString("Sex"));
				user.setUsername(rs.getString("Name"));
			}
			
		}catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeConn(conn);
		}	
		return user;
		
	}
}

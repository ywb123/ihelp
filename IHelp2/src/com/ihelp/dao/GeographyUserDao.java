package com.ihelp.dao;

import java.sql.SQLException;
import java.util.ArrayList;

import com.ihelp.domain.GeographyUser;
import com.ihelp.sql.DBUtil;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSet;

public class GeographyUserDao {

	DBUtil db=new DBUtil();
	public GeographyUserDao(){
		
	}
	public boolean add(int geographyId,int userId,double lat,double lon){
		String sql="insert into UserGeography(UserID,GeographyID,latitude,longitude)values("+userId+","+geographyId+","+lat+","+lon+");";
		DBUtil db=new DBUtil();
		boolean result=false;
		Connection conn=(Connection) db.openConnection();
		try {
			PreparedStatement ps=(PreparedStatement) conn.prepareStatement(sql);
			
			if(ps.executeUpdate()!=0){
				result=true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			db.closeConn(conn);
		}
		return result;
	}
	/*
	 * 通过地点ID查询用户ID，即附近的人
	 * 
	 * **/
	public ArrayList<GeographyUser> queryByGeographyId(int geographyId){
		
		String sql="select * from UserGeography where GeographyID="+geographyId+";";
		DBUtil db=new DBUtil();
		ArrayList<GeographyUser> gus=new ArrayList<GeographyUser>();
		Connection conn=(Connection) db.openConnection();
		try {
			PreparedStatement ps=(PreparedStatement) conn.prepareStatement(sql);
			ResultSet rs=(ResultSet) ps.executeQuery();
			while(rs.next()){
				GeographyUser gu=new GeographyUser();
				gu.setGeographyId(geographyId);
				gu.setId(rs.getInt("ID"));
				gu.setUserId(rs.getInt("UserID"));
				gu.setLatitude(rs.getDouble("latitude"));
				gu.setLongitude(rs.getDouble("longitude"));
				gus.add(gu);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			db.closeConn(conn);
		}
		
		return gus;
	}
	public boolean updateLocationByUserId(int geographyId,int userId,double lat,double lon){

		System.out.println("**********");
		String sql="update UserGeography set GeographyID="+geographyId+",latitude="+lat+",longitude="+lon+" where UserID="+userId+";";
		boolean b=false;
		Connection conn=(Connection) db.openConnection();
		try {
			PreparedStatement ps=(PreparedStatement) conn.prepareStatement(sql);
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
		System.out.println(b);
		return b;
	}
	public ArrayList<GeographyUser> query(){
		
		String sql="select * from UserGeography;";
		DBUtil db=new DBUtil();
		ArrayList<GeographyUser> gus=new ArrayList<GeographyUser>();
		Connection conn=(Connection) db.openConnection();
		try {
			PreparedStatement ps=(PreparedStatement) conn.prepareStatement(sql);
			ResultSet rs=(ResultSet) ps.executeQuery();
			while(rs.next()){
				GeographyUser gu=new GeographyUser();
				gu.setGeographyId(rs.getInt("GeographyID"));
				gu.setId(rs.getInt("ID"));
				gu.setUserId(rs.getInt("UserID"));
				gu.setLatitude(rs.getDouble("latitude"));
				gu.setLongitude(rs.getDouble("longitude"));
				gus.add(gu);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			db.closeConn(conn);
		}
		
		return gus;
	}
}

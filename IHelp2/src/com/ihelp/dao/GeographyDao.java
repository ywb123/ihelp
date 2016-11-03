package com.ihelp.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.ihelp.domain.Geography;
import com.ihelp.sql.DBUtil;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ResultSet;


public class GeographyDao {

	public GeographyDao(){
		
	}
	public Geography queryIdByLocation(double latitude,double longitude){
		String sql="select * from Geography where Latitude-"+latitude+"<1 and Latitude-"
				+latitude+">-1 and Longitude-"
				+longitude+"<1 and Longitude-"+longitude+">-1";
		Geography geography=new Geography();
		DBUtil db=new DBUtil();
		Connection conn=(Connection) db.openConnection();
		try {
			PreparedStatement pstmt=conn.prepareStatement(sql);
			ResultSet rs=(ResultSet) pstmt.executeQuery();
			if(rs.next()){
				geography.setGeographyName(rs.getString("Name"));
				geography.setLatitude(latitude);
				geography.setLongitude(longitude);
				geography.setId(rs.getInt("ID"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			db.closeConn(conn);
		}
		
		return geography;
		
	}
	
}

package com.ihelp.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
	public void closeConn(Connection conn){
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Connection openConnection() {
		
		String driver = null;
		String url = null;
		String username = null;
		String password = null;

		try {
			
			driver = "com.mysql.jdbc.Driver";
			url = "jdbc:mysql://w.rdc.sae.sina.com.cn:3307/app_ihelp2";
			username = "jlxkylm2l3";
			password = "3145yiji50yy3hzl5wxi3ykw3wky4kyj3yk5ik1k";
			
			Class.forName(driver);
			return DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
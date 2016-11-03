package com.ihelp.dao;

import java.sql.SQLException;

import com.ihelp.domain.GoldBean;
import com.ihelp.sql.DBUtil;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSet;

public class GoldBeanDao {

	private DBUtil db=new DBUtil();
	public GoldBean queryByNumber(int number){
		GoldBean goldBean=new GoldBean();
		String sql="select *from GoldBean where Number="+number+";";
		Connection conn=(Connection) db.openConnection();
		try {
			PreparedStatement ps=(PreparedStatement) conn.prepareStatement(sql);
			ResultSet rs=(ResultSet) ps.executeQuery();
			if(rs.next()){
				goldBean.setNumber(number);
				goldBean.setTopic(rs.getString("Topic"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return goldBean;
	}
	
}

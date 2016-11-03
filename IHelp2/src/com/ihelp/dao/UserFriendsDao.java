package com.ihelp.dao;

import java.sql.SQLException;
import java.util.ArrayList;

import com.ihelp.domain.UserFriends;
import com.ihelp.sql.DBUtil;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSet;

public class UserFriendsDao {
	private DBUtil db=new DBUtil();
	public UserFriendsDao(){
		
	}
	public boolean add(int userId,int friendId){
		String sql="insert into UserFriends(UserID,FriendID)values("+userId+","+friendId+");";
		boolean result=false;
		Connection conn=(Connection) db.openConnection();
		try {
			PreparedStatement ps=(PreparedStatement) conn.prepareStatement(sql);
			if(ps.executeUpdate()!=0)
			{
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
	public int calculate(int userId1,int userId2){
		String sql="select count(*) from UserFriends where UserID="+userId1+"and FriendID in  (select FriendID from UserFriends where UserID="+userId2+";";
		int count=0;
		Connection conn=(Connection) db.openConnection();
		try {
			PreparedStatement ps=(PreparedStatement) conn.prepareStatement(sql);
			ResultSet rs=(ResultSet) ps.executeQuery();
			while(rs.next()){
				count=rs.getInt("count(*)");
			}
		} 
			catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		finally{
			db.closeConn(conn);
		}
		return count;
	}
	public ArrayList<UserFriends> queryByCommonFriendId(int userId){
		ArrayList<UserFriends> ufs=new ArrayList<UserFriends>();
		String sql="select * from UserFriends where FriendID in (select FriendID from UserFriends where UserID="+userId+")and UserID!="+userId+";";
		Connection conn=(Connection) db.openConnection();
		try {
			PreparedStatement ps=(PreparedStatement) conn.prepareStatement(sql);
			ResultSet rs=(ResultSet) ps.executeQuery();
			while(rs.next()){
				UserFriends uf=new UserFriends();
				uf.setFriendId(rs.getInt("FriendID"));
				uf.setId(rs.getInt("ID"));
				uf.setUserId(rs.getInt("UserID"));
				ufs.add(uf);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			db.closeConn(conn);
		}
		
		return ufs;
	}
	public ArrayList<UserFriends> queryByUserId(int userId){
		ArrayList<UserFriends> ufs=new ArrayList<UserFriends>();
		String sql="select * from UserFriends where UserID="+userId;
		Connection conn=(Connection) db.openConnection();
		try {
			PreparedStatement ps=(PreparedStatement) conn.prepareStatement(sql);
			ResultSet rs=(ResultSet) ps.executeQuery();
			while(rs.next()){
				UserFriends uf=new UserFriends();
				uf.setFriendId(rs.getInt("FriendID"));
				uf.setId(rs.getInt("ID"));
				uf.setUserId(userId);
				ufs.add(uf);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			db.closeConn(conn);
		}
		
		return ufs;
	}
}

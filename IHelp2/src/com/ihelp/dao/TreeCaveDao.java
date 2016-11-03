package com.ihelp.dao;

import java.sql.SQLException;
import java.util.ArrayList;

import com.ihelp.domain.TreeCave;
import com.ihelp.sql.DBUtil;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSet;

public class TreeCaveDao {

	private DBUtil db=new DBUtil();
	public ArrayList<TreeCave> query(){
		
		ArrayList<TreeCave> tcs=new ArrayList<TreeCave>();
		String sql="select * from TreeCave;";
		Connection conn=(Connection) db.openConnection();
		try {
			PreparedStatement ps=(PreparedStatement) conn.prepareStatement(sql);
			ResultSet rs=(ResultSet) ps.executeQuery();
			while(rs.next()){
				TreeCave tc=new TreeCave();
				tc.setCommentNumber(rs.getInt("CommentNumber"));
				tc.setCommentTime(rs.getString("CommentTime"));
				tc.setId(rs.getInt("ID"));
				tc.setKeyword(rs.getString("KeyWord"));
				tc.setMessage(rs.getString("Message"));
				tc.setPraiseNumber(rs.getInt("PraiseNumber"));
				tcs.add(tc);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			db.closeConn(conn);
		}
		
		return tcs;
	}
	public TreeCave queryById(int id){
		TreeCave tc=new TreeCave();
		ArrayList<TreeCave> tcs=new ArrayList<TreeCave>();
		String sql="select * from TreeCave where ID="+id+";";
		Connection conn=(Connection) db.openConnection();
		try {
			PreparedStatement ps=(PreparedStatement) conn.prepareStatement(sql);
			ResultSet rs=(ResultSet) ps.executeQuery();
			if(rs.next()){
				tc.setCommentNumber(rs.getInt("CommentNumber"));
				tc.setCommentTime(rs.getString("CommentTime"));
				tc.setId(rs.getInt("ID"));
				tc.setKeyword(rs.getString("KeyWord"));
				tc.setMessage(rs.getString("Message"));
				tc.setPraiseNumber(rs.getInt("PraiseNumber"));
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			db.closeConn(conn);
		}
		
		return tc;
	}
	public boolean add(TreeCave tc){
		boolean result=false;
		String sql="insert into TreeCave(Message,KeyWord,PraiseNumber,CommentNumber,CommentTime)values('"
		+tc.getMessage()+"','"+tc.getKeyword()+"',"+tc.getPraiseNumber()+","+tc.getCommentNumber()+",'"+tc.getCommentTime()+"')";
		Connection conn=(Connection) db.openConnection();
		PreparedStatement ps;
		try {
			ps = (PreparedStatement) conn.prepareStatement(sql);
			if(ps.executeUpdate()!=0){
				result=true;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	public boolean updateCommentNum(int id){
		boolean result=false;
		Connection conn=(Connection) db.openConnection();
		TreeCave tc=queryById(id);
		int num=tc.getCommentNumber()+1;
		String sql="update TreeCave set CommentNumber="+num+" where ID="+id+";";
		PreparedStatement ps;
		try {
			ps = (PreparedStatement) conn.prepareStatement(sql);
			if(ps.executeUpdate()!=0){
				result=true;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
}

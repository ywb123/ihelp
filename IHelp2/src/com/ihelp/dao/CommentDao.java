package com.ihelp.dao;

import java.sql.SQLException;
import java.util.ArrayList;

import com.ihelp.domain.Comment;
import com.ihelp.domain.GeographyUser;
import com.ihelp.sql.DBUtil;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSet;

public class CommentDao {
	public CommentDao(){}
	public boolean add(Comment c){
		String sql="insert into Comment(MessageID,CommentContent,CommentUserID,CommentTime)values("+c.getMessageId()+",'"+c.getCommentContent()+"',"+c.getCommentUserId()+",'"+c.getCommentTime()+"');";
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
	public ArrayList<Comment> queryByMessageId(int messageId){
		ArrayList<Comment> comments=new ArrayList<Comment>();
		String sql="select * from Comment where MessageID="+messageId+";";
		DBUtil db=new DBUtil();
		Connection conn=(Connection) db.openConnection();
		try {
			PreparedStatement ps=(PreparedStatement) conn.prepareStatement(sql);
			ResultSet rs=(ResultSet) ps.executeQuery();
			while(rs.next()){
				Comment c=new Comment();
				c.setCommentContent(rs.getString("CommentContent"));
				c.setCommentTime(rs.getString("CommentTime"));
				c.setCommentUserId(rs.getInt("CommentUserID"));
				c.setId(rs.getInt("ID"));
				c.setMessageId(messageId);
				comments.add(c);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			db.closeConn(conn);
		}
		
		return comments;
	}
}

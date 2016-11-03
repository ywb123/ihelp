package domin;

import java.io.Serializable;



public class Response implements Serializable {
	private static final long serialVersionUID = 1L;
    private int replyid;
	
	private String response;
	private String username;
	private String date;
	public int getId() {
		return replyid;
	}

	public void setId(int id) {
		this.replyid = id;
	} 
	
	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	} 
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	} 
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	} 
}

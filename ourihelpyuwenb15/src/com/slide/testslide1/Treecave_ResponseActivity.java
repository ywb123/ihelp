package com.slide.testslide1;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ywb.uploadData.util.HttpUtil;

import domin.Comment;
import domin.Response;
import domin.User;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Treecave_ResponseActivity extends Activity {
	protected static final int SHOW = 0;
	private EditText etMessage;
	private TextView tvMessage;
	
	private ArrayList<Comment> comments=new ArrayList<Comment>();
	private ListView LV_Response;
	
	private ArrayList<User> users=new ArrayList<User>();
	private int messageId;
	private int userId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_treecave__response);
		
		etMessage=(EditText) findViewById(R.id.myresponse);
		tvMessage=(TextView) findViewById(R.id.tvMessage);
        LV_Response=(ListView) findViewById(R.id.LV_Response);
        
        Intent intent=getIntent();
	    Bundle bundle=intent.getExtras();
	    messageId=bundle.getInt("messageid");
	    Log.d("kkk", ""+messageId);
	    String content=bundle.getString("content");
	    tvMessage.setText(content);
	    getCommentByMessageId(messageId);
	    
	    SharedPreferences sp=this.getSharedPreferences("share",MODE_WORLD_READABLE);
	    userId=sp.getInt("userid", 0);
	}

	public void sure(View v){
		addComment();
		
	}
	public void flash(View v){
		getCommentByMessageId(messageId);
	}
	
	private class MyAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return users.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return users.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v=null;
			if(convertView==null){
				v = LayoutInflater.from(Treecave_ResponseActivity.this).inflate(R.layout.reply_list_view, null);
			}else{v=convertView;}
			
			TextView replyauthor= (TextView) v.findViewById(R.id.replyauthor);
			replyauthor.setText(comments.get(position).getCommentUserId()+users.get(position).getUsername());
			
			TextView replycontent= (TextView) v.findViewById(R.id.replycontent);
			replycontent.setText(comments.get(position).getCommentContent());
			
			TextView replydate= (TextView) v.findViewById(R.id.replydate);
			replydate.setText(comments.get(position).getCommentTime());
			return v;
		}
		
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.treecave__response, menu);
		return true;
	}
	//查询已有的评论
	public void getCommentByMessageId(int messageId){
		AsyncHttpClient client= new AsyncHttpClient();
		String path=HttpUtil.URL+"CommentServlet";
		RequestParams params=new RequestParams();
		params.add("action", "queryByMessageId");
		params.add("messageId", messageId+"");
		try {
			client.post(path, params, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					String result=new String(responseBody);
					try {
						comments=new ArrayList<Comment>();
						users=new ArrayList<User>();
						JSONArray array = new JSONArray(result);
						int length = array.length();
						for (int i = 0; i < length; i++) {
							JSONObject object = array.getJSONObject(i);
							getUserInfoById(object.getInt("commentUserId"));
							Comment c=new Comment();
							c.setMessageId(object.getInt("messageId"));
							c.setId(object.getInt("id"));
							c.setCommentTime(object.getString("commentTime"));
							c.setCommentContent(object.getString("commentContent"));
							c.setCommentUserId(object.getInt("commentUserId"));
							comments.add(c);
						}
						
					} catch (JSONException e) {e.printStackTrace();}
					new Thread(new Runnable(){
						@Override
						public void run() {
							// TODO Auto-generated method stub
							while(users.size()!=comments.size()){}
							handler.sendEmptyMessage(SHOW);
						}}).start();
					
				}
				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					Toast.makeText(Treecave_ResponseActivity.this, "未连接到服务器", Toast.LENGTH_SHORT).show();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(Treecave_ResponseActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
		}
	}
	//添加评论
	public void addComment(){
		AsyncHttpClient client= new AsyncHttpClient();
		String commentContent=etMessage.getText().toString().trim();
		
		SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss");
		Date curDate=new Date(System.currentTimeMillis());
		String strCommentTime=formatter.format(curDate);
		//Time t=new Time("GMT+8");
		//t.setToNow();
		//String strCommentTime=t.year+"_"+t.month+"_"+t.monthDay+"/"+t.hour+":"+t.minute+":"+t.second;
		if(commentContent.equals(""))
		{
			Toast.makeText(this, "评论信息为空", Toast.LENGTH_SHORT).show();
		}
		else{
			RequestParams params=new RequestParams();
			params.add("commentContent", commentContent);
			params.add("messageId", messageId+"");
			params.add("commentTime", strCommentTime);
			params.add("commentUserId", userId+"");
			params.add("action", "add");
			
			String path=HttpUtil.URL+"CommentServlet";
			try {
				client.post(path, params, new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
						// TODO Auto-generated method stub
						String str=new String(responseBody);
						if(str.equals("1")){
							getCommentByMessageId(messageId);
							Toast.makeText(Treecave_ResponseActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
							etMessage.setText("");
						}
						else
							Toast.makeText(Treecave_ResponseActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
					}
					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						// TODO Auto-generated method stub
						Toast.makeText(Treecave_ResponseActivity.this, "未连接到服务器", Toast.LENGTH_SHORT).show();
					}
				});
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(Treecave_ResponseActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
			}
			
				
		
		}
	}
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case SHOW:
				LV_Response.setAdapter(new MyAdapter());
				break;

			default:
				break;
			}
		}
		
	};
	void getUserInfoById(int userId){
		AsyncHttpClient client= new AsyncHttpClient();
		String path=HttpUtil.URL+"UserServlet";
		RequestParams params=new RequestParams();
		params.add("action", "queryById");
		params.add("id", userId+"");
		try {
			client.post(path, params, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					String result=new String(responseBody);
					try {
						
						JSONArray array = new JSONArray(result);
						int length = array.length();
						for (int i = 0; i < length; i++) {
							JSONObject object = array.getJSONObject(i);
							User user=new User();
							user.setBirthday(object.getString("Birthday"));
							user.setCreditGrade(object.getString("CreditGrade"));
							user.setDormitoryNumber(object.getString("DormitoryNumber"));
							user.setEmail(object.getString("Email"));
							user.setGoldBeanNumber(object.getInt("GoldBeanNumber"));
							user.setiAutography(object.getString("IAutography"));
							user.setId(object.getInt("ID"));
							user.setMajorClass(object.getString("MajorClass"));
							user.setPassword(object.getString("Password"));
							user.setPhotoUrl(object.getString("PhotoUrl"));
							user.setQqNumber(object.getString("QQNumber"));
							user.setSex(object.getString("Sex"));
							user.setUsername(object.getString("Username"));
						    users.add(user);
						}
						
					} catch (JSONException e) {e.printStackTrace();}	
				}
				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					Toast.makeText(Treecave_ResponseActivity.this, "未连接到服务器", Toast.LENGTH_SHORT).show();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(Treecave_ResponseActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
		}
	}
}

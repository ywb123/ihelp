package com.slide.testslide1;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.image.setcorner.ImageSetCorner;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.slide.testslide1.Fragment2_mainmyfriends.MyAdapter;
import com.ywb.uploadData.util.HttpUtil;

import domin.User;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
public class Friends_RecommendActivity extends Activity {
	protected static final int SHOW = 0;
	private List<User> Users=new ArrayList<User>();
	private ArrayList<Bitmap> bms=new ArrayList<Bitmap>();
	private TextView Name;
	private TextView MajorClass;
	private ListView lv_users;
	private ImageButton bt_insert; 
	private SharedPreferences sharedPreferences;
	@Override
    protected void onCreate(Bundle savedInstanceState){
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_friends_recommend);
	lv_users = (ListView)findViewById(R.id.lv_users);
	sharedPreferences = this.getSharedPreferences("test",MODE_WORLD_READABLE);
	//查找所有相同专业班级的好友；
	AsyncHttpClient client= new AsyncHttpClient();
	String path=HttpUtil.URL+"UserServlet";
	RequestParams params=new RequestParams();
	params.add("action", "queryByMajorClass");
	params.add("id", sharedPreferences.getString("userid", null));
	try {
		client.post(path, params,new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				// TODO Auto-generated method stub
			String str=new String(responseBody);
			try {
				JSONArray array = new JSONArray(str);
				final int length = array.length();
				for (int i = 0; i < length; i++) {
					JSONObject object = array.getJSONObject(i);
					final User p=new User();
					p.setEmail(object.getString("Email"));
					p.setId(object.getInt("ID"));
					p.setUsername(object.getString("Username"));
					p.setPassword(object.getString("Password"));
					p.setSex(object.getString("Sex"));
					p.setMajorClass(object.getString("MajorClass"));
					p.setPhotoUrl(object.getString("PhotoUrl"));
					final int index=i;
					Users.add(p);
					new Thread(){
						public void run(){
							 Bitmap bm=HttpUtil.getUserImage(p.getPhotoUrl());
							 bms.add(bm);
							 if(index==length-1){
								 handler.sendEmptyMessage(SHOW); 
							 }
							 
						} 
					}.start();
				}
			
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				// TODO Auto-generated method stub
				Toast.makeText(Friends_RecommendActivity.this, "未连接到服务器", Toast.LENGTH_SHORT).show();
			}
		});
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		Toast.makeText(Friends_RecommendActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
	}
}
	private Handler handler=new Handler(){
        @Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case SHOW:
				lv_users.setAdapter(new MyAdapter());
				break;
			default:
				break;
			}
		}
	};
public class MyAdapter extends BaseAdapter {
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return Users.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return Users.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int index=position;
	View view = LayoutInflater.from(Friends_RecommendActivity.this).inflate(R.layout.userlist, null);
	Name = (TextView) view.findViewById(R.id.username3);
	Name.setText(Users.get(position).getUsername());
	MajorClass = (TextView) view.findViewById(R.id.tv);
	MajorClass.setText(Users.get(position).getMajorClass());
	ImageView ivImage=(ImageView) view.findViewById(R.id.iv_photo);
	Bitmap output=ImageSetCorner.getRoundedCornerBitmap(bms.get(position), 2.0f);
	ivImage.setImageBitmap(output);
	bt_insert=(ImageButton)view.findViewById(R.id.bt_insert);
	bt_insert.setOnClickListener(new OnClickListener(){
		@Override
		public void onClick(View arg0) {
		// TODO Auto-generated method stub
			AsyncHttpClient client= new AsyncHttpClient();
			String path=HttpUtil.URL+"UserFriendsServlet";
			RequestParams params=new RequestParams();
			params.add("action", "queryByUserId");
			params.add("userId",sharedPreferences.getString("userid", null));
			try {
				client.post(path, params, new AsyncHttpResponseHandler() {
				@Override
					public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
						// TODO Auto-generated method stub
						String str=new String(responseBody);
						try {
							JSONArray array = new JSONArray(str);
							int length = array.length();
							boolean b=true;
							for (int i = 0; i < length; i++) {
								JSONObject object = array.getJSONObject(i);
								//判断推荐好友列表中是否有已经存在的好友
								if(Users.get(index).getId()==object.getInt("friendId")){
									b=false;
									Toast.makeText(Friends_RecommendActivity.this, "好友已存在", Toast.LENGTH_SHORT).show();
								}
							}
							if(b){
								int friendid=Users.get(index).getId();
								int userid = Integer.parseInt(sharedPreferences.getString("userid", null));
								AsyncHttpClient client= new AsyncHttpClient();
								String path1=HttpUtil.URL+"UserFriendsServlet";
								RequestParams params1=new RequestParams();
								params1.add("action", "add");
								params1.add("userId",userid+"");
								params1.add("friendId",friendid+"");
								try {
									client.post(path1, params1, new AsyncHttpResponseHandler() {
										@Override
										public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
											// TODO Auto-generated method stub
											String str=new String(responseBody);
											if(str.equals("1")){
												Toast.makeText(Friends_RecommendActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
											}
											else{
												Toast.makeText(Friends_RecommendActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
											}
										}
										@Override
										public void onFailure(int statusCode, Header[] headers,
												byte[] responseBody, Throwable error) {
											// TODO Auto-generated method stub
											Toast.makeText(Friends_RecommendActivity.this, "未连接到服务器", Toast.LENGTH_SHORT).show();
										}
									});
								}catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									Toast.makeText(Friends_RecommendActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
								}
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
					}
					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						// TODO Auto-generated method stub
						
					}
				});
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				Toast.makeText(Friends_RecommendActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
	        }
		}
	});
	 return view;
	}
}
	public void return3(View v) {
//  		Intent intent = new Intent(this, Friends_AddActivity.class);
//  		startActivity(intent);
  		finish();
  	  }
}


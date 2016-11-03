package com.slide.testslide1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.image.setcorner.ImageSetCorner;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ywb.uploadData.util.HttpUtil;

import domin.User;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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

public class Friends_FamiliarActivity extends Activity{
protected static final int SHOW = 0;
private List<User> Users=new ArrayList<User>();
private ArrayList<Bitmap> bms=new ArrayList<Bitmap>();
private TextView Name;
private TextView Prompt;
private ListView lv_users;
private ImageButton bt_insert; 
private SharedPreferences sharedPreferences;
@Override
protected void onCreate(Bundle savedInstanceState){
super.onCreate(savedInstanceState);
requestWindowFeature(Window.FEATURE_NO_TITLE);
setContentView(R.layout.activity_friends_familiar);
lv_users = (ListView)findViewById(R.id.lv_users);
sharedPreferences = this.getSharedPreferences("test",MODE_WORLD_READABLE);
final AsyncHttpClient client= new AsyncHttpClient();
String path1=HttpUtil.URL+"UserFriendsServlet";
RequestParams params1=new RequestParams();
params1.add("action", "queryByCommonFriendId");
params1.add("userId",sharedPreferences.getString("userid", null));
//通过登录用户的userid查找与其有共同好友的所有用户；
try {
	client.post(path1, params1,new AsyncHttpResponseHandler() {
		@Override
		public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
			// TODO Auto-generated method stub
			String str=new String(responseBody);
			try {
				JSONArray array = new JSONArray(str);
				int length = array.length();
				bms=new ArrayList<Bitmap>();
				Users=new ArrayList<User>();
				int term=0;
				for (int i = 0; i < length; i++) {
					JSONObject object = array.getJSONObject(i);
					int userid=object.getInt("userId");
					if(userid==term){
						continue;
					}
					else{
					term=userid;
					String path2=HttpUtil.URL+"UserServlet";
					RequestParams params2=new RequestParams();
					params2.add("action","queryById");
					params2.add("id",userid+"");
					//向服务器发出请求返回所有相应用户的信息并记录下来；
					try {
						client.post(path2, params2,new AsyncHttpResponseHandler() {
							@Override
							public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
								// TODO Auto-generated method stub
								String result=new String(responseBody);
								try {
									JSONArray array = new JSONArray(result);
									final int length = array.length();
									for (int i = 0; i < length; i++) {
										JSONObject object = array.getJSONObject(i);
										final User u=new User();
										u.setId(object.getInt("ID"));
										u.setUsername(object.getString("Username"));
										u.setPhotoUrl(object.getString("PhotoUrl"));
										final int index=i;
										Users.add(u);
										new Thread(){
											public void run(){
												 Bitmap bm=HttpUtil.getUserImage(u.getPhotoUrl());
												 bms.add(bm);
												 if(index==length-1){
													 handler.sendEmptyMessage(SHOW); 
												 }
												 
											} 
										}.start();
									}
									
								} catch (JSONException e) {
									e.printStackTrace();
								}	
							}
		      				@Override
							public void onFailure(int statusCode, Header[] headers,
									byte[] responseBody, Throwable error) {
								// TODO Auto-generated method stub
		      					Toast.makeText(Friends_FamiliarActivity.this, "未连接到服务器", Toast.LENGTH_SHORT).show();
							}
						});
					}  catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(Friends_FamiliarActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
					}
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
 			Toast.makeText(Friends_FamiliarActivity.this, "未连接到服务器", Toast.LENGTH_SHORT).show();
		}
	 });
} catch (Exception e) {
	// TODO: handle exception
	e.printStackTrace();
	Toast.makeText(Friends_FamiliarActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
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
		View view = null;
		if (convertView == null) {
			view = LayoutInflater.from(Friends_FamiliarActivity.this).inflate(R.layout.userlist, null);
		} else {
			view = convertView;
		}
		Name = (TextView) view.findViewById(R.id.username3);
		Name.setText(Users.get(position).getUsername());
		ImageView ivImage=(ImageView) view.findViewById(R.id.iv_photo);
		Bitmap output=ImageSetCorner.getRoundedCornerBitmap(bms.get(position), 2.0f);
		ivImage.setImageBitmap(output);
		bt_insert=(ImageButton) view.findViewById(R.id.bt_insert);
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
										Toast.makeText(Friends_FamiliarActivity.this, "好友已存在", Toast.LENGTH_SHORT).show();
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
													Toast.makeText(Friends_FamiliarActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
												}
												else{
													Toast.makeText(Friends_FamiliarActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
												}
											}
											@Override
											public void onFailure(int statusCode, Header[] headers,
													byte[] responseBody, Throwable error) {
												// TODO Auto-generated method stub
												Toast.makeText(Friends_FamiliarActivity.this, "未连接到服务器", Toast.LENGTH_SHORT).show();
											}
										});
									}catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										Toast.makeText(Friends_FamiliarActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
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
					Toast.makeText(Friends_FamiliarActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
		        }
			}
		});
		return view;
	}
  }
  //比较两个用户谁的共同好友更多；
 /* private class SortByCommonfriends implements Comparator<User>{
	  @Override
		public int compare(User u1, User u2) {
		  final String count1 = null;
		  final String count2 = null;
		  final AsyncHttpClient client= new AsyncHttpClient();
		  String path=HttpUtil.URL+"UserFriendsServlet";
		  RequestParams params1=new RequestParams();
		  params1.add("action","calculate");
		  params1.add("userId1",u1.getId()+"");
		  params1.add("userId2",sharedPreferences.getString("userid", null));
		  try {
			client.post(path, params1, new AsyncHttpResponseHandler() {
				
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					// TODO Auto-generated method stub
					String count1=new String(responseBody);
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					// TODO Auto-generated method stub
					Toast.makeText(Friends_FamiliarActivity.this, "未连接到服务器", Toast.LENGTH_SHORT).show();
				}
			});
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Toast.makeText(Friends_FamiliarActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
		}
		  RequestParams params2=new RequestParams();
		  params2.add("action","calculate");
		  params2.add("userId1",u2.getId()+"");
		  params2.add("userId2",sharedPreferences.getString("userid", null));
		  try {
			client.post(path, params2, new AsyncHttpResponseHandler() {
				
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					// TODO Auto-generated method stub
					String count2=new String(responseBody);
				}
				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					// TODO Auto-generated method stub
					Toast.makeText(Friends_FamiliarActivity.this, "未连接到服务器", Toast.LENGTH_SHORT).show();
				}
			});
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Toast.makeText(Friends_FamiliarActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
		}
	    if(Integer.parseInt(count1)<Integer.parseInt(count2)){
			  return 1;
		  }
		  else if(Integer.parseInt(count1)>Integer.parseInt(count2)){
			  return -1;
		  }
		  return 0;
	  }
  }*/
  public void return3(View v) {
//  		Intent intent = new Intent(this, Friends_AddActivity.class);
//  		startActivity(intent);
  		finish();
  	}
}


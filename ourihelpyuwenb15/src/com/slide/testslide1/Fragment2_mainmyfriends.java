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
import com.ywb.uploadData.util.HttpUtil;

import domin.User;
import domin.friends;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Fragment2_mainmyfriends extends Fragment {
	private List<friends> friends=new ArrayList<friends>();
	private List<User> Users=new ArrayList<User>();
	protected static final int SHOW = 0;
	TextView Name;
	ListView lv_friends;
	private Button bt_add;
	//private ImageButton bt_fresh;
	private Button bt_back;
	private Button bt_detail;
	private View v;
	private ImageView sex;
	private ImageView ivImage;
	private SharedPreferences sharedPreferences; 
	private SharedPreferences.Editor editor;
	private ArrayList<Bitmap> bms=new ArrayList<Bitmap>();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		v=inflater.inflate(R.layout.fragment2_mainmyfriends, null);
		
		bt_add=(Button)v.findViewById(R.id.btnJoin);
		//bt_fresh=(ImageButton)v.findViewById(R.id.freshButton);
		bt_back=(Button)v.findViewById(R.id.bt_back);
		lv_friends = (ListView)v.findViewById(R.id.lv_friends);
		//获得sharedPreferences实例
		sharedPreferences= getActivity().getSharedPreferences("test", getActivity().MODE_PRIVATE);
		//MODE_WORLD_READABLE指定该sharedPreferences的数据只能被其他应用程序读取但不可写
	    //获得editor实例
		editor=sharedPreferences.edit();
		bt_add.setOnClickListener(new OnClickListener() {			
		@Override
			public void onClick(View v) {
				//Toast.makeText(getActivity(), "aaa ", Toast.LENGTH_LONG).show();	
				Intent intent = new Intent(getActivity(), Friends_AddActivity.class);
				startActivity(intent);				
			}
		});
		show();
		
		
		bt_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), MainActivity.class);
				startActivity(intent);		
			}
		});
		return v;
	}
	void return1(View v){
		Intent intent=new Intent(getActivity(),MainActivity.class);
		startActivity(intent);
	}
	void show(){
		String username = sharedPreferences.getString("name", null);
		final AsyncHttpClient client= new AsyncHttpClient();
		RequestParams params1=new RequestParams();
		final RequestParams params2=new RequestParams();
		//向服务器提出请求通过用户名来查询该用户的id;
		String path1=HttpUtil.URL+"UserServlet";
		params1.add("action", "queryByUsername");
		params1.add("username",username);
		final String path2=HttpUtil.URL+"UserFriendsServlet";
		params2.add("action", "queryByUserId");		
		try {
			client.post(path1, params1, new AsyncHttpResponseHandler() {
				@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
			String result=new String(responseBody);
			try {
			    //解析服务器传回的jason数据，friends的arraylist数组  
				JSONArray array = new JSONArray(result);
				int length = array.length();
				for (int i = 0; i < length; i++) {
					JSONObject object = array.getJSONObject(i);
			        //获取返回的id;
				    int userid=object.getInt("ID");
				    //保存获取的用户名id
				    editor.putString("userid",userid+"");
				    editor.commit();
				    Log.d("bbb",userid+"");
				    params2.add("userId",userid+"");
					try {
						client.post(path2, params2, new AsyncHttpResponseHandler() {
							@Override
							public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
								// TODO Auto-generated method stub
                                //responseBody服务器传回的数据，jason格式
								String result=new String(responseBody);
								Log.d("ccc",result);
								try {
                                //解析服务器传回的jason数据，friends的arraylist数组 ;
									JSONArray array = new JSONArray(result);
									int length = array.length();
									for (int i = 0; i < length; i++) {
										JSONObject object = array.getJSONObject(i);
										//根据userid查找对应的friendid;
										int friendid=object.getInt("friendId");
										RequestParams params3=new RequestParams();
										String path3=HttpUtil.URL+"UserServlet";
										params3.add("action", "queryById");
										params3.add("id",friendid+"");
										//通过返回的friendid来向服务器发出请求，得到相应的User的arraylist数组；
										try {
											client.post(path3, params3,new AsyncHttpResponseHandler() {
												@Override
												public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
													// TODO Auto-generated method stub
													String result=new String(responseBody);
													try {
														JSONArray array = new JSONArray(result);
														final int length = array.length();
														/*bms=new ArrayList<Bitmap>();
														Users=new ArrayList<User>();*/
														for (int i = 0; i < length; i++) {
															JSONObject object = array.getJSONObject(i);
															final User u=new User();
															//获取服务器传回的3个字段。。。客户端要用到哪些字段，都要从服务器获取。。。
															u.setId(object.getInt("ID"));
															u.setUsername(object.getString("Username"));
															u.setSex(object.getString("Sex"));
															u.setPhotoUrl(object.getString("PhotoUrl"));
															Users.add(u);
															final int index=i;
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
							      					Toast.makeText(getActivity(), "未连接到服务器", Toast.LENGTH_SHORT).show();
												}
											});
										}  catch (Exception e) {
											e.printStackTrace();
											Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();
										}
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}	
							}
							@Override
							public void onFailure(int statusCode, Header[] headers,
									byte[] responseBody, Throwable error) {
								// TODO Auto-generated method stub
								Toast.makeText(getActivity(), "未连接到服务器", Toast.LENGTH_SHORT).show();
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();
					}	  //通过获得的userid向服务器数据库表中获取对应的friends;
				}
		} catch (JSONException e) {
						e.printStackTrace();
		}	
}
		@Override
		public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					// TODO Auto-generated method stub
					Toast.makeText(getActivity(), "未连接到服务器", Toast.LENGTH_SHORT).show();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();
		}
}
	private Handler handler=new Handler(){
        @Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case SHOW:
				lv_friends.setAdapter(new MyAdapter());
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
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = null;
			if (convertView == null) {
				view = LayoutInflater.from(getActivity()).inflate(R.layout.personallist, null);
			} else {
				view = convertView;
			}
			Name = (TextView) view.findViewById(R.id.username2);
			Name.setText(Users.get(position).getUsername());
			sex=(ImageView)view.findViewById(R.id.iv_gender);
			ivImage=(ImageView)view.findViewById(R.id.iv_photo);
			if(Users.get(position).getSex().equals("男")){
				//Drawable img1=getResources().getDrawable(R.drawable.xingbiem);
				sex.setImageResource(R.drawable.xingbiem);
			}
			else{
				//Drawable img2=getResources().getDrawable(R.drawable.xingbien);
				sex.setImageResource(R.drawable.xingbien);
			}
			Log.d("kkk", "url:"+Users.get(position).getPhotoUrl());
			
			Bitmap output=ImageSetCorner.getRoundedCornerBitmap(bms.get(position),2.0f);
			ivImage.setImageBitmap(output);
			
			bt_detail=(Button)view.findViewById(R.id.bt_detail);
			//点击查看按钮查看好友详细信息；
			bt_detail.setOnClickListener(new OnClickListener() {
			@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
			     Bundle data=new Bundle();
			     data.putString("clickUsername",Users. get(position).getUsername());
			     data.putInt("clickUserId", Users. get(position).getId());
			     Intent intent=new Intent(getActivity(),Friends_DetailActivity.class);
				intent.putExtras(data);
				startActivity(intent);
				}
			});
			return view;
		}
	}
	
}
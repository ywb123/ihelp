package com.slide.testslide1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ywb.uploadData.util.HttpUtil;

import domin.User;
import domin.UserInfoGeography;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.text.Layout.Alignment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class ShowBesideUser extends Activity {

	private TextView tvLoc;
	private ListView lvBeside;
	private TextView Name;

    private SharedPreferences sharedPreferences; 
	private TextView friendusername;
	private TextView friendinfo;
	private ImageView friendtoux;
	private View viewBefore = null;
	private String longClickUsername = null;
	private int geographyID;
	private ArrayList<UserInfoGeography> ugs=new ArrayList<UserInfoGeography>();
	private List<User> users=new ArrayList<User>();
	private List<User> users1=new ArrayList<User>();
	private User tapUser=new User();
	
	private static final String MYTAG = "mytag";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//设置activity无头部logo。全屏显示
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
		
		setContentView(R.layout.activity_show_beside_user);
		
		sharedPreferences = this.getSharedPreferences("test",MODE_WORLD_READABLE);
		tvLoc=(TextView) findViewById(R.id.tvLoc);
		lvBeside=(ListView) findViewById(R.id.lvBeside);
		lvBeside.setOnItemClickListener(new MyItemClickListener());// 
		Bundle data=this.getIntent().getExtras();
		tvLoc.setText(data.getString("loc")+" (附近的人)");
		//获取从GetPacketActivity页面传来的地点ID
		geographyID=data.getInt("geographyID");
		//获取在当前这些地点的好友
		getUserBeside(geographyID);
	}
	//“离我最近”：
			public void sortbydistance(View v){
				Collections.sort(users, new SortByDistance());
				for(User user:users){
					Log.i(MYTAG,user.getUsername()+"");
				}
				
				lvBeside.setAdapter(new MyAdapter1());
				
				
			}
		//“金豆最多”：
		public void sortbygoldbeannumber(View v){
			Collections.sort(users, new SortByGoldnumber());
			for(User user:users){
				Log.i(MYTAG,user.getGoldBeanNumber()+"");
			}
			lvBeside.setAdapter(new MyAdapter2());
			
		}
		//“我最熟悉”：
		public void sortbyfriend(View v){
			final AsyncHttpClient client= new AsyncHttpClient();
			RequestParams params=new RequestParams();
			final RequestParams params1=new RequestParams();
			String path=HttpUtil.URL+"UserFriendsServlet";
			final String path1=HttpUtil.URL+"UserServlet";
			params.add("action", "queryByUserId");	
			params.add("userId",sharedPreferences.getString("userid", null));
			Log.v(MYTAG,"2222");
			params1.add("action", "queryById");
			Log.v(MYTAG,"111");
			try {
				client.post(path, params, new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
						// TODO Auto-generated method stub
						String result=new String(responseBody);
						try {
							JSONArray array = new JSONArray(result);
							int length = array.length();
							for (int i = 0; i < length; i++) {
								JSONObject object = array.getJSONObject(i);
								int friendid=object.getInt("friendId");
								params1.add("id",friendid+"");
								try {
									client.post(path1, params1, new AsyncHttpResponseHandler() {									@Override
										public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
											// TODO Auto-generated method stub
											String result=new String(responseBody);
											try {
												JSONArray array = new JSONArray(result);
												int length = array.length();
												for (int i = 0; i < length; i++) {
													JSONObject object = array.getJSONObject(i);
													User u=new User();
													u.setId(object.getInt("ID"));
													u.setUsername(object.getString("Username"));
													u.setSex(object.getString("Sex"));
													u.setGoldBeanNumber(object.getInt("GoldBeanNumber"));
													users1.add(u);
												}
												lvBeside.setAdapter(new MyAdapter3());
												
											} catch (JSONException e) {e.printStackTrace();}
										}
				      					@Override
										public void onFailure(int statusCode, Header[] headers,
												byte[] responseBody, Throwable error) {
											// TODO Auto-generated method stub
											Toast.makeText(ShowBesideUser.this, "未连接到服务器", Toast.LENGTH_SHORT).show();
										}
									});
								} catch (Exception e) {
									e.printStackTrace();
									Toast.makeText(ShowBesideUser.this, "网络错误", Toast.LENGTH_SHORT).show();
								}
							}
						} catch (JSONException e) {e.printStackTrace();}
					}
					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						// TODO Auto-generated method stub
						Toast.makeText(ShowBesideUser.this, "未连接到服务器", Toast.LENGTH_SHORT).show();
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(this, "网络错误", Toast.LENGTH_SHORT).show();
			}
			
		}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_beside_user, menu);
		return true;
	}
	
	/*
	 * 向ListView添加查询结果，附近的人
	 */
	private class MyAdapter1 extends BaseAdapter {

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
			// TODO Auto-generated method stub
			/*TextView tv = new TextView(getApplicationContext());
			tv.setTextSize(20);
			tv.setHeight(80);
			tv.setTextColor(Color.BLACK);
			User u=users.get(position);
			tv.setText(u.getUsername()+"  "+u.getGoldBeanNumber()+"豆");
			return tv;*/
			
			View view=null;//新建空的view对象，最后返回它
			//若函数中参数convertView为空，则将view置为listview的显示样式xml文件，
			if(convertView==null){
				view=LayoutInflater.from(ShowBesideUser.this).inflate(R.layout.nearest_friend_item,null);
			}else {
				view=convertView;  //若不为空，直接将参数赋值给view
			}
			friendusername=(TextView)view.findViewById(R.id.item_username);
			friendinfo=(TextView)view.findViewById(R.id.item_info);
			friendtoux=(ImageView)view.findViewById(R.id.item_friendtoux);
			
			User u=users.get(position);
			friendusername.setText(u.getUsername());
			friendinfo.setText(200+position*100+"m");
			
			return view;
		}

	}
	
	/*
	 * 向ListView添加查询结果，附近的人
	 */
	private class MyAdapter2 extends BaseAdapter {

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
			// TODO Auto-generated method stub
			/*TextView tv = new TextView(getApplicationContext());
			tv.setTextSize(20);
			tv.setHeight(80);
			tv.setTextColor(Color.BLACK);
			User u=users.get(position);
			tv.setText(u.getUsername()+"  "+u.getGoldBeanNumber()+"豆");
			return tv;*/
			
			View view=null;//新建空的view对象，最后返回它
			//若函数中参数convertView为空，则将view置为listview的显示样式xml文件，
			if(convertView==null){
				view=LayoutInflater.from(ShowBesideUser.this).inflate(R.layout.nearest_friend_item,null);
			}else {
				view=convertView;  //若不为空，直接将参数赋值给view
			}
			friendusername=(TextView)view.findViewById(R.id.item_username);
			friendinfo=(TextView)view.findViewById(R.id.item_info);
			friendtoux=(ImageView)view.findViewById(R.id.item_friendtoux);
			
			User u=users.get(position);
			friendusername.setText(u.getUsername());
			friendinfo.setText(u.getGoldBeanNumber()+"颗");
			
			return view;
		}

	}
	
	/*
	 * 向ListView添加查询结果，附近的人
	 */
	private class MyAdapter3 extends BaseAdapter {

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
			
			
			View view=null;//新建空的view对象，最后返回它
			//若函数中参数convertView为空，则将view置为listview的显示样式xml文件，
			if(convertView==null){
				view=LayoutInflater.from(ShowBesideUser.this).inflate(R.layout.nearest_friend_item,null);
			}else {
				view=convertView;  //若不为空，直接将参数赋值给view
			}
			friendusername=(TextView)view.findViewById(R.id.item_username);
			friendinfo=(TextView)view.findViewById(R.id.item_info);
			friendtoux=(ImageView)view.findViewById(R.id.item_friendtoux);
			
			User u=users.get(position);
			friendusername.setText(u.getUsername());
			friendinfo.setText(u.getSex());
			
			return view;
		}

	}
	/*
	 * 单击附近的人Item监听者。与好友聊天
	 */
	public class MyItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			tapUser=users.get(arg2);
			if (viewBefore != null) {
				viewBefore.setBackgroundColor(Color.rgb(255,255,255));
			}
			//arg1.setBackgroundColor(Color.GREEN);
			arg1.setBackgroundColor(Color.rgb(204,153,255));
			viewBefore = arg1;
			
			new AlertDialog.Builder(ShowBesideUser.this).setTitle("操作")
			.setMessage("用户" + tapUser.getUsername())
			.setPositiveButton("与ta聊天", myChat)
			.setNegativeButton("查看资料", myListenerViewInfo).show();
			
			
		}
	}

	private OnClickListener myChat = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			Bundle data=new Bundle();
			data.putString("clickUsername", tapUser.getUsername());
			Intent intent=new Intent(ShowBesideUser.this,ChatWithUserActivity.class);
			intent.putExtras(data);
			startActivity(intent);
			Toast.makeText(ShowBesideUser.this, "与"+tapUser.getUsername()+"聊天", Toast.LENGTH_SHORT).show();
		}

	};
	private OnClickListener myListenerViewInfo = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			//查看资料代码
			Bundle data=new Bundle();
			data.putString("clickUsername", tapUser.getUsername());
			data.putInt("clickUserId", tapUser.getId());
			Intent intent=new Intent(ShowBesideUser.this,ViewInfo.class);
			intent.putExtras(data);
			startActivity(intent);
			Toast.makeText(ShowBesideUser.this, "查看"+tapUser.getUsername()+"的资料", Toast.LENGTH_SHORT).show();
			
		}

	};

	//根据地点ID获取在当前地点的好友ID
	public void getUserBeside(int gID ){
		AsyncHttpClient client= new AsyncHttpClient();
		//连接服务器，查询GeographyUserServlet
		String path=HttpUtil.URL+"GeographyUserServlet";
		RequestParams params=new RequestParams();
		params.add("action", "queryByGeographyId");
		params.add("geographyId", gID+"");
		try {
			client.post(path, params, new AsyncHttpResponseHandler() {
				
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					String result=new String(responseBody);
					try {
						JSONArray array = new JSONArray(result);
						int length = array.length();
						int[] userIds=new int[length] ;
						for (int i = 0; i < length; i++) {
							JSONObject object = array.getJSONObject(i);
						    userIds[i]=object.getInt("userId");						    
						}
						Message msg=new Message();
					    Bundle data=new Bundle();
					    data.clear();
					    data.putIntArray("userIds", userIds);//将获取到的用户ID数组设进入data
					    msg.setData(data);//为消息设置数据
					    msg.what=100;//为消息设置标志码
					    handler.sendMessage(msg);//利用句柄发送消息
					} catch (Exception e) {e.printStackTrace();}	
				}
				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					Toast.makeText(ShowBesideUser.this, "未连接到服务器", Toast.LENGTH_SHORT).show();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "网络错误", Toast.LENGTH_SHORT).show();
		}
	}
	//根据查询到的好友id查询好友表
	public void getUserByID(int ID ){
		AsyncHttpClient client= new AsyncHttpClient();
		//连接服务器。请求至UserServlet
		String path=HttpUtil.URL+"UserServlet";
		RequestParams params=new RequestParams();
		params.add("action", "queryById");
		params.add("id", ID+"");
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
							Message msg=new Message();
						    Bundle data=new Bundle();
						    data.clear();
						    data.putString("Username", object.getString("Username"));//将查询到的好友相关信息都存入data中
						    data.putInt("ID", object.getInt("ID"));
						    data.putInt("GoldBeanNumber", object.getInt("GoldBeanNumber"));
						    data.putString("Birthday", object.getString("Birthday"));
						    data.putString("CreditGrade", object.getString("CreditGrade"));
						    data.putString("DormitoryNumber", object.getString("DormitoryNumber"));
						    data.putString("Email", object.getString("Email"));
						    data.putString("IAutography", object.getString("IAutography"));
						    data.putString("MajorClass", object.getString("MajorClass"));
						    data.putString("Password", object.getString("Password"));
						    data.putString("PhotoUrl", object.getString("PhotoUrl"));
						    data.putString("QQNumber", object.getString("QQNumber"));
						    data.putString("Sex", object.getString("Sex"));
						    msg.setData(data);//为要发送消息设置数据data
						    msg.what=101;//为消息设置标志码
						    handler.sendMessage(msg);
						}
					} catch (JSONException e) {e.printStackTrace();}	
				}
				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					Toast.makeText(ShowBesideUser.this, "未连接到服务器", Toast.LENGTH_SHORT).show();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(ShowBesideUser.this, "网络错误", Toast.LENGTH_SHORT).show();
		}
	}
	//句柄
	Handler handler =new Handler(){
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 100:
				int[] userIds=msg.getData().getIntArray("userIds");
				for(int i=0;i<userIds.length;i++){
					UserInfoGeography ug=new UserInfoGeography();
					ug.setGeographyID(geographyID);
					ug.setUserID(userIds[i]);
					
					ugs.add(ug);//将查询到的UserInfoGeography  加入到ugs集中
					//获取好友
					getUserByID(ug.getUserID());//再通过UserInfoGeography中userid查询好友
				}
				
				break;
			case 101:
				User u=new User();
				u.setId(msg.getData().getInt("ID"));
				u.setBirthday(msg.getData().getString("Birthday"));
				u.setCreditGrade(msg.getData().getString("CreditGrade"));
				u.setDormitoryNumber(msg.getData().getString("DormitoryNumber"));
				u.setEmail(msg.getData().getString("Email"));
				u.setGoldBeanNumber(msg.getData().getInt("GoldBeanNumber"));
				u.setiAutography(msg.getData().getString("IAutography"));
				u.setMajorClass(msg.getData().getString("MajorClass"));
				u.setPassword(msg.getData().getString("Password"));
				u.setPhotoUrl(msg.getData().getString("PhotoUrl"));
				u.setQqNumber(msg.getData().getString("QQNumber"));
				u.setSex(msg.getData().getString("Sex"));
				u.setUsername(msg.getData().getString("Username"));
				users.add(u);//将查询到的好友具体信息 加至 users集中	
				//对users进行排序，通过SortByGoldnumber类（实现Comparator接口并重写compare方法），
				//调用sort方法时将ArrayList对象与实现Commparator接口的类的对象作为参数
				/*Collections.sort(users, new SortByGoldnumber());
				for(User user:users){
					Log.i(MYTAG,user.getGoldBeanNumber()+"");
				}*/
				break;
			default:
				break;
			}
		}
		
	};
	
	//声明SortByGoldnumber类，通过金豆数排序
		private class SortByGoldnumber implements Comparator<User>{

			@Override
			public int compare(User u1, User u2) {
				// TODO Auto-generated method stub
				/*User u1=(User)object1;
				User u2=(User)object2;*/
				//users按金豆数从高至低排序。。。
				if(u1.getGoldBeanNumber()<u2.getGoldBeanNumber()){
					return 1;
				}
				else if(u1.getGoldBeanNumber()>u2.getGoldBeanNumber()){
					return -1;
				}
				return 0;
				//return u1.getUsername().compareTo(u2.getUsername());
			}
		}
		//排序类SortByDistance：按姓名字母顺序排序
		private class SortByDistance implements Comparator<User>{

			@Override
			public int compare(User u1, User u2) {
				// TODO Auto-generated method stub
				/*User u1=(User)object1;
				User u2=(User)object2;*/
				//users按金豆数从高至低排序。。。
				/*if(u1.getGoldBeanNumber()<u2.getGoldBeanNumber()){
					return 1;
				}
				else if(u1.getGoldBeanNumber()>u2.getGoldBeanNumber()){
					return -1;
				}
				return 0;*/
				return u1.getUsername().compareTo(u2.getUsername());
			}
		}

		
}

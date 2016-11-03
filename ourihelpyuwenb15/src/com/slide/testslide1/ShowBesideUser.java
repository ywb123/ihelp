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
		//����activity��ͷ��logo��ȫ����ʾ
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
		
		setContentView(R.layout.activity_show_beside_user);
		
		sharedPreferences = this.getSharedPreferences("test",MODE_WORLD_READABLE);
		tvLoc=(TextView) findViewById(R.id.tvLoc);
		lvBeside=(ListView) findViewById(R.id.lvBeside);
		lvBeside.setOnItemClickListener(new MyItemClickListener());// 
		Bundle data=this.getIntent().getExtras();
		tvLoc.setText(data.getString("loc")+" (��������)");
		//��ȡ��GetPacketActivityҳ�洫���ĵص�ID
		geographyID=data.getInt("geographyID");
		//��ȡ�ڵ�ǰ��Щ�ص�ĺ���
		getUserBeside(geographyID);
	}
	//�������������
			public void sortbydistance(View v){
				Collections.sort(users, new SortByDistance());
				for(User user:users){
					Log.i(MYTAG,user.getUsername()+"");
				}
				
				lvBeside.setAdapter(new MyAdapter1());
				
				
			}
		//������ࡱ��
		public void sortbygoldbeannumber(View v){
			Collections.sort(users, new SortByGoldnumber());
			for(User user:users){
				Log.i(MYTAG,user.getGoldBeanNumber()+"");
			}
			lvBeside.setAdapter(new MyAdapter2());
			
		}
		//��������Ϥ����
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
											Toast.makeText(ShowBesideUser.this, "δ���ӵ�������", Toast.LENGTH_SHORT).show();
										}
									});
								} catch (Exception e) {
									e.printStackTrace();
									Toast.makeText(ShowBesideUser.this, "�������", Toast.LENGTH_SHORT).show();
								}
							}
						} catch (JSONException e) {e.printStackTrace();}
					}
					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						// TODO Auto-generated method stub
						Toast.makeText(ShowBesideUser.this, "δ���ӵ�������", Toast.LENGTH_SHORT).show();
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(this, "�������", Toast.LENGTH_SHORT).show();
			}
			
		}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_beside_user, menu);
		return true;
	}
	
	/*
	 * ��ListView��Ӳ�ѯ�������������
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
			tv.setText(u.getUsername()+"  "+u.getGoldBeanNumber()+"��");
			return tv;*/
			
			View view=null;//�½��յ�view������󷵻���
			//�������в���convertViewΪ�գ���view��Ϊlistview����ʾ��ʽxml�ļ���
			if(convertView==null){
				view=LayoutInflater.from(ShowBesideUser.this).inflate(R.layout.nearest_friend_item,null);
			}else {
				view=convertView;  //����Ϊ�գ�ֱ�ӽ�������ֵ��view
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
	 * ��ListView��Ӳ�ѯ�������������
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
			tv.setText(u.getUsername()+"  "+u.getGoldBeanNumber()+"��");
			return tv;*/
			
			View view=null;//�½��յ�view������󷵻���
			//�������в���convertViewΪ�գ���view��Ϊlistview����ʾ��ʽxml�ļ���
			if(convertView==null){
				view=LayoutInflater.from(ShowBesideUser.this).inflate(R.layout.nearest_friend_item,null);
			}else {
				view=convertView;  //����Ϊ�գ�ֱ�ӽ�������ֵ��view
			}
			friendusername=(TextView)view.findViewById(R.id.item_username);
			friendinfo=(TextView)view.findViewById(R.id.item_info);
			friendtoux=(ImageView)view.findViewById(R.id.item_friendtoux);
			
			User u=users.get(position);
			friendusername.setText(u.getUsername());
			friendinfo.setText(u.getGoldBeanNumber()+"��");
			
			return view;
		}

	}
	
	/*
	 * ��ListView��Ӳ�ѯ�������������
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
			
			
			View view=null;//�½��յ�view������󷵻���
			//�������в���convertViewΪ�գ���view��Ϊlistview����ʾ��ʽxml�ļ���
			if(convertView==null){
				view=LayoutInflater.from(ShowBesideUser.this).inflate(R.layout.nearest_friend_item,null);
			}else {
				view=convertView;  //����Ϊ�գ�ֱ�ӽ�������ֵ��view
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
	 * ������������Item�����ߡ����������
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
			
			new AlertDialog.Builder(ShowBesideUser.this).setTitle("����")
			.setMessage("�û�" + tapUser.getUsername())
			.setPositiveButton("��ta����", myChat)
			.setNegativeButton("�鿴����", myListenerViewInfo).show();
			
			
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
			Toast.makeText(ShowBesideUser.this, "��"+tapUser.getUsername()+"����", Toast.LENGTH_SHORT).show();
		}

	};
	private OnClickListener myListenerViewInfo = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			//�鿴���ϴ���
			Bundle data=new Bundle();
			data.putString("clickUsername", tapUser.getUsername());
			data.putInt("clickUserId", tapUser.getId());
			Intent intent=new Intent(ShowBesideUser.this,ViewInfo.class);
			intent.putExtras(data);
			startActivity(intent);
			Toast.makeText(ShowBesideUser.this, "�鿴"+tapUser.getUsername()+"������", Toast.LENGTH_SHORT).show();
			
		}

	};

	//���ݵص�ID��ȡ�ڵ�ǰ�ص�ĺ���ID
	public void getUserBeside(int gID ){
		AsyncHttpClient client= new AsyncHttpClient();
		//���ӷ���������ѯGeographyUserServlet
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
					    data.putIntArray("userIds", userIds);//����ȡ�����û�ID���������data
					    msg.setData(data);//Ϊ��Ϣ��������
					    msg.what=100;//Ϊ��Ϣ���ñ�־��
					    handler.sendMessage(msg);//���þ��������Ϣ
					} catch (Exception e) {e.printStackTrace();}	
				}
				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					Toast.makeText(ShowBesideUser.this, "δ���ӵ�������", Toast.LENGTH_SHORT).show();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "�������", Toast.LENGTH_SHORT).show();
		}
	}
	//���ݲ�ѯ���ĺ���id��ѯ���ѱ�
	public void getUserByID(int ID ){
		AsyncHttpClient client= new AsyncHttpClient();
		//���ӷ�������������UserServlet
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
						    data.putString("Username", object.getString("Username"));//����ѯ���ĺ��������Ϣ������data��
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
						    msg.setData(data);//ΪҪ������Ϣ��������data
						    msg.what=101;//Ϊ��Ϣ���ñ�־��
						    handler.sendMessage(msg);
						}
					} catch (JSONException e) {e.printStackTrace();}	
				}
				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					Toast.makeText(ShowBesideUser.this, "δ���ӵ�������", Toast.LENGTH_SHORT).show();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(ShowBesideUser.this, "�������", Toast.LENGTH_SHORT).show();
		}
	}
	//���
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
					
					ugs.add(ug);//����ѯ����UserInfoGeography  ���뵽ugs����
					//��ȡ����
					getUserByID(ug.getUserID());//��ͨ��UserInfoGeography��userid��ѯ����
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
				users.add(u);//����ѯ���ĺ��Ѿ�����Ϣ ���� users����	
				//��users��������ͨ��SortByGoldnumber�ࣨʵ��Comparator�ӿڲ���дcompare��������
				//����sort����ʱ��ArrayList������ʵ��Commparator�ӿڵ���Ķ�����Ϊ����
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
	
	//����SortByGoldnumber�࣬ͨ����������
		private class SortByGoldnumber implements Comparator<User>{

			@Override
			public int compare(User u1, User u2) {
				// TODO Auto-generated method stub
				/*User u1=(User)object1;
				User u2=(User)object2;*/
				//users�������Ӹ��������򡣡���
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
		//������SortByDistance����������ĸ˳������
		private class SortByDistance implements Comparator<User>{

			@Override
			public int compare(User u1, User u2) {
				// TODO Auto-generated method stub
				/*User u1=(User)object1;
				User u2=(User)object2;*/
				//users�������Ӹ��������򡣡���
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

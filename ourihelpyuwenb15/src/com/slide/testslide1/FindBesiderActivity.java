package com.slide.testslide1;

import java.util.ArrayList;

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
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

//“签到”后  查找附近的人。。
public class FindBesiderActivity extends Activity {

	private TextView tvLocation;


	private String longClickUsername = null;
	private View viewBefore = null;
	private ListView lvBeside = null;
	private int geographyID;
	private ArrayList<User> users=new ArrayList<User>();
	private ArrayList<UserInfoGeography> ugs=new ArrayList<UserInfoGeography>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_find_besider);
		
		tvLocation=(TextView) findViewById(R.id.tvLocation);
		Bundle bundleData=this.getIntent().getExtras();
		geographyID=bundleData.getInt("geographyID");
		tvLocation.setText(bundleData.get("userLocation").toString().trim()+"附近的人");
		getUserBeside(geographyID);
		lvBeside = (ListView) findViewById(R.id.lvBesideUser);
		lvBeside.setOnItemClickListener(new MyItemClickListener());// 单击聊天
		lvBeside.setOnItemLongClickListener(new MyItemLongClickListener());// 弹出对话框选择
		lvBeside.setAdapter(new MyAdapter());
	}

	public void update(View v){
		lvBeside.setAdapter(new MyAdapter());
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.find_besider, menu);
		return true;
	}
	
	
	
	/*
	 * 单击附近的人Item监听者
	 */
	public class MyItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			String clickUsername = ((TextView) arg1).getText().toString()
					.trim();
			if (viewBefore != null) {
				viewBefore.setBackgroundColor(Color.rgb(255,255, 255));
			}
			arg1.setBackgroundColor(Color.GREEN);
			viewBefore = arg1;
			Toast.makeText(FindBesiderActivity.this, "和" + clickUsername + "聊天",
					Toast.LENGTH_SHORT).show();
		}
	}

	/*
	 * 长按附近的人Item
	 */
	public class MyItemLongClickListener implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			// TODO Auto-generated method stub
			longClickUsername = ((TextView) arg1).getText().toString().trim();

			new AlertDialog.Builder(FindBesiderActivity.this).setTitle("操作")
					.setMessage("用户" + longClickUsername)
					.setPositiveButton("加为好友", myListenerBeFriend)
					.setNegativeButton("查看资料", myListenerViewInfo).show();
			return false;
		}

	}

	private OnClickListener myListenerBeFriend = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			Toast.makeText(FindBesiderActivity.this,
					"成功加" + longClickUsername + "为好友！", Toast.LENGTH_SHORT)
					.show();
		}

	};
	private OnClickListener myListenerViewInfo = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			Toast.makeText(FindBesiderActivity.this, "查看" + longClickUsername + "的资料",
					Toast.LENGTH_SHORT).show();
		}

	};


	/*
	 * 向ListView添加查询结果，附近的人
	 */
	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return users.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Log.i("MyAdapter", "显示的位置:" + position);
			TextView tv = new TextView(getApplicationContext());
			tv.setTextSize(20);
			tv.setHeight(60);
			//UserInfoGeography ug = ugs.get(position);
			User u=users.get(position);
			//getUserByID(ug.getUserID());//给users赋值
			//tv.setText(gt.selectByID(ug.getUserID()).getName());
			//tv.setText("用户ID"+ug.getUserID());
			tv.setText(u.getUsername());
			return tv;
		}

	}
	public void getUserBeside(int gID ){
		AsyncHttpClient client= new AsyncHttpClient();
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
							//UserInfoGeography ug=new UserInfoGeography();
							//ug.setGeographyID(object.getInt("geographyId"));
							//ug.setUserID(object.getInt("userId"));
							//ugs.add(ug);
						    userIds[i]=object.getInt("userId");
						    
						}
						Message msg=new Message();
					    Bundle data=new Bundle();
					    data.clear();
					    data.putIntArray("userIds", userIds);
					    msg.setData(data);
					    msg.what=100;
					    handler.sendMessage(msg);
					} catch (Exception e) {e.printStackTrace();}	
				}
				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					Toast.makeText(FindBesiderActivity.this, "未连接到服务器", Toast.LENGTH_SHORT).show();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "网络错误", Toast.LENGTH_SHORT).show();
		}
	}

	public void getUserByID(int ID ){
		AsyncHttpClient client= new AsyncHttpClient();
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
						    data.putString("Username", object.getString("Username"));
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
						    msg.setData(data);
						    msg.what=101;
						    handler.sendMessage(msg);
						}
					} catch (JSONException e) {e.printStackTrace();}	
				}
				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					Toast.makeText(FindBesiderActivity.this, "未连接到服务器", Toast.LENGTH_SHORT).show();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(FindBesiderActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
		}
	}
	Handler handler =new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 100:
				String str="";
				int[] userIds=msg.getData().getIntArray("userIds");
				for(int i=0;i<userIds.length;i++){
					UserInfoGeography ug=new UserInfoGeography();
					ug.setGeographyID(geographyID);
					ug.setUserID(userIds[i]);
					str+="aaaa"+ug.getUserID();
					ugs.add(ug);
					getUserByID(ug.getUserID());
				}
				Toast.makeText(FindBesiderActivity.this, str, Toast.LENGTH_SHORT).show();
				
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
				users.add(u);
				break;
			default:
				break;
			}
		}
		
	};
}

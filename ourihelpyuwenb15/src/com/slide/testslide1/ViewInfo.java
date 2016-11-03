package com.slide.testslide1;

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

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ViewInfo extends Activity {

	protected static final int SHOW = 0;
	private String username;
	private TextView information;
	private TextView tvInfo1;
	private TextView tvInfo2;
	private Button btAddFriend;
	private ImageView ivImage;
	private User user=new User();
	private SharedPreferences sharedPreferences;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_view_info);
		tvInfo1=(TextView) findViewById(R.id.tvInfo1);
		tvInfo2=(TextView) findViewById(R.id.tvInfo2);
		information=(TextView) findViewById(R.id.information);
		ivImage=(ImageView) findViewById(R.id.imageView1);
		
		sharedPreferences = this.getSharedPreferences("test",MODE_WORLD_READABLE);
		Bundle data=this.getIntent().getExtras();
		//获取传来，与之聊天的用户名
		username=data.getString("clickUsername");
		final int userId=data.getInt("clickUserId");
		information.setText(username+"的资料");
		showInfo();
		
		btAddFriend=(Button)findViewById(R.id.btAddFriend);
		btAddFriend.setOnClickListener(new OnClickListener() {
		@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int friendid=userId;
				Log.d("sss", "sss1");
				int userid = Integer.parseInt(sharedPreferences.getString("userid", null));
				Log.d("sss", "sss2");
				AsyncHttpClient client= new AsyncHttpClient();
				String path=HttpUtil.URL+"UserFriendsServlet";
				RequestParams params=new RequestParams();
				Log.d("sss", "sss3");
				params.add("action", "add");
				params.add("userId",userid+"");
				params.add("friendId",friendid+"");
				Log.d("sss", "sss4");
				try {
					client.post(path, params, new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
							// TODO Auto-generated method stub
							String str=new String(responseBody);
							if(str.equals("1")){
								Log.d("sss", "sss5");
								Toast.makeText(ViewInfo.this, "添加成功", Toast.LENGTH_SHORT).show();
							}
							else{
								Toast.makeText(ViewInfo.this, "添加失败", Toast.LENGTH_SHORT).show();
							}
						}
						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							// TODO Auto-generated method stub
							Toast.makeText(ViewInfo.this, "未连接到服务器", Toast.LENGTH_SHORT).show();
						}
					});
				}catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(ViewInfo.this, "网络错误", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	
	void showInfo(){
		AsyncHttpClient client= new AsyncHttpClient();
		String path=HttpUtil.URL+"UserServlet";
		RequestParams params=new RequestParams();
		params.add("action", "queryByUsername");
		params.add("username", username+"");
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
							user=new User();
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
						    handler.sendEmptyMessage(SHOW);
						}
						
					} catch (JSONException e) {e.printStackTrace();}	
				}
				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					Toast.makeText(ViewInfo.this, "未连接到服务器", Toast.LENGTH_SHORT).show();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(ViewInfo.this, "网络错误", Toast.LENGTH_SHORT).show();
		}
	}
	
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case SHOW:
				String iauto=user.getiAutography();
				if(iauto.equals("")||iauto==null){iauto="这个人很懒，什么也没留下";}
				String str1="金豆数："+user.getGoldBeanNumber()
						+"\nEmail："+user.getEmail()+"\nQQ号："+user.getQqNumber()+"\n性别："+user.getSex()+"\n个性签名："+iauto;
				
				String str2="生日："+user.getBirthday()+"\n专业班级："+user.getMajorClass()+
				"\n宿舍："+user.getDormitoryNumber();
				tvInfo1.setText(str1);
				tvInfo2.setText(str2);
				showImage();
				break;
			case 2:
				Bitmap bm=(Bitmap) msg.obj;
				Bitmap output=ImageSetCorner.getRoundedCornerBitmap(bm, 2.0f);
				ivImage.setImageBitmap(output);
				break;
			default:
				break;
			}
		}
		
	};
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_info, menu);
		return true;
	}

	public void showImage(){
		new Thread(){
			public void run(){
				Bitmap bm=HttpUtil.getUserImage(user.getPhotoUrl());
				Message msg=new Message();
				msg.what=2;
				msg.obj=bm;
				handler.sendMessage(msg);
			} 
		}.start();
	}
}




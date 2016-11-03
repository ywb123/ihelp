package com.slide.testslide1;

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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Gold_GiveActivity extends Activity {
	private TextView txfriname;
	private EditText etnum;
	
	private String username;
	//�洢EditText�������ֵ
	private String  etnum1;
	private String  friname;
	private String gold;
	private Button bt_back;
	private String strImage="";
	private ImageView ivImage;
	private static final int CHANGE=0;
	private SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_gold__give);
		//��ȡsharedPreferencesʵ��
		sharedPreferences=this.getSharedPreferences("test",MODE_WORLD_READABLE);
		username=sharedPreferences.getString("name", null);
		bt_back=(Button)findViewById(R.id.bt_back);
		txfriname=(TextView)findViewById(R.id.friendsgoldbeanlevel);
		etnum=(EditText)findViewById(R.id.et_num);
		ivImage=(ImageView)findViewById(R.id.image);
		//��ȡ���ݹ����ĺ�������������
		Intent intent=getIntent();
		Bundle bundle=intent.getExtras();
		friname=bundle.getString("friendsname");
		gold=bundle.getString("gold");
		show();
		txfriname.setText("****�û���"+friname+"\n������"+gold);
		bt_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent();
				Bundle data=new Bundle();
				data.putInt("index", 4);
				intent.putExtras(data);
				intent.setClass(Gold_GiveActivity.this, MainActivity.class);
				startActivity(intent);
				finish();	
			}
		});
	}

	public void show(){
		AsyncHttpClient client= new AsyncHttpClient();
		String path=HttpUtil.URL+"UserServlet";
		RequestParams params=new RequestParams();
		params.add("action", "queryByUsername");
		params.add("username", friname);
		try {
			client.post(path, params, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					// TODO Auto-generated method stub
//					�ɷ�����ִ�в�ѯqueryByUsername��������֯��JSON�ַ����� List<User> 
					String result=new String(responseBody);
					try {
						JSONArray array = new JSONArray(result);
						int length = array.length();
						
						for (int i = 0; i < length; i++) {
							JSONObject object = array.getJSONObject(i);														
							strImage=object.getString("PhotoUrl");
							showImage();							
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
					Toast.makeText(Gold_GiveActivity.this, "δ���ӵ�������", Toast.LENGTH_SHORT).show();
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(Gold_GiveActivity.this, "�������", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void showImage(){
		new Thread(){
			public void run(){
				Bitmap bm=HttpUtil.getUserImage(strImage);
				Message msg=new Message();
				msg.what=2;
				msg.obj=bm;
				handler1.sendMessage(msg);
			} 
		}.start();
	}
	private Handler handler1=new Handler(){
		public void  handleMessage (Message msg){
			if(msg.what==2){
				Bitmap bm=(Bitmap) msg.obj;
				Bitmap output=ImageSetCorner.getRoundedCornerBitmap(bm, 2.0f);
				ivImage.setImageBitmap(output);
			}
		}
	};
	
	
	public int getGoldbeanid(String goldnum){
		int id=1;
		int num=Integer.parseInt(goldnum);
		if(num>50&&num<=100){
			id=2;		
		}else{
			if(num>100&&num<=200){
				id=3;
			}
		}		
		return id;
	}
	
	//�����͡���������ý𶹵��û��������ӣ����͵�user��������
		public void suregive(View v){
			//Ҫ���͵Ľ���
			etnum1=etnum.getText().toString().trim();			
			final AsyncHttpClient client= new AsyncHttpClient();
			String path=HttpUtil.URL+"GoldBeanServlet";
			RequestParams params=new RequestParams();
			params.add("action", "give");
			params.add("number", etnum1);
			params.add("username", username);
			params.add("giveUsername", friname);
			
			final String path1=HttpUtil.URL+"JpushServlet";
			final RequestParams params1=new RequestParams();
			params1.add("action", "sendgoldchange");
			params1.add("number",etnum1);
			params1.add("giveUsername", friname);
			
			String path2=HttpUtil.URL+"UserServlet";
			RequestParams params2=new RequestParams();
			params2.add("action", "queryByUsername");
			params2.add("username", friname);	
			//������UserServlet��ȡҪ���ͽ𶹺��ѵ�id
			try {
				client.post(path2, params2, new AsyncHttpResponseHandler() {				
					@Override
					public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
						// TODO Auto-generated method stub
						String result=new String(responseBody);
						try {
							//��ȡ"UserServlet"�����û�����ѯ�����ص�jason���ݣ���һ��person
							JSONArray array = new JSONArray(result);
							int length = array.length();
							for (int i = 0; i < length; i++) {
								//���ص�JSONO�����а�������ֶ�object
								JSONObject object = array.getJSONObject(i);
								int id=object.getInt("ID");
								Log.i("id",id+"");
								params1.add("tuisonguserid", id+"");
						     } 
					   }
							catch (Exception e) {
							// TODO: handle exception
								e.printStackTrace();
						}				
					}				
					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						// TODO Auto-generated method stub
						Toast.makeText(Gold_GiveActivity.this, "��ȡ���û���idʧ��", Toast.LENGTH_SHORT).show();
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(Gold_GiveActivity.this, "�������", Toast.LENGTH_SHORT).show();
			}
			//������GoldBeanServlet���Ӽ���
			try {
				client.post(path, params, new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
							String str=new String(responseBody);
							if(str.equals("SUCCESS")){
								
								//handler.sendEmptyMessage(CHANGE);
								gold=(Integer.parseInt(gold)+Integer.parseInt(etnum1))+"";
								str="****�û���"+friname+"\n������"+Integer.parseInt(gold);
								txfriname.setText(str);
								Toast.makeText(Gold_GiveActivity.this, "���ͳɹ�", Toast.LENGTH_SHORT).show();
								//������JpushServlet����֪ͨ
								client.post(path1,params1, new AsyncHttpResponseHandler() {									
									@Override
									public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
										// TODO Auto-generated method stub
										Toast.makeText(Gold_GiveActivity.this, "���ͳɹ�", Toast.LENGTH_SHORT).show();
									}									
									@Override
									public void onFailure(int statusCode, Header[] headers,
											byte[] responseBody, Throwable error) {
										// TODO Auto-generated method stub									
									}
								});
							}
						else
							Toast.makeText(Gold_GiveActivity.this, "����ʧ��", Toast.LENGTH_SHORT).show();
					
					}
					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						// TODO Auto-generated method stub
						Toast.makeText(Gold_GiveActivity.this, "δ���ӵ�������", Toast.LENGTH_SHORT).show();
						
					}
				});
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(Gold_GiveActivity.this, "�������", Toast.LENGTH_SHORT).show();
			}
		
			
		}
		Handler handler=new Handler(){
			public void handleMessage(Message mes){
				switch (mes.what) {
				case CHANGE:
					etnum.setText(Integer.parseInt(gold)+Integer.parseInt(etnum1));
					super.handleMessage(mes);
					finish();
					break;

				default:
					break;
				}
			}
		};
		//"����"�������ص����ﱦ�������棬ˢ����ʾ����
		public void returnshow(View v){
			Intent intent=new Intent();
			Bundle data=new Bundle();
			data.putInt("index", 4);
			intent.putExtras(data);
			intent.setClass(Gold_GiveActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
		}
		@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gold__give, menu);
		return true;
	}

}

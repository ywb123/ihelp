package com.slide.testslide1;


import java.util.Set;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.connect.auth.QQAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.ywb.uploadData.util.HttpUtil;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private EditText etloginname;
	private EditText etloginpassword;
	
	private CheckBox cbRemember;
	private String mAppid;
	private Tencent mTencent;
	public static QQAuth mQQAuth;
	private SharedPreferences sharedPreferences;  	  
    private SharedPreferences.Editor editor;  
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//����activity��ͷ��logo
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
		setContentView(R.layout.activity_login);
		//Jpush��ʼ��
		JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        
		//���sharedPreferencesʵ��
		sharedPreferences=this.getSharedPreferences("test", MODE_WORLD_READABLE);
		//MODE_WORLD_READABLEָ����sharedPreferences������ֻ�ܱ�����Ӧ�ó����ȡ������д
       //���editorʵ��
		editor=sharedPreferences.edit();
		
		etloginname=(EditText) findViewById(R.id.username);
		etloginpassword=(EditText) findViewById(R.id.password);
		cbRemember=(CheckBox) findViewById(R.id.remember);
		showback(LoginActivity.this);
	}
	
	//login��������¼��������֤�������û����Ƿ���ȷ
	
	public void login(View v){
		login();
	}
	public void login(){
		
		AsyncHttpClient client= new AsyncHttpClient();
		final String username=etloginname.getText().toString().trim();
		final String password=etloginpassword.getText().toString().trim();
		//���ñ���
		String path=HttpUtil.URL+"UserServlet";
		RequestParams params=new RequestParams();
		params.add("action", "queryByUsername");
		params.add("username", username);
		try {
			client.post(path, params, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
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
							JPushInterface.setAlias(getApplicationContext(), id+"", new TagAliasCallback() {
								
								@Override
								public void gotResult(int responseCode, String alias, Set<String> tags) {
									// TODO Auto-generated method stub
									Log.i("code", responseCode+"");
									Log.i("alias",alias);
								}
							});
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					// TODO Auto-generated method stub
					Toast.makeText(LoginActivity.this, "��������ʧ��", Toast.LENGTH_SHORT).show();					
				}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(LoginActivity.this, "�������", Toast.LENGTH_SHORT).show();
		}
		
		if(username.equals("")||password.equals("")){
			Toast.makeText(LoginActivity.this, "������û���Ϊ��", Toast.LENGTH_SHORT).show();
			
		}
		else{
			RequestParams params1=new RequestParams();
			params1.add("username", username);
			params1.add("password", password);
			String path1=HttpUtil.URL+"LoginServlet";
			try {
				client.post(path1, params1, new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
						// TODO Auto-generated method stub
						String str=new String(responseBody);
						
						if(str.equals("SUCCESS")){
							Toast.makeText(LoginActivity.this, "��¼�ɹ�", Toast.LENGTH_SHORT).show();
							editor.putString("name",username);
							editor.putString("password",password);
							//һ��Ҫ��editor�ύ �������Ž�name�����sharepreferences
							editor.commit();
							Intent intent=new Intent();
							intent.setClass(LoginActivity.this, MainActivity.class);
							startActivity(intent);
							if(cbRemember.isChecked()){
								save(LoginActivity.this, username, password,true);
								
							}else
							{
								save(LoginActivity.this, "", "",false);
							}
							finish();
						}
						else
							Toast.makeText(LoginActivity.this, "������û�������ȷ", Toast.LENGTH_SHORT).show();
					}
					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						// TODO Auto-generated method stub
						Toast.makeText(LoginActivity.this, "δ���ӵ�������", Toast.LENGTH_SHORT).show();
						
					}
				});
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(LoginActivity.this, "�������", Toast.LENGTH_SHORT).show();
			}
		
		}
		
	}
    //logintoregister��������ת��ע�����
	public void logintoregister(View v){
		Intent intent=new Intent(this,RegisterActivity.class);
    	startActivity(intent);
	}	
	
	/**
	 * ʵ��QQ��¼
	 * 
	 * @author gxl
	 * @param v
	 *            View�ؼ�
	 */
	public void qqlogin(View v) {
		mAppid = "101089715";
		final Context appcontext = LoginActivity.this.getApplicationContext();
		mQQAuth = QQAuth.createInstance(mAppid, appcontext);
		mTencent = Tencent.createInstance(mAppid, LoginActivity.this);
		if (!mQQAuth.isSessionValid()) {
			IUiListener listener = new BaseUiListener() {
				@Override
				protected void doComplete(JSONObject values) {
					// updateUserInfo();
					// updateLoginButton();
				}
			};
			// mQQAuth.login(this, "all", listener);
			// mTencent.loginWithOEM(this, "all",
			// listener,"10000144","10000144","xxxx");
			mTencent.login(this, "all", listener);
		}
	}

	private class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(Object response) {
			// Util.showResultDialog(MainActivity.this, response.toString(),
			// "��¼�ɹ�");
			doComplete((JSONObject) response);
			Toast.makeText(LoginActivity.this, "��¼�ɹ�", 1000).show();
			System.out.println("��¼�ɹ�");
			Intent intent = new Intent();
			intent.setClass(LoginActivity.this,
					MainActivity.class);
			startActivity(intent);
		}

		protected void doComplete(JSONObject values) {
			Toast.makeText(LoginActivity.this, "��¼�ɹ�", 1000).show();
			System.out.println("��¼�ɹ�");
		}

		@Override
		public void onError(UiError e) {
			// Util.toastMessage(MainActivity.this, "onError: " +
			// e.errorDetail);
			// Util.dismissDialog();

			Toast.makeText(LoginActivity.this, "��¼ʧ�ܣ�" + e.errorDetail, 1000)
					.show();
			System.out.println("��¼ʧ�ܣ�" + e.errorDetail);
		}

		@Override
		public void onCancel() {
			// Util.toastMessage(MainActivity.this, "onCancel: ");
			// Util.dismissDialog();
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }
    public void save(Context con,String username,String password,boolean ischecked){
		SharedPreferences sp=con.getSharedPreferences("Login", MODE_PRIVATE);
		Editor editor=sp.edit();
		editor.putString("username", username);
		editor.putString("password", password);
		editor.putBoolean("ischecked", ischecked);
		editor.commit();
	}
    public void showback(Context con){
		SharedPreferences sp=con.getSharedPreferences("Login", MODE_PRIVATE);
		String username=sp.getString("username", "");
		String password=sp.getString("password", "");
		boolean ischecked=sp.getBoolean("ischecked", false);
		etloginname.setText(username.toString().trim());
		etloginpassword.setText(password.toString().trim());
		cbRemember.setChecked(ischecked);
		if(username!=""&&password!=""&&ischecked)
			login();
	}
}

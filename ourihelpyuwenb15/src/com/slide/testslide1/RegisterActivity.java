package com.slide.testslide1;

import org.apache.http.Header;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ywb.uploadData.util.HttpUtil;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	private EditText etname;
	private EditText etpassword;
	private EditText etrepassword;
	private EditText etemail;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_register);
		
		etname=(EditText) findViewById(R.id.et_num);
		etpassword=(EditText) findViewById(R.id.editText2);
		etrepassword=(EditText) findViewById(R.id.editText3);
		etemail=(EditText) findViewById(R.id.editText4);
	}
	//返回登录函数。
	public void returntologin(View v){
		Intent intent=new Intent(this,LoginActivity.class);
    	startActivity(intent);
    	finish();
	}

	//sureregister函数：确认注册函数，将输入的用户名，密码，邮箱加入到数据库中。用户首次注册金豆数置20
    //其中有判断用户名是否已被注册
	@SuppressLint("NewApi")
	public void sureregister(View v){
		AsyncHttpClient client= new AsyncHttpClient();
		String username=etname.getText().toString().trim();
		String password=etpassword.getText().toString().trim();
		String repassword=etrepassword.getText().toString().trim();
		String email=etemail.getText().toString().trim();
		
		
		if(username.isEmpty()||password.isEmpty()||email.isEmpty())
		{
			Toast.makeText(this, "注册信息不能为空", Toast.LENGTH_SHORT).show();
		}
		else{
		
			if(!password.equals(repassword)){
				Toast.makeText(this, "两次输入密码不一致，请重新输入！", Toast.LENGTH_SHORT).show();
				etpassword.setText("");
				etrepassword.setText("");
				etpassword.requestFocus();
			}
			else{
				RequestParams params=new RequestParams();
				params.add("username", username);
				params.add("password", password);
				params.add("email", email);
				params.add("sex", " ");
				params.add("birthday", " ");
				params.add("dormitoryNumber", " ");
				params.add("majorClass", " ");
				params.add("iAutography", " ");
				params.add("photoUrl", "default.png");
				params.add("creditGrade", " ");
				params.add("qqNumber", " ");
				params.add("goldBeanNumber", "20");
				
				String path=HttpUtil.URL+"RegisterServlet";
				try {
					client.post(path, params, new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
							// TODO Auto-generated method stub
							String str=new String(responseBody);
							String str1=str.substring(0,7);
							if(str1.equals("SUCCESS")){
								Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
								int id=Integer.parseInt(str.substring(7));
								//用户注册初始位置设为1.南八快递点
								insertUserGeography(id,1);
							}
							else if(str1.equals("REPEAT0")){
								Toast.makeText(RegisterActivity.this, "用户名已被注册", Toast.LENGTH_SHORT).show();
								
							}
							else
								Toast.makeText(RegisterActivity.this, "注册失败"+str1, Toast.LENGTH_SHORT).show();
						}
						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							// TODO Auto-generated method stub
							Toast.makeText(RegisterActivity.this, "未连接到服务器", Toast.LENGTH_SHORT).show();
						}
					});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(RegisterActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
				}
				
					
			}
		}
	}
	
	
	public void insertUserGeography(int userID,int geographyID){
		AsyncHttpClient client= new AsyncHttpClient();
		String path=HttpUtil.URL+"GeographyUserServlet";
		RequestParams params=new RequestParams();
		params.add("action", "add");
		params.add("userId", userID+"");
		params.add("geographyId", geographyID+"");
		params.add("latitude", "30.443");
		params.add("longitude", "114.271");
		try {
			client.post(path, params, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					String result=new String(responseBody);
					if(result.equals("1")){
					}
				}
				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

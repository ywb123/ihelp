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
	//���ص�¼������
	public void returntologin(View v){
		Intent intent=new Intent(this,LoginActivity.class);
    	startActivity(intent);
    	finish();
	}

	//sureregister������ȷ��ע�ắ������������û��������룬������뵽���ݿ��С��û��״�ע�������20
    //�������ж��û����Ƿ��ѱ�ע��
	@SuppressLint("NewApi")
	public void sureregister(View v){
		AsyncHttpClient client= new AsyncHttpClient();
		String username=etname.getText().toString().trim();
		String password=etpassword.getText().toString().trim();
		String repassword=etrepassword.getText().toString().trim();
		String email=etemail.getText().toString().trim();
		
		
		if(username.isEmpty()||password.isEmpty()||email.isEmpty())
		{
			Toast.makeText(this, "ע����Ϣ����Ϊ��", Toast.LENGTH_SHORT).show();
		}
		else{
		
			if(!password.equals(repassword)){
				Toast.makeText(this, "�����������벻һ�£����������룡", Toast.LENGTH_SHORT).show();
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
								Toast.makeText(RegisterActivity.this, "ע��ɹ�", Toast.LENGTH_SHORT).show();
								int id=Integer.parseInt(str.substring(7));
								//�û�ע���ʼλ����Ϊ1.�ϰ˿�ݵ�
								insertUserGeography(id,1);
							}
							else if(str1.equals("REPEAT0")){
								Toast.makeText(RegisterActivity.this, "�û����ѱ�ע��", Toast.LENGTH_SHORT).show();
								
							}
							else
								Toast.makeText(RegisterActivity.this, "ע��ʧ��"+str1, Toast.LENGTH_SHORT).show();
						}
						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							// TODO Auto-generated method stub
							Toast.makeText(RegisterActivity.this, "δ���ӵ�������", Toast.LENGTH_SHORT).show();
						}
					});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(RegisterActivity.this, "�������", Toast.LENGTH_SHORT).show();
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

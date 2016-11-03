package com.slide.testslide1;

import java.util.ArrayList;

import org.apache.http.Header;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ywb.uploadData.util.HttpUtil;

import android.os.Bundle;
import android.os.SystemClock;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Treecave_AttributeActivity extends Activity {
	private EditText etMessage;
	private EditText etKeyword;
	private TextView keywordshow;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_treecave__attribute);
		
		etMessage=(EditText) findViewById(R.id.etMessage);
		etKeyword=(EditText) findViewById(R.id.etKeyword);
		keywordshow=(TextView) findViewById(R.id.keywordshow);
	}

	public void back(View v){
		Intent intent=new Intent(this,MainActivity.class);
		Bundle extras=new Bundle();
		extras.putInt("index", 3);
		intent.putExtras(extras);
		startActivity(intent);
		finish();
	}
	public void submit(View v){
		AsyncHttpClient client= new AsyncHttpClient();
		String strMessage=etMessage.getText().toString().trim();
		String strKeyword=etKeyword.getText().toString().trim();
		Time t=new Time("GMT+8");
		t.setToNow();
		String strCommentTime=t.year+"_"+t.month+"_"+t.monthDay+""+t.hour+":"+t.minute+":"+t.second;
		if(strMessage.isEmpty()||strKeyword.isEmpty())
		{
			Toast.makeText(this, "发帖信息不能为空", Toast.LENGTH_SHORT).show();
		}
		else{
			RequestParams params=new RequestParams();
			params.add("message", strMessage);
			params.add("keyword", strKeyword);
			params.add("commentTime", strCommentTime);
			params.add("commentNumber", "0");
			params.add("praiseNumber", "0");
			params.add("action", "insert");;
			
			String path=HttpUtil.URL+"TreeCaveServlet";
			try {
				client.post(path, params, new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
						// TODO Auto-generated method stub
						String str=new String(responseBody);
						if(str.equals("1")){
							Intent intent=new Intent(Treecave_AttributeActivity.this,MainActivity.class);
							Bundle extras=new Bundle();
							extras.putInt("index", 3);
							intent.putExtras(extras);
							startActivity(intent);
							finish();
							Toast.makeText(Treecave_AttributeActivity.this, "发表成功", Toast.LENGTH_SHORT).show();
						}
						else
							Toast.makeText(Treecave_AttributeActivity.this, "发表失败", Toast.LENGTH_SHORT).show();
					}
					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						// TODO Auto-generated method stub
						Toast.makeText(Treecave_AttributeActivity.this, "未连接到服务器", Toast.LENGTH_SHORT).show();
					}
				});
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(Treecave_AttributeActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
			}
			
				
		
		}
	}
	
	public void keycut(View v){
		//Java实现逆向最大匹配中文分词算法
		ArrayList<String> keyword;
		String input=etMessage.getText().toString().trim();// 要匹配的字符	
		//String input = "太好了，今天是星期六啊";  // 要匹配的字符	
		keyword=new Split(input).start();
		String str="";
		//System.out.println(keyword);
		for(int i=0;i<keyword.size();i++){
			System.out.println(keyword.get(i));
			str+=keyword.get(i)+"   ";
			//keywordshow.setText((CharSequence) keyword);
		}
		keywordshow.setText(str);
	     
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.treecave__attribute, menu);
		return true;
	}

}

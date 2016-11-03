package com.slide.testslide1;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ywb.uploadData.util.HttpUtil;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
//带聊天界面
public class ChatWithUserActivity extends Activity {
	String chatusername;//与之聊天的用户名
	private SharedPreferences sharedPreferences;
	private String username;//登录用户名
	String contString;//要发送的消息
	
	private Button mBtnSend;
	private Button mBtnBack;
	private EditText mEditTextContent;
	private ListView mListView;
	private TextView mTextViewUser;
	//为mListView设置的适配器
	private ChatMsgViewAdapter mAdapter;
	//消息类的集合
	private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();
    private String[]msgArray = new String[]{"你在吗？", "在呢？", "给我取个快递呗～～", "说，取货号"};
    
    private String[]dataArray = new String[]{"2014-5-15 21:00", "2014-5-15 21:05", 
    										"2014-5-15 21:10", "2014-5-15 21:15"}; 
    private final static int COUNT = 4;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//设置activity无头部logo
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
		setContentView(R.layout.activity_chat_with_user);
		//获取登录用户名
		sharedPreferences=this.getSharedPreferences("test",MODE_WORLD_READABLE);
		username=sharedPreferences.getString("name", null);
		Bundle data=this.getIntent().getExtras();
		//获取传来，与之聊天的用户名
		chatusername=data.getString("clickUsername");
		Toast.makeText(ChatWithUserActivity.this,chatusername,
				Toast.LENGTH_SHORT).show();
		mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
		mTextViewUser=(TextView)findViewById(R.id.et_chatuser);
		//初始化listview
        initView();
        //初始化其中数据
        initData();
	}
   
    //初始化最开始的数据
    public void initData()
    {
    	for(int i = 0; i < COUNT; i++)
    	{
    		//新建消息类
    		ChatMsgEntity entity = new ChatMsgEntity();
    		//为消息设置时间
    		entity.setDate(dataArray[i]);
    		if (i % 2 == 0)
    		{
    			entity.setName(chatusername);
    			entity.setMsgType(true);//MsgType(true)左边，接收方
    		}else{
    			entity.setName(username);
    			entity.setMsgType(false);//MsgType(true)右边，发送方
    		}
    		//设置消息内容
    		entity.setText(msgArray[i]);
    		//将此entity添加进消息类的集合mDataArrays
    		mDataArrays.add(entity);
    	}
        //利用这些数据新建适配器
    	mAdapter = new ChatMsgViewAdapter(this, mDataArrays);
    	//给mListView设置适配器
		mListView.setAdapter(mAdapter);		
    }
    //初始化界面，并在其中，点击“发送”按钮，消息显示在上方，并连接服务器Jpush发送通知
	public void initView()
    {
		mTextViewUser.setText(chatusername);
    	mListView = (ListView) findViewById(R.id.listview);
    	mBtnSend = (Button) findViewById(R.id.btn_send);
    	//mBtnSend.setOnClickListener(this);
    	mBtnSend.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				contString = mEditTextContent.getText().toString();
				if (contString.length() > 0)
				{
					ChatMsgEntity entity = new ChatMsgEntity();
					entity.setDate(getDate());
					entity.setName(username);
					entity.setMsgType(false);
					entity.setText(contString);
					//添加进listview
					mDataArrays.add(entity);
					mAdapter.notifyDataSetChanged();
					//将输入消息置空
					mEditTextContent.setText("");
					//在当前listview后显示输入信息
					mListView.setSelection(mListView.getCount() - 1);
				}
				//连接请求至服务器。。。将消息推送至相应好友
				final AsyncHttpClient client= new AsyncHttpClient();
				String path=HttpUtil.URL+"UserServlet";
				RequestParams params=new RequestParams();
				params.add("action", "queryByUsername");
				params.add("username", chatusername);
				
				final String path1=HttpUtil.URL+"JpushServlet";
				final RequestParams params1=new RequestParams();
				params1.add("action", "sendchatmessage");
				String message=mEditTextContent.getText().toString().trim();
				params1.add("message", contString);
				params1.add("username",username);
				//请求至UserServlet，获取要聊天好友的id,并作为参数存进JpushServlet中
				try {
					client.post(path, params, new AsyncHttpResponseHandler() {
						
						@Override
						public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
							// TODO Auto-generated method stub
							String result=new String(responseBody);//服务器返回数据responseBody
							try {
								//解析服务器传回的Jason数据，获取其中的id
								JSONArray array = new JSONArray(result);//将result重构造为Jason
								for(int i=0;i<array.length();i++){
									//array为Jason数组，其中包含多个Jason字段
									JSONObject object = array.getJSONObject(i);
									int id=object.getInt("ID");
									//Log.i("id",id+"");
//									Toast.makeText(ChatWithUserActivity.this,id+"aaaaa",
//											Toast.LENGTH_SHORT).show();
									params1.add("tuisonguserid", id+"");
//									Toast.makeText(ChatWithUserActivity.this, "成功获取要聊天id！～～", Toast.LENGTH_SHORT).show();
									//请求连接至JpushServlet，发送通知
									try {
										client.post(path1, params1, new AsyncHttpResponseHandler() {
											
											@Override
											public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
												// TODO Auto-generated method stub
												Toast.makeText(ChatWithUserActivity.this, "发送成功！～～", Toast.LENGTH_SHORT).show();
											}
											
											@Override
											public void onFailure(int statusCode, Header[] headers,
													byte[] responseBody, Throwable error) {
												// TODO Auto-generated method stub
												Toast.makeText(ChatWithUserActivity.this, "未连接到服务器", Toast.LENGTH_SHORT).show();
												
											}
										});
									} catch (Exception e) {
										e.printStackTrace();
										Toast.makeText(ChatWithUserActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						
						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							Toast.makeText(ChatWithUserActivity.this, "获取该用户名id失败", Toast.LENGTH_SHORT).show();				
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(ChatWithUserActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
				}
			}
		});
    	mBtnBack = (Button) findViewById(R.id.btn_back);
    	//mBtnBack.setOnClickListener(this);
    	mBtnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
    	
    }

    private String getDate() {
        Calendar c = Calendar.getInstance();

        String year = String.valueOf(c.get(Calendar.YEAR));
        String month = String.valueOf(c.get(Calendar.MONTH)+1);
        String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        String mins = String.valueOf(c.get(Calendar.MINUTE));
               
        StringBuffer sbBuffer = new StringBuffer();
        sbBuffer.append(year + "-" + month + "-" + day + " " + hour + ":" + mins); 
        						       						
        return sbBuffer.toString();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat_with_user, menu);
		return true;
	}

}

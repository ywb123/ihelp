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
//���������
public class ChatWithUserActivity extends Activity {
	String chatusername;//��֮������û���
	private SharedPreferences sharedPreferences;
	private String username;//��¼�û���
	String contString;//Ҫ���͵���Ϣ
	
	private Button mBtnSend;
	private Button mBtnBack;
	private EditText mEditTextContent;
	private ListView mListView;
	private TextView mTextViewUser;
	//ΪmListView���õ�������
	private ChatMsgViewAdapter mAdapter;
	//��Ϣ��ļ���
	private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();
    private String[]msgArray = new String[]{"������", "���أ�", "����ȡ������¡���", "˵��ȡ����"};
    
    private String[]dataArray = new String[]{"2014-5-15 21:00", "2014-5-15 21:05", 
    										"2014-5-15 21:10", "2014-5-15 21:15"}; 
    private final static int COUNT = 4;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//����activity��ͷ��logo
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
		setContentView(R.layout.activity_chat_with_user);
		//��ȡ��¼�û���
		sharedPreferences=this.getSharedPreferences("test",MODE_WORLD_READABLE);
		username=sharedPreferences.getString("name", null);
		Bundle data=this.getIntent().getExtras();
		//��ȡ��������֮������û���
		chatusername=data.getString("clickUsername");
		Toast.makeText(ChatWithUserActivity.this,chatusername,
				Toast.LENGTH_SHORT).show();
		mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
		mTextViewUser=(TextView)findViewById(R.id.et_chatuser);
		//��ʼ��listview
        initView();
        //��ʼ����������
        initData();
	}
   
    //��ʼ���ʼ������
    public void initData()
    {
    	for(int i = 0; i < COUNT; i++)
    	{
    		//�½���Ϣ��
    		ChatMsgEntity entity = new ChatMsgEntity();
    		//Ϊ��Ϣ����ʱ��
    		entity.setDate(dataArray[i]);
    		if (i % 2 == 0)
    		{
    			entity.setName(chatusername);
    			entity.setMsgType(true);//MsgType(true)��ߣ����շ�
    		}else{
    			entity.setName(username);
    			entity.setMsgType(false);//MsgType(true)�ұߣ����ͷ�
    		}
    		//������Ϣ����
    		entity.setText(msgArray[i]);
    		//����entity��ӽ���Ϣ��ļ���mDataArrays
    		mDataArrays.add(entity);
    	}
        //������Щ�����½�������
    	mAdapter = new ChatMsgViewAdapter(this, mDataArrays);
    	//��mListView����������
		mListView.setAdapter(mAdapter);		
    }
    //��ʼ�����棬�������У���������͡���ť����Ϣ��ʾ���Ϸ��������ӷ�����Jpush����֪ͨ
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
					//��ӽ�listview
					mDataArrays.add(entity);
					mAdapter.notifyDataSetChanged();
					//��������Ϣ�ÿ�
					mEditTextContent.setText("");
					//�ڵ�ǰlistview����ʾ������Ϣ
					mListView.setSelection(mListView.getCount() - 1);
				}
				//��������������������������Ϣ��������Ӧ����
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
				//������UserServlet����ȡҪ������ѵ�id,����Ϊ�������JpushServlet��
				try {
					client.post(path, params, new AsyncHttpResponseHandler() {
						
						@Override
						public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
							// TODO Auto-generated method stub
							String result=new String(responseBody);//��������������responseBody
							try {
								//�������������ص�Jason���ݣ���ȡ���е�id
								JSONArray array = new JSONArray(result);//��result�ع���ΪJason
								for(int i=0;i<array.length();i++){
									//arrayΪJason���飬���а������Jason�ֶ�
									JSONObject object = array.getJSONObject(i);
									int id=object.getInt("ID");
									//Log.i("id",id+"");
//									Toast.makeText(ChatWithUserActivity.this,id+"aaaaa",
//											Toast.LENGTH_SHORT).show();
									params1.add("tuisonguserid", id+"");
//									Toast.makeText(ChatWithUserActivity.this, "�ɹ���ȡҪ����id������", Toast.LENGTH_SHORT).show();
									//����������JpushServlet������֪ͨ
									try {
										client.post(path1, params1, new AsyncHttpResponseHandler() {
											
											@Override
											public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
												// TODO Auto-generated method stub
												Toast.makeText(ChatWithUserActivity.this, "���ͳɹ�������", Toast.LENGTH_SHORT).show();
											}
											
											@Override
											public void onFailure(int statusCode, Header[] headers,
													byte[] responseBody, Throwable error) {
												// TODO Auto-generated method stub
												Toast.makeText(ChatWithUserActivity.this, "δ���ӵ�������", Toast.LENGTH_SHORT).show();
												
											}
										});
									} catch (Exception e) {
										e.printStackTrace();
										Toast.makeText(ChatWithUserActivity.this, "�������", Toast.LENGTH_SHORT).show();
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						
						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							Toast.makeText(ChatWithUserActivity.this, "��ȡ���û���idʧ��", Toast.LENGTH_SHORT).show();				
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(ChatWithUserActivity.this, "�������", Toast.LENGTH_SHORT).show();
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

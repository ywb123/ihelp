package com.slide.testslide1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ywb.uploadData.util.HttpUtil;

import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

//ʹMainActivity�̳�FragmentActivity
public class MainActivity extends FragmentActivity implements OnTouchListener{
	/** 
     * ������ʾ������menuʱ����ָ������Ҫ�ﵽ���ٶȡ� 
     */  
    public static final int SNAP_VELOCITY = 200;  
  
    /** 
     * ��Ļ���ֵ�� 
     */  
    private int screenWidth;  
  
    /** 
     * menu�����Ի����������Ե��ֵ��menu���ֵĿ��������marginLeft�����ֵ֮�󣬲����ټ��١� 
     */  
    private int leftEdge;  
  
    /** 
     * menu�����Ի��������ұ�Ե��ֵ��Ϊ0����marginLeft����0֮�󣬲������ӡ� 
     */  
    private int rightEdge = 0;  
  
    /** 
     * menu��ȫ��ʾʱ������content�Ŀ��ֵ�� 
     */  
    private int menuPadding =400;
  
    /** 
     * �����ݵĲ��֡� 
     */  
    private View content;  
    private View content1;  
  
    /** 
     * menu�Ĳ��֡�   //centent��menuʵ�ʶ���һ��Activity�У������ǲ�ͬ��view
     */  
    private View menu;  
  
    /** 
     * menu���ֵĲ�����ͨ���˲���������leftMargin��ֵ�� 
     */  
    private LinearLayout.LayoutParams menuParams;  
  
    /** 
     * ��¼��ָ����ʱ�ĺ����ꡣ 
     */  
    private float xDown;  
  
    /** 
     * ��¼��ָ�ƶ�ʱ�ĺ����ꡣ 
     */  
    private float xMove;  
  
    /** 
     * ��¼�ֻ�̧��ʱ�ĺ����ꡣ 
     */  
    private float xUp;  
  
    /** 
     * menu��ǰ����ʾ�������ء�ֻ����ȫ��ʾ������menuʱ�Ż���Ĵ�ֵ�����������д�ֵ��Ч�� 
     */  
    private boolean isMenuVisible;  
  
    /** 
     * ���ڼ�����ָ�������ٶȡ� 
     */  
    private VelocityTracker mVelocityTracker;  
    
    /*
     * ����FragmentManager��FragmentTransaction���������ڲ���fragment
     */
    private FragmentManager fm;
	private FragmentTransaction ft;
	
    private ListView lv_menu;
    private TextView tv_item;
    private String title[] = {"��ҳ","�ҵ�����","�ҵĺ���","����","�ﱦ��","ǩ��"};
    private int[] imageId=new int[]{R.drawable.shouye,R.drawable.ziliao,R.drawable.haoyou1,
    		R.drawable.shudong1,R.drawable.doudou,R.drawable.qiandao};

    private View viewBefore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_navigation);
		
		initValues();  
        content.setOnTouchListener(this);  //Ϊcontent����ontouchlistener�����¼�
        
        //������λ����
        startService(new Intent(MainActivity.this,LocationService.class));
        SharedPreferences sp=this.getSharedPreferences("test",MODE_WORLD_READABLE);
		String username=sp.getString("name", "");
        shareInfo(username);
        
	}

	/** 
     * ��ʼ��һЩ�ؼ������ݡ�������ȡ��Ļ�Ŀ�ȣ���content�����������ÿ�ȣ���menu�����������ÿ�Ⱥ�ƫ�ƾ���ȡ� 
     */  
    private void initValues() {
    	content=(LinearLayout)findViewById(R.id.content);
    	//content1=(LinearLayout)findViewById(R.id.content1);
		
    	menu=(LinearLayout)findViewById(R.id.menu);
    	lv_menu=(ListView)findViewById(R.id.lv_menu);
    	List<Map<String,Object>>listItems=new ArrayList<Map<String,Object>>();
    	for(int i=0;i<imageId.length;i++){
    		Map<String,Object> map=new HashMap<String, Object>();
    		map.put("image",imageId[i]);
    		map.put("title",title[i]);
    		listItems.add(map);
    	}
    	lv_menu.setAdapter(new SimpleAdapter(this, listItems, R.layout.lv_menu_item, new String[]{"title","image"}, new int[]{R.id.title,R.id.listimage}));
    	
    	Bundle data=getIntent().getExtras();
    	
    	if(data != null){
    		int n=data.getInt("index", 0);
    		Log.d("kkk", "index"+n);
    		getFragment(n);
    	}else{Log.d("kkk", "index");getFragment(0);}
    	lv_menu.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				tv_item=(TextView)view.findViewById(R.id.title);
				if(viewBefore!=null){
					((TextView) viewBefore).setTextColor(Color.rgb(0X99, 0X33, 0xCC));
				}
				tv_item.setTextColor(Color.rgb(0X99, 0XCC, 0x33));
				viewBefore=tv_item;
				scrollToContent();
				getFragment(position);
				Toast.makeText(MainActivity.this, title[position], 1).show();
			}
		});
    	
        WindowManager window = (WindowManager) getSystemService(Context.WINDOW_SERVICE);  
        screenWidth = window.getDefaultDisplay().getWidth();  
        content = findViewById(R.id.content);  
        menu = findViewById(R.id.menu);  
        menuParams = (LinearLayout.LayoutParams) menu.getLayoutParams();  
        // ��menu�Ŀ������Ϊ��Ļ��ȼ�ȥmenuPadding  
        menuParams.width = screenWidth - menuPadding;  
        // ���Ե��ֵ��ֵΪmenu��ȵĸ���  
        leftEdge = -menuParams.width;  
        // menu��leftMargin����Ϊ���Ե��ֵ��������ʼ��ʱmenu�ͱ�Ϊ���ɼ�  
        menuParams.leftMargin = leftEdge;  
        // ��content�Ŀ������Ϊ��Ļ���  
        content.getLayoutParams().width = screenWidth;  
    }  
	public void getFragment(int position){
		//��fm,ft��ʼ��
		fm=getSupportFragmentManager();
		ft=fm.beginTransaction();//���FragmentTransactionʵ��
		if(title[position].equals("�ﱦ��")){
			//ͨ��ft����replace�������滻��ʾ�ﱦ������
			ft.replace(R.id.content, new Fragment1_maingoldbean());
								
		}
		else if(title[position].equals("��ҳ")){
			ft.replace(R.id.content, new Fragment0_index());
						
		}
		else if(title[position].equals("�ҵĺ���")){
			ft.replace(R.id.content, new Fragment2_mainmyfriends());
						
		}
		else if(title[position].equals("�ҵ�����")){
			ft.replace(R.id.content, new Fragment3_mainmyinfo());
						
		}
		else if(title[position].equals("����")){
			ft.replace(R.id.content, new Fragment4_mainmytreecave());
					
		}
		else if(title[position].equals("ǩ��")){
			ft.replace(R.id.content, new Fragment4_mainGeography());
					
		}
		//һ��Ҫ�ύ
		ft.commit();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		menu.add("�л��˺�").setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				save(MainActivity.this,"","",false);
				startActivity(new Intent(MainActivity.this,LoginActivity.class));
				finish();
				return true;
			}
		});
		return true;
	}

	public void save(Context con,String username,String password,boolean ischecked){
		SharedPreferences sp=con.getSharedPreferences("Login", MODE_PRIVATE);
		Editor editor=sp.edit();
		editor.putString("username", username);
		editor.putString("password", password);
		editor.putBoolean("ischecked", ischecked);
		editor.commit();
	}

	
	void shareInfo(String username){
		AsyncHttpClient client= new AsyncHttpClient();
		String path=HttpUtil.URL+"UserServlet";
		RequestParams params=new RequestParams();
		params.add("action", "queryByUsername");
		params.add("username", username);
		
		Log.d("kkk", username);
		
		try {
			client.post(path, params, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					// TODO Auto-generated method stub
					Log.d("kkk", "111");
					String result=new String(responseBody);
					
					try {
						
						JSONArray array = new JSONArray(result);
						
						int length = array.length();
						for (int i = 0; i < length; i++) {
							JSONObject object = array.getJSONObject(i);
							SharedPreferences sp1=MainActivity.this.getSharedPreferences("share",MODE_WORLD_WRITEABLE);
							Editor editor=sp1.edit();
							editor.clear();
							editor.putInt("userid", object.getInt("ID"));
							Log.d("kkk", "**"+object.getInt("ID"));
							editor.putString("username", object.getString("Username"));
							
							editor.putString("qqNumber", object.getString("QQNumber"));
							editor.putString("iAutography", object.getString("IAutography"));
							editor.putString("majorClass", object.getString("MajorClass"));
							editor.putString("dormitoryNumber", object.getString("DormitoryNumber"));
							editor.putString("email", object.getString("Email"));
							editor.putString("sex", object.getString("Sex"));
							editor.putInt("goldBeanNumber", object.getInt("GoldBeanNumber"));
							
							editor.commit();
							
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						Log.d("kkk", "error2");
						e.printStackTrace();
					}	
					
				}
				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					// TODO Auto-generated method stub
					Log.d("kkk", "error2");
					Toast.makeText(MainActivity.this, "δ���ӵ�������", Toast.LENGTH_SHORT).show();
					
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d("kkk", "error2");
			e.printStackTrace();
			Toast.makeText(MainActivity.this, "�������", Toast.LENGTH_SHORT).show();
		}
	}

	 private long firstTime=0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
    	if(keyCode==KeyEvent.KEYCODE_BACK){
			long secondTime=System.currentTimeMillis();
			if(secondTime-firstTime>1000){
				Toast.makeText(this, "�ٰ�һ���˳�", Toast.LENGTH_LONG).show();
				firstTime=secondTime;
			}else{
				System.exit(0);
				
			}
			return true;
		}
    	else
    		return super.onKeyDown(keyCode, event);
		

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		createVelocityTracker(event);  
        switch (event.getAction()) {  
        case MotionEvent.ACTION_DOWN:  
            // ��ָ����ʱ����¼����ʱ�ĺ�����  
            xDown = event.getRawX();  
            break;  
        case MotionEvent.ACTION_MOVE:  
            // ��ָ�ƶ�ʱ���ԱȰ���ʱ�ĺ����꣬������ƶ��ľ��룬������menu��leftMarginֵ���Ӷ���ʾ������menu  
            xMove = event.getRawX();  
            int distanceX = (int) (xMove - xDown);  
            if (isMenuVisible) {  
                menuParams.leftMargin = distanceX;  
            } else {  
                menuParams.leftMargin = leftEdge + distanceX;  
            }  
            if (menuParams.leftMargin < leftEdge) {  
                menuParams.leftMargin = leftEdge;  
            } else if (menuParams.leftMargin > rightEdge) {  
                menuParams.leftMargin = rightEdge;  
            }  
            menu.setLayoutParams(menuParams);  
            break;  
        case MotionEvent.ACTION_UP:  
            // ��ָ̧��ʱ�������жϵ�ǰ���Ƶ���ͼ���Ӷ������ǹ�����menu���棬���ǹ�����content����  
            xUp = event.getRawX();  
            if (wantToShowMenu()) {  
                if (shouldScrollToMenu()) {  
                    scrollToMenu();  
                } else {  
                    scrollToContent();  
                }  
            } else if (wantToShowContent()) {  
                if (shouldScrollToContent()) {  
                    scrollToContent();  
                } else {  
                    scrollToMenu();  
                }  
            }  
            recycleVelocityTracker();  
            break;  
        }  
		return true;
	}
	/** 
     * �жϵ�ǰ���Ƶ���ͼ�ǲ�������ʾcontent�������ָ�ƶ��ľ����Ǹ������ҵ�ǰmenu�ǿɼ��ģ�����Ϊ��ǰ��������Ҫ��ʾcontent�� 
     *  
     * @return ��ǰ��������ʾcontent����true�����򷵻�false�� 
     */  
    private boolean wantToShowContent() {  
        return xUp - xDown < 0 && isMenuVisible;  
    }  
  
    /** 
     * �жϵ�ǰ���Ƶ���ͼ�ǲ�������ʾmenu�������ָ�ƶ��ľ������������ҵ�ǰmenu�ǲ��ɼ��ģ�����Ϊ��ǰ��������Ҫ��ʾmenu�� 
     *  
     * @return ��ǰ��������ʾmenu����true�����򷵻�false�� 
     */  
    private boolean wantToShowMenu() {  
        return xUp - xDown > 0 && !isMenuVisible;  
    }  
  
    /** 
     * �ж��Ƿ�Ӧ�ù�����menuչʾ�����������ָ�ƶ����������Ļ��1/2��������ָ�ƶ��ٶȴ���SNAP_VELOCITY�� 
     * ����ΪӦ�ù�����menuչʾ������ 
     *  
     * @return ���Ӧ�ù�����menuչʾ��������true�����򷵻�false�� 
     */  
    private boolean shouldScrollToMenu() {  
        return xUp - xDown > screenWidth / 2 || getScrollVelocity() > SNAP_VELOCITY;  
    }  
  
    /** 
     * �ж��Ƿ�Ӧ�ù�����contentչʾ�����������ָ�ƶ��������menuPadding������Ļ��1/2�� 
     * ������ָ�ƶ��ٶȴ���SNAP_VELOCITY�� ����ΪӦ�ù�����contentչʾ������ 
     *  
     * @return ���Ӧ�ù�����contentչʾ��������true�����򷵻�false�� 
     */  
    private boolean shouldScrollToContent() {  
        return xDown - xUp + menuPadding > screenWidth / 2 || getScrollVelocity() > SNAP_VELOCITY;  
    }  
  
    /** 
     * ����Ļ������menu���棬�����ٶ��趨Ϊ30. 
     */  
    private void scrollToMenu() {  
        new ScrollTask().execute(30);  
    }  
  
    /** 
     * ����Ļ������content���棬�����ٶ��趨Ϊ-30. 
     */  
    private void scrollToContent() {  
        new ScrollTask().execute(-30);  
    }  
    
    /** 
     * ����VelocityTracker���󣬲�������content����Ļ����¼����뵽VelocityTracker���С� 
     *  
     * @param event 
     *            content����Ļ����¼� 
     */  
    private void createVelocityTracker(MotionEvent event) {  
        if (mVelocityTracker == null) {  
            mVelocityTracker = VelocityTracker.obtain();  
        }  
        mVelocityTracker.addMovement(event);  
    }  
  
    /** 
     * ��ȡ��ָ��content���滬�����ٶȡ� 
     *  
     * @return �����ٶȣ���ÿ�����ƶ��˶�������ֵΪ��λ�� 
     */  
    private int getScrollVelocity() {  
        mVelocityTracker.computeCurrentVelocity(1000);  
        int velocity = (int) mVelocityTracker.getXVelocity();  
        return Math.abs(velocity);  
    }  
    /** 
     * ����VelocityTracker���� 
     */  
    private void recycleVelocityTracker() {  
        mVelocityTracker.recycle();  
        mVelocityTracker = null;  
    }  
    class ScrollTask extends AsyncTask<Integer, Integer, Integer> {  
    	  
        @Override  
        protected Integer doInBackground(Integer... speed) {  
            int leftMargin = menuParams.leftMargin;  
            // ���ݴ�����ٶ����������棬������������߽���ұ߽�ʱ������ѭ����  
            while (true) {  
                leftMargin = leftMargin + speed[0];  
                if (leftMargin > rightEdge) {  
                    leftMargin = rightEdge;  
                    break;  
                }  
                if (leftMargin < leftEdge) {  
                    leftMargin = leftEdge;  
                    break;  
                }  
                publishProgress(leftMargin);  
                // Ϊ��Ҫ�й���Ч��������ÿ��ѭ��ʹ�߳�˯��20���룬�������۲��ܹ���������������  
                sleep(20);  
            }  
            if (speed[0] > 0) {  
                isMenuVisible = true;  
            } else {  
                isMenuVisible = false;  
            }  
            return leftMargin;  
        }  
  
        protected void onProgressUpdate(Integer... leftMargin) {  
            menuParams.leftMargin = leftMargin[0];  
            menu.setLayoutParams(menuParams);  
        }  
  
        protected void onPostExecute(Integer leftMargin) {  
            menuParams.leftMargin = leftMargin;  
            menu.setLayoutParams(menuParams);  
        }  
    }  
  
    /** 
     * ʹ��ǰ�߳�˯��ָ���ĺ������� 
     *  
     * @param millis 
     *            ָ����ǰ�߳�˯�߶�ã��Ժ���Ϊ��λ 
     */  
    private void sleep(long millis) {  
        try {  
            Thread.sleep(millis);  
        } catch (InterruptedException e) {  
            e.printStackTrace();  
        }  
    }  

}

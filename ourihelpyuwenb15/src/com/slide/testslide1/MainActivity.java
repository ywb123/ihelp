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

//使MainActivity继承FragmentActivity
public class MainActivity extends FragmentActivity implements OnTouchListener{
	/** 
     * 滚动显示和隐藏menu时，手指滑动需要达到的速度。 
     */  
    public static final int SNAP_VELOCITY = 200;  
  
    /** 
     * 屏幕宽度值。 
     */  
    private int screenWidth;  
  
    /** 
     * menu最多可以滑动到的左边缘。值由menu布局的宽度来定，marginLeft到达此值之后，不能再减少。 
     */  
    private int leftEdge;  
  
    /** 
     * menu最多可以滑动到的右边缘。值恒为0，即marginLeft到达0之后，不能增加。 
     */  
    private int rightEdge = 0;  
  
    /** 
     * menu完全显示时，留给content的宽度值。 
     */  
    private int menuPadding =400;
  
    /** 
     * 主内容的布局。 
     */  
    private View content;  
    private View content1;  
  
    /** 
     * menu的布局。   //centent与menu实际都在一个Activity中，他们是不同的view
     */  
    private View menu;  
  
    /** 
     * menu布局的参数，通过此参数来更改leftMargin的值。 
     */  
    private LinearLayout.LayoutParams menuParams;  
  
    /** 
     * 记录手指按下时的横坐标。 
     */  
    private float xDown;  
  
    /** 
     * 记录手指移动时的横坐标。 
     */  
    private float xMove;  
  
    /** 
     * 记录手机抬起时的横坐标。 
     */  
    private float xUp;  
  
    /** 
     * menu当前是显示还是隐藏。只有完全显示或隐藏menu时才会更改此值，滑动过程中此值无效。 
     */  
    private boolean isMenuVisible;  
  
    /** 
     * 用于计算手指滑动的速度。 
     */  
    private VelocityTracker mVelocityTracker;  
    
    /*
     * 声明FragmentManager，FragmentTransaction变量。用于操作fragment
     */
    private FragmentManager fm;
	private FragmentTransaction ft;
	
    private ListView lv_menu;
    private TextView tv_item;
    private String title[] = {"首页","我的资料","我的好友","树洞","帮宝宝","签到"};
    private int[] imageId=new int[]{R.drawable.shouye,R.drawable.ziliao,R.drawable.haoyou1,
    		R.drawable.shudong1,R.drawable.doudou,R.drawable.qiandao};

    private View viewBefore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_navigation);
		
		initValues();  
        content.setOnTouchListener(this);  //为content设置ontouchlistener监听事件
        
        //开启定位服务
        startService(new Intent(MainActivity.this,LocationService.class));
        SharedPreferences sp=this.getSharedPreferences("test",MODE_WORLD_READABLE);
		String username=sp.getString("name", "");
        shareInfo(username);
        
	}

	/** 
     * 初始化一些关键性数据。包括获取屏幕的宽度，给content布局重新设置宽度，给menu布局重新设置宽度和偏移距离等。 
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
        // 将menu的宽度设置为屏幕宽度减去menuPadding  
        menuParams.width = screenWidth - menuPadding;  
        // 左边缘的值赋值为menu宽度的负数  
        leftEdge = -menuParams.width;  
        // menu的leftMargin设置为左边缘的值，这样初始化时menu就变为不可见  
        menuParams.leftMargin = leftEdge;  
        // 将content的宽度设置为屏幕宽度  
        content.getLayoutParams().width = screenWidth;  
    }  
	public void getFragment(int position){
		//对fm,ft初始化
		fm=getSupportFragmentManager();
		ft=fm.beginTransaction();//获得FragmentTransaction实例
		if(title[position].equals("帮宝宝")){
			//通过ft调用replace方法，替换显示帮宝宝界面
			ft.replace(R.id.content, new Fragment1_maingoldbean());
								
		}
		else if(title[position].equals("首页")){
			ft.replace(R.id.content, new Fragment0_index());
						
		}
		else if(title[position].equals("我的好友")){
			ft.replace(R.id.content, new Fragment2_mainmyfriends());
						
		}
		else if(title[position].equals("我的资料")){
			ft.replace(R.id.content, new Fragment3_mainmyinfo());
						
		}
		else if(title[position].equals("树洞")){
			ft.replace(R.id.content, new Fragment4_mainmytreecave());
					
		}
		else if(title[position].equals("签到")){
			ft.replace(R.id.content, new Fragment4_mainGeography());
					
		}
		//一定要提交
		ft.commit();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		menu.add("切换账号").setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
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
					Toast.makeText(MainActivity.this, "未连接到服务器", Toast.LENGTH_SHORT).show();
					
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d("kkk", "error2");
			e.printStackTrace();
			Toast.makeText(MainActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
		}
	}

	 private long firstTime=0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
    	if(keyCode==KeyEvent.KEYCODE_BACK){
			long secondTime=System.currentTimeMillis();
			if(secondTime-firstTime>1000){
				Toast.makeText(this, "再按一次退出", Toast.LENGTH_LONG).show();
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
            // 手指按下时，记录按下时的横坐标  
            xDown = event.getRawX();  
            break;  
        case MotionEvent.ACTION_MOVE:  
            // 手指移动时，对比按下时的横坐标，计算出移动的距离，来调整menu的leftMargin值，从而显示和隐藏menu  
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
            // 手指抬起时，进行判断当前手势的意图，从而决定是滚动到menu界面，还是滚动到content界面  
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
     * 判断当前手势的意图是不是想显示content。如果手指移动的距离是负数，且当前menu是可见的，则认为当前手势是想要显示content。 
     *  
     * @return 当前手势想显示content返回true，否则返回false。 
     */  
    private boolean wantToShowContent() {  
        return xUp - xDown < 0 && isMenuVisible;  
    }  
  
    /** 
     * 判断当前手势的意图是不是想显示menu。如果手指移动的距离是正数，且当前menu是不可见的，则认为当前手势是想要显示menu。 
     *  
     * @return 当前手势想显示menu返回true，否则返回false。 
     */  
    private boolean wantToShowMenu() {  
        return xUp - xDown > 0 && !isMenuVisible;  
    }  
  
    /** 
     * 判断是否应该滚动将menu展示出来。如果手指移动距离大于屏幕的1/2，或者手指移动速度大于SNAP_VELOCITY， 
     * 就认为应该滚动将menu展示出来。 
     *  
     * @return 如果应该滚动将menu展示出来返回true，否则返回false。 
     */  
    private boolean shouldScrollToMenu() {  
        return xUp - xDown > screenWidth / 2 || getScrollVelocity() > SNAP_VELOCITY;  
    }  
  
    /** 
     * 判断是否应该滚动将content展示出来。如果手指移动距离加上menuPadding大于屏幕的1/2， 
     * 或者手指移动速度大于SNAP_VELOCITY， 就认为应该滚动将content展示出来。 
     *  
     * @return 如果应该滚动将content展示出来返回true，否则返回false。 
     */  
    private boolean shouldScrollToContent() {  
        return xDown - xUp + menuPadding > screenWidth / 2 || getScrollVelocity() > SNAP_VELOCITY;  
    }  
  
    /** 
     * 将屏幕滚动到menu界面，滚动速度设定为30. 
     */  
    private void scrollToMenu() {  
        new ScrollTask().execute(30);  
    }  
  
    /** 
     * 将屏幕滚动到content界面，滚动速度设定为-30. 
     */  
    private void scrollToContent() {  
        new ScrollTask().execute(-30);  
    }  
    
    /** 
     * 创建VelocityTracker对象，并将触摸content界面的滑动事件加入到VelocityTracker当中。 
     *  
     * @param event 
     *            content界面的滑动事件 
     */  
    private void createVelocityTracker(MotionEvent event) {  
        if (mVelocityTracker == null) {  
            mVelocityTracker = VelocityTracker.obtain();  
        }  
        mVelocityTracker.addMovement(event);  
    }  
  
    /** 
     * 获取手指在content界面滑动的速度。 
     *  
     * @return 滑动速度，以每秒钟移动了多少像素值为单位。 
     */  
    private int getScrollVelocity() {  
        mVelocityTracker.computeCurrentVelocity(1000);  
        int velocity = (int) mVelocityTracker.getXVelocity();  
        return Math.abs(velocity);  
    }  
    /** 
     * 回收VelocityTracker对象。 
     */  
    private void recycleVelocityTracker() {  
        mVelocityTracker.recycle();  
        mVelocityTracker = null;  
    }  
    class ScrollTask extends AsyncTask<Integer, Integer, Integer> {  
    	  
        @Override  
        protected Integer doInBackground(Integer... speed) {  
            int leftMargin = menuParams.leftMargin;  
            // 根据传入的速度来滚动界面，当滚动到达左边界或右边界时，跳出循环。  
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
                // 为了要有滚动效果产生，每次循环使线程睡眠20毫秒，这样肉眼才能够看到滚动动画。  
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
     * 使当前线程睡眠指定的毫秒数。 
     *  
     * @param millis 
     *            指定当前线程睡眠多久，以毫秒为单位 
     */  
    private void sleep(long millis) {  
        try {  
            Thread.sleep(millis);  
        } catch (InterruptedException e) {  
            e.printStackTrace();  
        }  
    }  

}

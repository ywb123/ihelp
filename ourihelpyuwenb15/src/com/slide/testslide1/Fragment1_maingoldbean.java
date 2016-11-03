package com.slide.testslide1;

import java.util.ArrayList;
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
import domin.goldbeanlevel;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
//fragment包导入一定得是v4下的，
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
/*
 * 创建内容页fragment1,其view来源xml:fragment_maingoldbean
 */
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class Fragment1_maingoldbean extends Fragment{
	private TextView txshowperson;
	private List<User> persons=new ArrayList<User>();
	private List<goldbeanlevel> golds;
	//显示好友列表的lsmyfriends
	private ListView  lsmyfriends;
	//private String friname;
	private String showname;
	private String showgoldbean;
    private int goldbeanlevelid;
	private String level;//等级
	private String strImage="";
	
	private String name;
	private TextView friendsname;
	private TextView friendsgoldbean;
	private TextView friendsdoldbeanlevel;
	private  ImageButton  bt_givegoldbean;
	private Button bt_back;
	//private Context context;
	private FragmentManager fm;
	//声明sharepreferences对象
	private SharedPreferences sharedPreferences;  
	private View v;
	private Button button;
	private ImageView imglevel;
	private ImageView ivImage;
	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//使用inflater布局泵调用其inflater方法为view赋值,
		v=inflater.inflate(R.layout.fragment1_maingoldbean, null);
		//context=this;
		//获得sharedPreferences实例，系统将自动创建名为”test“的xml文件，
		sharedPreferences=getActivity().getSharedPreferences("test", getActivity().MODE_WORLD_READABLE);
		
		ivImage=(ImageView) v.findViewById(R.id.image);
		button=(Button)v.findViewById(R.id.showfriends);
		txshowperson=(TextView)v.findViewById(R.id.myinfo);
		lsmyfriends=(ListView)v.findViewById(R.id.list);
		bt_back=(Button)v.findViewById(R.id.bt_back);
		/*
		 * show函数初始显示用户名等基本
		 */
		show();
		bt_back.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getActivity(), MainActivity.class);
						startActivity(intent);		
					}
				});
//		“查看好友”按钮：从服务器端获取到数据库中所有user
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) { 
//				lsmyfriends.setAdapter(null);
				
				AsyncHttpClient client= new AsyncHttpClient();
				String path=HttpUtil.URL+"UserServlet";
				RequestParams params=new RequestParams();
				params.add("action", "queryAllUser");
				try {
					client.post(path, params, new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
							// TODO Auto-generated method stub
//							responseBody服务器传回的数据，jason格式
							String result=new String(responseBody);
							try {
//							     解析服务器传回的jason数据，person的arraylist数组  
								JSONArray array = new JSONArray(result);
								int length = array.length();
								persons=new ArrayList<User>();
								for (int i = 0; i < length; i++) {
									JSONObject object = array.getJSONObject(i);
									User p=new User();
									Log.d("aaa",object.getString("Username"));
									int levelId=0;
									levelId=getGoldbeanid(object.getInt("GoldBeanNumber")+"");
									p.setEmail(object.getString("Email"));
									p.setGoldBeanNumber(object.getInt("GoldBeanNumber"));
									
									p.setId(object.getInt("ID"));
									p.setUsername(object.getString("Username"));
									p.setPassword(object.getString("Password"));
									persons.add(p);
									//lsmyfriends.removeAllViews();
									lsmyfriends.setAdapter(new MyAdapter());
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}	
						}
						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							// TODO Auto-generated method stub
							Toast.makeText(getActivity(), "未连接到服务器", Toast.LENGTH_SHORT).show();
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();
				}
				
			    
//			    lsmyfriends=null;
			}
		});
		return v;
	}
	
	public void show(){
		//获取保存在sharedPreferences中的name
		showname=sharedPreferences.getString("name",null);
		
		AsyncHttpClient client= new AsyncHttpClient();
		String path=HttpUtil.URL+"UserServlet";
		RequestParams params=new RequestParams();
		params.add("action", "queryByUsername");
		params.add("username", showname);
		try {
			client.post(path, params, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					// TODO Auto-generated method stub
//					由服务器执行查询queryByUsername，返回组织成JSON字符串的 List<User> 
					String result=new String(responseBody);
					try {
						JSONArray array = new JSONArray(result);
						int length = array.length();
						String mainStr="";
						for (int i = 0; i < length; i++) {
							JSONObject object = array.getJSONObject(i);
							String str="用户名"+object.getString("Username")+
									"\n金豆数："+object.getInt("GoldBeanNumber")
									+"\n等级："+getGoldbeanlevel(object.getInt("GoldBeanNumber"));
							Log.d("aaa", str);
							strImage=object.getString("PhotoUrl");
							showImage();
							mainStr=str;
						}
						txshowperson.setText(mainStr);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
					
				}
				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					// TODO Auto-generated method stub
					Toast.makeText(getActivity(), "未连接到服务器", Toast.LENGTH_SHORT).show();
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();
		}
		
		
		
	    
	}
	public void showImage(){
		new Thread(){
			public void run(){
				Bitmap bm=HttpUtil.getUserImage(strImage);
				Message msg=new Message();
				msg.what=2;
				msg.obj=bm;
				handler.sendMessage(msg);
			} 
		}.start();
	}
	private Handler handler=new Handler(){
		public void  handleMessage (Message msg){
			if(msg.what==2){
				Bitmap bm=(Bitmap) msg.obj;
				Bitmap output=ImageSetCorner.getRoundedCornerBitmap(bm, 2.0f);
				ivImage.setImageBitmap(output);
			}
		}
	};
	
	
	/*
	 * getGoldbeanid函数：判断用户金豆数判断其等级。
	 */
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
/*
 * 直接根据金豆数判断返回等级名称
 */
	public String getGoldbeanlevel(int goldnum){
		String level="";
		int num=goldnum;
		if(num>=20&&num<=35)
		{
			level="小红帽";
		}
		else if(num>35&&num<=50){
			level="警察叔叔";	
		}else{
			if(num>50&&num<=100){
				level="好人雷锋";	
			}
		}		
		return level;
	}
/*
 * 金豆数对应不同等级图片
 */
	public int getlevelImage(int goldnum){
		int imglevel=0;
		int num=goldnum;
		if(num>=20&&num<=35)
		{
			imglevel=R.drawable.xiaohongmao1;
		}
		else if(num>35&&num<=50){
			imglevel=R.drawable.jingcha1;
		}else{
			if(num>50&&num<=100){
				imglevel=R.drawable.leifeng4;
			}
		}		
		return imglevel;
	}

	
	public Fragment1_maingoldbean() {
		super();
	}

	
	
	//在整个GoldbeanActivity类中再新建MyAdapter类,私有类
	public class MyAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return persons.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return persons.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@SuppressLint("NewApi")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view=null;//新建空的view对象，最后返回它
			//若函数中参数convertView为空，则将view置为listview的显示样式xml文件，
			if(convertView==null){
				view=LayoutInflater.from(getActivity()).inflate(R.layout.showfriendslist,null);
			}else {
				view=convertView;  //若不为空，直接将参数赋值给view
			}
			//这里获取自定义视图showfriendslist中的控件时，与其他地方获取不同，要通过新建的view对象获取，  
			friendsname=(TextView)view.findViewById(R.id.friendsname);
			friendsname.setText(persons.get(position).getUsername());
			friendsgoldbean=(TextView)view.findViewById(R.id.friendsgoldbean);
			friendsgoldbean.setText("金豆数： "+persons.get(position).getGoldBeanNumber());
			friendsdoldbeanlevel=(TextView)view.findViewById(R.id.friendsgoldbeanlevel);
			friendsdoldbeanlevel.setText("等级："+getGoldbeanlevel(persons.get(position).getGoldBeanNumber()));
			imglevel=(ImageView)view.findViewById(R.id.imglevel);
			
		    /*Bitmap bitmap=BitmapFactory.decodeResource(getResources(),getlevelImage(persons.get(position).getGoldBeanNumber()));
		    Bitmap output=ImageSetCorner.getRoundedCornerBitmap(bitmap, 2.0f);
		    imglevel.setImageBitmap(output);*/
			imglevel.setImageResource(getlevelImage(persons.get(position).getGoldBeanNumber()));
			bt_givegoldbean=(ImageButton)view.findViewById(R.id.givegoldbean);
			//使用setTag保存当前position，即button当前所在位置
			bt_givegoldbean.setTag(position+"");
			//为button按钮设置监听事件
			bt_givegoldbean.setOnClickListener(new OnClickListener() {			
				@Override
				public void onClick(View v) {
					//通过当前v获取该行的position
					//得到button的tag
					int index = Integer.parseInt(v.getTag().toString());
					Intent intent=new Intent(getActivity(),Gold_GiveActivity.class);
					Bundle bundle=new Bundle();
					String friname;
					int num;
					num=persons.get(index).getGoldBeanNumber();
					friname=persons.get(index).getUsername();
					bundle.putCharSequence("friendsname",friname);
					bundle.putCharSequence("gold",num+"");
					intent.putExtras(bundle);
					startActivity(intent);
				}
			});
			return view;		
		}	
	}
}

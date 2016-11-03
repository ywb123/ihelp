package com.slide.testslide1;

import java.util.ArrayList;
import java.util.Random;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.image.setcorner.ImageSetCorner;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ywb.uploadData.util.HttpUtil;

import domin.User;
import domin.UserInfoGeography;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class GetPacketActivity extends Activity {

	
	protected static final int GETUSER = 0;
	protected static final int GETUSERSID = 1;
	// 地图管理器
	private BMapManager mBMapMan = null;
	// 地图视图
	private MapView mMapView = null;
	private ArrayList<GeoPoint> points;
	private ArrayList<GeoPoint> userPoints;
	
	private MyOverlayUser myOverlayUser;
	
	private ArrayList<UserInfoGeography> ugs;
	
	double loclatitude;
	double loclongitude;
	private Spinner spinner;
	private LinearLayout llmain;
	
	private int[][] gID={{1,2},{3,0},{4,0}};
	private String[] actLocs;
	private ArrayList<Integer> UsersId;
	private User tapUser=new User();
	private Bitmap bm=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBMapMan = new BMapManager(getApplication());
		mBMapMan.init("QfICf3VW3yvEgG8mxr7nXRID", null);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_get_packet);
		loclatitude=30.444;
		loclongitude=114.268;
		llmain=(LinearLayout) findViewById(R.id.llmain);
		spinner=(Spinner) findViewById(R.id.spinner);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				TextView tv=(TextView) arg1;
				tv.setTextColor(Color.WHITE);
				setMapView(arg2+1);
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				Toast.makeText(GetPacketActivity.this,"nothing" , Toast.LENGTH_LONG).show();
			}
		});
		Bundle data=this.getIntent().getExtras();
		int index=data.getInt("index");//1,2,3分别表示“取快递”等三项事件
		spinner.setSelection(index-1);
		//setMapView(index);//根据不同事件，设置显示地图的不同部分
		//因为在spinner刚开始加载的时候会调用一次onitemclick函数
	}
	
	
	//获取用户位置
	public void getUserLocation(){
		
		AsyncHttpClient client= new AsyncHttpClient();
		//连接服务器，查询GeographyUserServlet
		String path=HttpUtil.URL+"GeographyUserServlet";
		RequestParams params=new RequestParams();
		params.add("action", "query");
		try {
			client.post(path, params, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					String result=new String(responseBody);
					try {
						JSONArray array = new JSONArray(result);
						int length = array.length();
						ugs=new ArrayList<UserInfoGeography>();
						for (int i = 0; i < length; i++) {
							JSONObject object = array.getJSONObject(i);
							UserInfoGeography ug=new UserInfoGeography();
							ug.setGeographyID(object.getInt("geographyId"));
							ug.setUserID(object.getInt("userId"));
							ug.setLatitude(object.getDouble("latitude"));
							ug.setLongitude(object.getDouble("longitude"));
							ugs.add(ug);
						}
						Log.d("ccc", "111");
						handler.sendEmptyMessage(GETUSERSID);
					} catch (Exception e) {e.printStackTrace();}	
				}
				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					Toast.makeText(GetPacketActivity.this, "未连接到服务器", Toast.LENGTH_SHORT).show();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "网络错误", Toast.LENGTH_SHORT).show();
		}
	}
	
	Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GETUSERSID:
				userPoints=new ArrayList<GeoPoint>();
				loclatitude=30.443;
				loclongitude=114.271;
				UsersId=new ArrayList<Integer>();
				Log.d("aaa", ugs.size()+"");
				for(int i=0;i<ugs.size();i++){
					GeoPoint point = new GeoPoint((int) (ugs.get(i).getLatitude()* 1E6),(int) (ugs.get(i).getLongitude() * 1E6));
					/*Random random=new Random();
					int k1=random.nextInt(100);
					k1=k1-50;
					int k2=random.nextInt(100);
					k2=k2-50;
					GeoPoint point = new GeoPoint((int) (loclatitude * 1E6+k1 * 100),(int) (loclongitude * 1E6+k2 * 100));
					Log.d("aaa", ugs.get(i).getUserID()+"");
*/					userPoints.add(point);
					UsersId.add(ugs.get(i).getUserID());
				}
				
				mMapView = (MapView) findViewById(R.id.bmapsView);
				mMapView.setBuiltInZoomControls(true);
				// 设置启用内置的缩放控件
				MapController mMapController = mMapView.getController();
				// 得到mMapView的控制权,可以用它控制和驱动平移和缩放
				GeoPoint point = new GeoPoint((int) (loclatitude * 1E6),
						(int) (loclongitude * 1E6));
				// 用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
				mMapController.setCenter(point);// 设置地图中心点
				mMapController.setZoom(17);// 设置地图zoom级别
				
				//标识小人的位置
				Drawable mark=getResources().getDrawable(R.drawable.renwu1);
				myOverlayUser=new MyOverlayUser(mark,mMapView);
				for(int i=0;i<userPoints.size();i++){
					OverlayItem item=new OverlayItem(userPoints.get(i), "item", "item");
					myOverlayUser.addItem(item);
				}
				mMapView.getOverlays().add(myOverlayUser);
				mMapView.refresh();
				
				break;

			case GETUSER:
				
				View view = getLayoutInflater().inflate(R.layout.pop_text, null); 
				
	            TextView textName = (TextView) view.findViewById(R.id.textName);
	            TextView textBean = (TextView) view.findViewById(R.id.textBean);
	            TextView textIAuto = (TextView) view.findViewById(R.id.textIAuto);
	            TextView textEmail = (TextView) view.findViewById(R.id.textEmail);
	            
	            String sex=tapUser.getSex();
	            Drawable img1 = null;
	            
	            if(sex.equals("男")){
	            	sex="帅哥";
	            	img1=getResources().getDrawable(R.drawable.touxm);
	            }
	            else if(sex.equals("女")){
	            	sex="美女";
	            	img1=getResources().getDrawable(R.drawable.touxn);
	            }
	            
	           /* Bitmap output=ImageSetCorner.getRoundedCornerBitmap(bm, 2.0f);
			    img_selecttoux.setImageBitmap(output);*/
	            img1=new BitmapDrawable(bm);
	         // 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
            	img1.setBounds(0, 0, img1.getMinimumWidth(), img1.getMinimumHeight());
            	textName.setCompoundDrawables(img1, null, null, null); //设置左图标
	            String iauto=tapUser.getiAutography();
	            if(iauto.equals("")||iauto==null){iauto="这个人很懒，什么也没留下";}
	            
	            textName.setText(tapUser.getUsername()+sex); 
	            textBean.setText("金豆数:"+tapUser.getGoldBeanNumber()+"个");
	            textEmail.setText("Email:"+tapUser.getEmail()); 
	            textIAuto.setText(iauto); 
	            
	            PopupOverlay pop=new PopupOverlay(mMapView, null);
	            OverlayItem item=myOverlayUser.getItem(msg.getData().getInt("arg0"));
	            pop.showPopup(view, item.getPoint(), 10);
	            view.setOnLongClickListener(new OnLongClickListener() {
					@Override
					public boolean onLongClick(View arg0) {
						new AlertDialog.Builder(GetPacketActivity.this).setTitle("选择操作")
						.setMessage(tapUser.getUsername())
						.setPositiveButton("与ta聊天", myListenerChat)
						.setNegativeButton("查看资料", myListenerViewInfo).show();
						return false;
					}
				});
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
		
	};
	
	private android.content.DialogInterface.OnClickListener myListenerChat = new android.content.DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			//跳转至ChatWithUserActivity，并将要聊天的好友用户名clickUsername作参数传递
			Bundle data=new Bundle();
			data.putString("clickUsername", tapUser.getUsername());
			Intent intent=new Intent(GetPacketActivity.this,ChatWithUserActivity.class);
			intent.putExtras(data);
			startActivity(intent);
			Toast.makeText(GetPacketActivity.this, "与"+tapUser.getUsername()+"聊天", Toast.LENGTH_SHORT).show();
		}

	};
	private android.content.DialogInterface.OnClickListener myListenerViewInfo = new android.content.DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			//查看资料代码
			Bundle data=new Bundle();
			data.putString("clickUsername", tapUser.getUsername());
			data.putInt("clickUserId", tapUser.getId());
			Intent intent=new Intent(GetPacketActivity.this,ViewInfo.class);
			intent.putExtras(data);
			startActivity(intent);
			Toast.makeText(GetPacketActivity.this, "查看"+tapUser.getUsername()+"的资料", Toast.LENGTH_SHORT).show();
		}
	};
	//在地图上设置标识
	public void setMapView(int index){
		points=new ArrayList<GeoPoint>();
		switch (index) {//index传来值可能1，2，3
		case 1:
			actLocs=new String[2];
			//设置为取快递位置
			loclatitude=30.443;
			loclongitude=114.271;
			//南八
			double mlatitude1=30.443;
			double mlongitude1=114.271;
			GeoPoint point1 = new GeoPoint((int) (mlatitude1 * 1E6),(int) (mlongitude1 * 1E6));
			
			actLocs[0]="南八快递点";
			//校门口
			double mlatitude2=30.4463;
			double mlongitude2=114.2688;
			GeoPoint point2 = new GeoPoint((int) (mlatitude2 * 1E6),(int) (mlongitude2 * 1E6));
			actLocs[1]="校门口快递点";
			points.add(point1);
			points.add(point2);
			break;
		case 2:
			actLocs=new String[1];
			//设置为带外卖位置
			loclatitude=30.4470;
			loclongitude=114.267;
			//园艺街
			double mlatitude3=30.4470;
			double mlongitude3=114.267;
			GeoPoint point3 = new GeoPoint((int) (mlatitude3 * 1E6),
					(int) (mlongitude3 * 1E6));
			actLocs[0]="园艺街";
			points.add(point3);
			break;
		case 3:
			actLocs=new String[1];
			//设置为校医院位置
			loclatitude=30.4488;
			loclongitude=114.271;
			//校医院
			double mlatitude4=30.4488;
			double mlongitude4=114.271;
			GeoPoint point4 = new GeoPoint((int) (mlatitude4 * 1E6),
					(int) (mlongitude4 * 1E6));
			actLocs[0]="校医院";
			points.add(point4);
			break;
		default:
			break;
		}
		
		mMapView = (MapView) findViewById(R.id.bmapsView);
		mMapView.setBuiltInZoomControls(true);
		// 设置启用内置的缩放控件
		MapController mMapController = mMapView.getController();
		// 得到mMapView的控制权,可以用它控制和驱动平移和缩放
		GeoPoint point = new GeoPoint((int) (loclatitude * 1E6),
				(int) (loclongitude * 1E6));
		// 用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
		mMapController.setCenter(point);// 设置地图中心点
		mMapController.setZoom(17);// 设置地图zoom级别
		
		//标识小人的位置
		Drawable mark=getResources().getDrawable(R.drawable.location3);
		MyOverlayPlace myOverlay=new MyOverlayPlace(mark,mMapView);
		
		for(int i=0;i<points.size();i++){
			OverlayItem item=new OverlayItem(points.get(i), "item", "item");
			myOverlay.addItem(item);
		}
		
		mMapView.getOverlays().clear();
		mMapView.getOverlays().add(myOverlay);
		mMapView.refresh();
		getUserLocation();
		//清空llmain后多余的数据
		if(llmain.getChildCount()>2){
			llmain.removeViews(2,llmain.getChildCount()-2);
		}
		for(int i=0;i<points.size();i++){
			//tvChild为按钮
			Button tvChild=new Button(GetPacketActivity.this);
			tvChild.setText(actLocs[i]);//actLocs[i]数组中存放的是选择不同活动对应的 地点
			//tvChild.setBackgroundColor(Color.GRAY);
			tvChild.setTextColor(Color.WHITE);
			//设置按钮透明度
			tvChild.getBackground().setAlpha(100);
			tvChild.setHeight(80);
			tvChild.setTextSize(25);
			tvChild.setBottom(10);
			final int index1=i;
			final int index2=index;
			tvChild.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int lat=points.get(index1).getLatitudeE6();
					int lon=points.get(index1).getLatitudeE6();
					Bundle data=new Bundle();
					data.putInt("lat", lat);
					data.putInt("lon", lon);
					//地点id。作为GeographyUser表的外键
					data.putInt("geographyID", gID[index2-1][index1]);
					data.putString("loc",actLocs[index1]);
					//跳转至ShowBesideUser，显示不同地点所在好友。并使用Bundle传值过去
					Intent intent=new Intent(GetPacketActivity.this,ShowBesideUser.class);
					intent.putExtras(data);
					startActivity(intent);
					
				}
			});
			llmain.addView(tvChild);
			
		}
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.get_packet, menu);
		return true;
	}
	class MyOverlayPlace extends ItemizedOverlay<OverlayItem>{

		@Override
		public boolean onTap(GeoPoint arg0, MapView arg1) {
			return super.onTap(arg0, arg1);
		}

		@Override
		protected boolean onTap(int arg0) {
			Toast.makeText(GetPacketActivity.this,actLocs[arg0] , Toast.LENGTH_LONG).show();
			return true;
		}

		public MyOverlayPlace(Drawable arg0, MapView arg1) {
			super(arg0, arg1);
		}
		
	}

	class MyOverlayUser extends ItemizedOverlay<OverlayItem>{

		@Override
		public boolean onTap(GeoPoint arg0, MapView arg1) {
			return super.onTap(arg0, arg1);
		}

		@Override
		protected boolean onTap(int arg0) {
            getUser(UsersId.get(arg0),arg0);
			return true;
		}

		public MyOverlayUser(Drawable arg0, MapView arg1) {
			super(arg0, arg1);
		}
		
	}
	
	//获取用户信息
	public void getUser(int id,int arg0){
		
		final Message msg=new Message();
		Bundle data=new Bundle();
		data.clear();
		data.putInt("arg0", arg0);
		msg.setData(data);
		msg.what=GETUSER;
		
		
		AsyncHttpClient client= new AsyncHttpClient();
		String path=HttpUtil.URL+"UserServlet";
		RequestParams params=new RequestParams();
		params.add("action", "queryById");
		params.add("id", id+"");
		try {
			client.post(path, params, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					String result=new String(responseBody);
					try {
						JSONArray array = new JSONArray(result);
						int length = array.length();
						for (int i = 0; i < length; i++) {
							JSONObject object = array.getJSONObject(i);
							
						    tapUser=new User();
						    tapUser.setBirthday(object.getString("Birthday"));
						    tapUser.setCreditGrade(object.getString("CreditGrade"));
						    tapUser.setDormitoryNumber(object.getString("DormitoryNumber"));
						    tapUser.setEmail(object.getString("Email"));
						    tapUser.setGoldBeanNumber(object.getInt("GoldBeanNumber"));
						    tapUser.setiAutography(object.getString("IAutography"));
						    tapUser.setId(object.getInt("ID"));
						    tapUser.setMajorClass(object.getString("MajorClass"));
						    tapUser.setPassword(object.getString("Password"));
						    tapUser.setPhotoUrl(object.getString("PhotoUrl"));
						    tapUser.setQqNumber(object.getString("QQNumber"));
						    tapUser.setSex(object.getString("Sex"));
						    tapUser.setUsername(object.getString("Username"));
						    new Thread(){
								public void run(){
									 bm=HttpUtil.getUserImage(tapUser.getPhotoUrl());
									 handler.sendMessage(msg);
								} 
							}.start();
						    
						}
						
					} catch (JSONException e) {e.printStackTrace();}	
				}
				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					Toast.makeText(GetPacketActivity.this, "未连接到服务器", Toast.LENGTH_SHORT).show();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(GetPacketActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
		}
		
		
	}
}

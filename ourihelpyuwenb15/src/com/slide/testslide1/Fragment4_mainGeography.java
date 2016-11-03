package com.slide.testslide1;

import java.util.ArrayList;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import android.content.DialogInterface.OnClickListener;

import domin.Geography;
import domin.UserInfoGeography;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
@SuppressLint("HandlerLeak")
public class Fragment4_mainGeography extends Fragment {

	private Button bt_back;
	protected static final int GOLOC = 0;
	protected static int SLEEPTIME = 1;
	//服务后台定位
	private LocationService ls;
	private ServiceConnection myConnection=new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			ls=null;
			Toast.makeText(getActivity(), "停止连接。。。", Toast.LENGTH_SHORT).show();
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			ls=((LocationService.ServiceBinder)service).getService();
			Toast.makeText(getActivity(), "连接服务。。。",Toast.LENGTH_SHORT).show();
		}
	};
	
	
	// 地图管理器
	private BMapManager mBMapMan = null;
	// 地图视图
	private MapView mMapView = null;
	// 我的位置覆盖物
	private MyLocationOverlay myOverlay;
	// 位置在图层中的索引
	private int myOverlayIndex = 0;
	// 位置数据
	private LocationData data = new LocationData();
	
	private Geography geography = new Geography();
	private BDLocation bdLocation;
	
	ArrayList<UserInfoGeography> ugs = new ArrayList<UserInfoGeography>();

	private TextView tv1 = null;
	private boolean isRun;
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		

		super.onCreateView(inflater, container, savedInstanceState);

		mBMapMan = new BMapManager(getActivity().getApplication());
		mBMapMan.init("QfICf3VW3yvEgG8mxr7nXRID", null);

		View v=inflater.inflate(R.layout.fragment4_maingeography, null);
		tv1 = (TextView) v.findViewById(R.id.tv1);
		bt_back=(Button)v.findViewById(R.id.bt_back);
		
		mMapView = (MapView) v.findViewById(R.id.bmapsView);
		mMapView.setBuiltInZoomControls(true);
		// 设置启用内置的缩放控件
		MapController mMapController = mMapView.getController();
		// 得到mMapView的控制权,可以用它控制和驱动平移和缩放
		GeoPoint point = new GeoPoint((int) (30.444 * 1E6),
				(int) (114.268 * 1E6));
		// 用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
		mMapController.setCenter(point);// 设置地图中心点
		mMapController.setZoom(17);// 设置地图zoom级别
		
		
		//自定义控件        
	   	 Button button = new Button(getActivity());
	   	 button.setText("定位");
	   	 button.setId(1);
	   	 button.setOnClickListener(new MyOnClickListener());
	   	 //创建布局参数
	   	 MapView.LayoutParams layoutParam  = new MapView.LayoutParams(
	   	       //控件宽,继承自ViewGroup.LayoutParams
	   	       MapView.LayoutParams.WRAP_CONTENT,
	   	        //控件高,继承自ViewGroup.LayoutParams
	   	       MapView.LayoutParams.WRAP_CONTENT,
	   	       //使控件固定在某个地理位置
	   	        10,10,
	   	       //控件对齐方式
	   	         MapView.LayoutParams.TOP);
	   	 //添加View到MapView中
	   	 mMapView.addView(button,layoutParam);
   	 	myOverlay = new MyLocationOverlay(mMapView);
		//启动定位服务
		getActivity().bindService(new Intent(getActivity(),LocationService.class), myConnection, android.content.Context.BIND_AUTO_CREATE );
		//getActivity().startService(new Intent(getActivity(),LocationService.class));
		isRun=true;
		new Thread(new Runnable(){
			@Override
			public void run() {
				while(isRun){
					try {
						Thread.sleep(SLEEPTIME*1000);
					} catch (InterruptedException e) {e.printStackTrace();}
					handler.sendEmptyMessage(GOLOC);
				}
			}
		}).start();
		tv1.setOnClickListener(new android.view.View.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(geography!=null){
					Intent intent=new Intent();
					intent.setClass(getActivity(), FindBesiderActivity.class);
					Bundle bundleData=new Bundle();
					bundleData.putString("userLocation", bdLocation.getAddrStr().toString().trim()+geography.getName());
					bundleData.putString("geographyName",geography.getName());
					bundleData.putInt("geographyID",geography.getId());
					
					intent.putExtras(bundleData);
					startActivity(intent);
				}
			}
		});
		/*bt_back.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), MainActivity.class);
				startActivity(intent);		
			}
		});*/
		return v;
	}

	Handler handler =new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GOLOC:
				bdLocation=ls.getBDLocation();
				geography=ls.getGeography();
				if (bdLocation!=null&&geography!=null) {
					SLEEPTIME=60;//更新时间变为1分钟
					Toast.makeText(getActivity(), bdLocation.getAddrStr().toString().trim(), Toast.LENGTH_LONG).show();
					locationToMap(bdLocation.getLongitude(), bdLocation.getLatitude());
					updateTv();
					Log.d("asda", bdLocation.getLongitude()+",,,"+bdLocation.getLatitude()+"....."+geography.getName());
				} else {
					Log.d("LocSDK3", "机没网");
				}
				break;
			default:
				break;
			}
		}
		
	};

	private void locationToMap(double lon, double lat) {
		data.latitude = lat;
		data.longitude = lon;
		data.direction = 2.0f;
		myOverlay.setData(data);
		// 检查覆盖物是否存在，存在则修改，否则则添加
		if (mMapView.getOverlays().contains(myOverlay)) {
			mMapView.getOverlays().set(myOverlayIndex, myOverlay);
		} else {
			myOverlayIndex = mMapView.getOverlays().size();
			mMapView.getOverlays().add(myOverlay);
		}
		GeoPoint geoPoint = new GeoPoint((int) (lat * 1E6),
				(int) (lon * 1E6));
		mMapView.getController().setCenter(geoPoint);
		mMapView.refresh();
		
	}
	private void updateTv(){
		geography=ls.getGeography();
		String str = "您的具体位置为："  +bdLocation.getAddrStr().toString().trim()+ geography.getName()
				+ "\n坐标为西经：" + bdLocation.getLongitude() + "北纬：" + bdLocation.getLatitude() + "\n";
				tv1.setText(str);
				
	}

	@Override
	public void onDestroy() {
		mMapView.destroy();
		if (mBMapMan != null) {
			mBMapMan.destroy();
			mBMapMan = null;
		}
		getActivity().unbindService(myConnection);
		isRun=false;
		super.onDestroy();
	}

	@Override
	public void onPause() {
		mMapView.onPause();
		if (mBMapMan != null) {
			mBMapMan.stop();
		}
		super.onPause();
	}

	@Override
	public void onResume() {
		mMapView.onResume();
		if (mBMapMan != null) {
			mBMapMan.start();
		}
		super.onResume();
	}

	/*
	 * 判断网络是否连接
	 */
	public boolean isNetworkOK() {
		ConnectivityManager conMan = (ConnectivityManager) getActivity().getSystemService(MainActivity.CONNECTIVITY_SERVICE);
		// mobile 3G Data Network
		State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		// wifi
		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		// 如果3G网络和wifi网络都未连接，且不是处于正在连接状态 则进入Network Setting界面 由用户配置网络连接
		if (mobile == State.CONNECTED || mobile == State.CONNECTING
				|| wifi == State.CONNECTED || wifi == State.CONNECTING)
			return true;
		else
			return false;

	}

	
	public class MyOnClickListener implements android.view.View.OnClickListener{
		public void onClick(View v) {
			if(v.getId()==1){
				if (isNetworkOK()) {
					locationToMap(bdLocation.getLongitude(), bdLocation.getLatitude());
					updateTv();
				} else {
					new AlertDialog.Builder(getActivity()).setTitle("确认").setMessage("确定打开网络吗?")
						.setPositiveButton("是", myListenerYES)
						.setNegativeButton("否", myListenerNO).show();
				}
			}
		}
		
	}
	
	/*
	 * 是否联网对话框的事件监听
	 */
	private OnClickListener myListenerYES = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			Toast.makeText(getActivity(), "进入系统设置界面", Toast.LENGTH_SHORT)
					.show();
			Intent intent = new Intent(
					android.provider.Settings.ACTION_WIRELESS_SETTINGS);
			// 4.0以下系统可以这样intent.setClassName("com.android.settings",
			// "com.android.settings.SecuritySettings");
			startActivity(intent);
		};
	};
	private OnClickListener myListenerNO = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			Toast.makeText(getActivity(), "暂时无法签到", Toast.LENGTH_SHORT)
					.show();
		};
	};

	
	/*
	 * @param 函数作用：保存用户地点信息到数据库 参数：经纬度
	 */
	public void saveToDB(double lon, double lat) {

	}
	
	
}

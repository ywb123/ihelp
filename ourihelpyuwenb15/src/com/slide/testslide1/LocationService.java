package com.slide.testslide1;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ywb.uploadData.util.HttpUtil;

import domin.Geography;
import domin.User;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class LocationService extends Service {

	
	protected static final int TOASTOK = 0;
	protected static final int TOASTERROR = 1;
	//位置数据
	private String username = null;
	private User user=new User();
	private Geography geography=new Geography();
	private BDLocation bdLocation=null;
	
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	
	
	private ServiceBinder sb=new ServiceBinder();
	private boolean isRun;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		isRun=true;
		mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
		mLocationClient.registerLocationListener( myListener );    //注册监听函数
		mLocationClient.setAK("yc5u3aEvNm9U8Whx99BWbfY1");
		
		Context con = getApplicationContext();
		SharedPreferences sp = con.getSharedPreferences("test",
				MODE_PRIVATE);
		username = sp.getString("name", "");
		
		new Thread(new Runnable(){
			@Override
			public void run() {
				while(isRun){
					getLocation();
					try {
						getLocationGeography(bdLocation.getLongitude(), bdLocation.getLatitude());
						Thread.sleep(5000);
						getUser(username);
						Thread.sleep(3000);
						
						//if(userIsRecord()){insertUserGeography(user.getId(), geography.getId());}
						//在用户注册的时候给一个初始位置，以后只用更新
						updateUserGeography(user.getId(), geography.getId(),bdLocation.getLatitude(),bdLocation.getLongitude());
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			}
		}).start();
	
	}

	public void getLocation(){
		if(isNetworkOK())
			{
			    LocationClientOption option = new LocationClientOption();
			    option.setOpenGps(true);
			    option.setAddrType("all");//返回的定位结果包含地址信息
			    option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
			    //5000ms刷新一次用户所在位置
			    option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
			    option.disableCache(true);//禁止启用缓存定位
			    option.setPoiNumber(5);    //最多返回POI个数   
			    option.setPoiDistance(1000); //poi查询距离        
			    option.setPoiExtraInfo(true); //是否需要POI的电话和地址等详细信息        
			    mLocationClient.setLocOption(option);
			    mLocationClient.start();
			    if (mLocationClient != null && mLocationClient.isStarted())
			    	mLocationClient.requestLocation();
			    else 
			    	Log.d("LocSDK3", "locClient is null or not started");
			}
			else{
				Log.d("LocSDK3", "手机没网");
				handler.sendEmptyMessage(TOASTERROR);
			}
	}
	Handler handler =new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case TOASTOK:
				//Toast.makeText(LocationService.this, "你的位置为"+msg.getData().getString("myLocation"), Toast.LENGTH_SHORT).show();
				break;
			case TOASTERROR:
				Toast.makeText(LocationService.this, "网络有问题。。。", Toast.LENGTH_SHORT).show();
				break;
			case 1000:
				geography.setId(msg.getData().getInt("ID"));
				geography.setLatitude(msg.getData().getDouble("latitude"));
				geography.setLongitude(msg.getData().getDouble("longitude"));
				geography.setName(msg.getData().getString("geographyName"));
				break;
			case 1001:

				//Toast.makeText(LocationService.this, msg.getData().getString("Username")+"...."+msg.getData().getInt("ID"), Toast.LENGTH_SHORT).show();
				
				user.setBirthday(msg.getData().getString("Birthday"));
				user.setId(msg.getData().getInt("ID"));
				user.setGoldBeanNumber(msg.getData().getInt("GoldBeanNumber"));
				user.setUsername(msg.getData().getString("Username"));
				user.setCreditGrade(msg.getData().getString("CreditGrade"));
				user.setDormitoryNumber(msg.getData().getString("DormitoryNumber"));
				user.setEmail(msg.getData().getString("Email"));
				user.setiAutography(msg.getData().getString("IAutography"));
				user.setMajorClass(msg.getData().getString("MajorClass"));
				user.setPassword(msg.getData().getString("Password"));
				user.setPhotoUrl(msg.getData().getString("PhotoUrl"));
				user.setQqNumber(msg.getData().getString("QQNumber"));
				user.setSex(msg.getData().getString("Sex"));
				//Toast.makeText(LocationService.this, user.getUsername()+"...."+user.getId(), Toast.LENGTH_SHORT).show();
				
				break;
			default:
				break;
			}
		}
		
	};
    public class MyLocationListener implements BDLocationListener {
	    @Override
	   public void onReceiveLocation(BDLocation location) {
	      if (location == null)
	          return ;
	      bdLocation=location;
	      Message msg=new Message();
	      Bundle data=new Bundle();
	      data.clear();
	      data.putString("myLocation", location.getAddrStr());
	      msg.setData(data);
	      msg.what=TOASTOK;
	      handler.sendMessage(msg);
	    }
	    public void onReceivePoi(BDLocation location) {
	         if (location == null){
	                return ;
	         }
	         Message msg=new Message();
		     Bundle data=new Bundle();
		     data.clear();
		     data.putString("myLocation", location.getAddrStr());
		     msg.setData(data);
		     msg.what=TOASTOK;
		     handler.sendMessage(msg);
	         bdLocation=location;
		}
    }
    
	public void getLocationGeography(double longitude,double latitude){
		double lon = longitude;
		double lat = latitude;
		AsyncHttpClient client= new AsyncHttpClient();
		String path=HttpUtil.URL+"GeographyServlet";
		RequestParams params=new RequestParams();
		params.add("action", "query");
		params.add("longitude", lon+"");
		params.add("latitude", lat+"");
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
//							g.setId(object.getInt("ID"));
//							g.setLatitude(object.getDouble("latitude"));
//							g.setLongitude(object.getDouble("longitude"));
//							g.setName(object.getString("geographyName"));
							Message msg=new Message();
						    Bundle data=new Bundle();
						    data.clear();
						    data.putString("geographyName", object.getString("geographyName"));
						    data.putInt("ID", object.getInt("ID"));
						    data.putDouble("longitude", object.getDouble("longitude"));
						    data.putDouble("latitude", object.getDouble("latitude"));
						    msg.setData(data);
						    msg.what=1000;
						    handler.sendMessage(msg);
						}
						
					} catch (JSONException e) {e.printStackTrace();}	
				}
				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					Toast.makeText(LocationService.this, "未连接到服务器", Toast.LENGTH_SHORT).show();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(LocationService.this, "网络错误", Toast.LENGTH_SHORT).show();
		}
        
	}

	public void getUser(String name){
		AsyncHttpClient client= new AsyncHttpClient();
		String path=HttpUtil.URL+"UserServlet";
		RequestParams params=new RequestParams();
		params.add("action", "queryByUsername");
		params.add("username", name);
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
							
							Message msg=new Message();
						    Bundle data=new Bundle();
						    data.clear();
						    data.putString("Username", object.getString("Username"));
						    data.putInt("ID", object.getInt("ID"));
						    data.putInt("GoldBeanNumber", object.getInt("GoldBeanNumber"));
						    data.putString("Birthday", object.getString("Birthday"));
						    data.putString("CreditGrade", object.getString("CreditGrade"));
						    data.putString("DormitoryNumber", object.getString("DormitoryNumber"));
						    data.putString("Email", object.getString("Email"));
						    data.putString("IAutography", object.getString("IAutography"));
						    data.putString("MajorClass", object.getString("MajorClass"));
						    data.putString("Password", object.getString("Password"));
						    data.putString("PhotoUrl", object.getString("PhotoUrl"));
						    data.putString("QQNumber", object.getString("QQNumber"));
						    data.putString("Sex", object.getString("Sex"));
						    msg.setData(data);
						    msg.what=1001;
						    handler.sendMessage(msg);
						}
					} catch (JSONException e) {e.printStackTrace();}	
				}
				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					Toast.makeText(LocationService.this, "未连接到服务器", Toast.LENGTH_SHORT).show();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(LocationService.this, "网络错误", Toast.LENGTH_SHORT).show();
		}
		
	}
	public void insertUserGeography(int userID,int geographyID){
		AsyncHttpClient client= new AsyncHttpClient();
		String path=HttpUtil.URL+"GeographyUserServlet";
		RequestParams params=new RequestParams();
		params.add("action", "add");
		params.add("userId", userID+"");
		params.add("geographyId", geographyID+"");
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
	public void updateUserGeography(int userID,int geographyID,double lat,double lon){
		AsyncHttpClient client= new AsyncHttpClient();
		String path=HttpUtil.URL+"GeographyUserServlet";
		RequestParams params=new RequestParams();
		params.add("action", "updateLocationByUserId");
		params.add("userId", userID+"");
		params.add("geographyId", geographyID+"");
		params.add("latitude", lat+"");
		params.add("longitude", lon+"");
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
	
	
    public boolean isNetworkOK(){
    	ConnectivityManager conMan = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
		//mobile 3G Data Network
		State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		//wifi
		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		if(mobile==State.CONNECTED||mobile==State.CONNECTING||wifi==State.CONNECTED||wifi==State.CONNECTING)
			return true;
		else
			return false;
    	
    }

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mLocationClient != null && mLocationClient.isStarted())
			mLocationClient.stop();
		isRun=false;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return sb;
	}
	public class ServiceBinder extends Binder{
		LocationService getService(){
			return LocationService.this;
		}
	}
	
	public BDLocation getBDLocation(){
		return bdLocation;
	}
	public Geography getGeography(){
		return geography;
	}
	
	
	

}
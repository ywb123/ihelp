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
	// ��ͼ������
	private BMapManager mBMapMan = null;
	// ��ͼ��ͼ
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
		int index=data.getInt("index");//1,2,3�ֱ��ʾ��ȡ��ݡ��������¼�
		spinner.setSelection(index-1);
		//setMapView(index);//���ݲ�ͬ�¼���������ʾ��ͼ�Ĳ�ͬ����
		//��Ϊ��spinner�տ�ʼ���ص�ʱ������һ��onitemclick����
	}
	
	
	//��ȡ�û�λ��
	public void getUserLocation(){
		
		AsyncHttpClient client= new AsyncHttpClient();
		//���ӷ���������ѯGeographyUserServlet
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
					Toast.makeText(GetPacketActivity.this, "δ���ӵ�������", Toast.LENGTH_SHORT).show();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "�������", Toast.LENGTH_SHORT).show();
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
				// �����������õ����ſؼ�
				MapController mMapController = mMapView.getController();
				// �õ�mMapView�Ŀ���Ȩ,�����������ƺ�����ƽ�ƺ�����
				GeoPoint point = new GeoPoint((int) (loclatitude * 1E6),
						(int) (loclongitude * 1E6));
				// �ø����ľ�γ�ȹ���һ��GeoPoint����λ��΢�� (�� * 1E6)
				mMapController.setCenter(point);// ���õ�ͼ���ĵ�
				mMapController.setZoom(17);// ���õ�ͼzoom����
				
				//��ʶС�˵�λ��
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
	            
	            if(sex.equals("��")){
	            	sex="˧��";
	            	img1=getResources().getDrawable(R.drawable.touxm);
	            }
	            else if(sex.equals("Ů")){
	            	sex="��Ů";
	            	img1=getResources().getDrawable(R.drawable.touxn);
	            }
	            
	           /* Bitmap output=ImageSetCorner.getRoundedCornerBitmap(bm, 2.0f);
			    img_selecttoux.setImageBitmap(output);*/
	            img1=new BitmapDrawable(bm);
	         // ����setCompoundDrawablesʱ���������Drawable.setBounds()����,����ͼƬ����ʾ
            	img1.setBounds(0, 0, img1.getMinimumWidth(), img1.getMinimumHeight());
            	textName.setCompoundDrawables(img1, null, null, null); //������ͼ��
	            String iauto=tapUser.getiAutography();
	            if(iauto.equals("")||iauto==null){iauto="����˺�����ʲôҲû����";}
	            
	            textName.setText(tapUser.getUsername()+sex); 
	            textBean.setText("����:"+tapUser.getGoldBeanNumber()+"��");
	            textEmail.setText("Email:"+tapUser.getEmail()); 
	            textIAuto.setText(iauto); 
	            
	            PopupOverlay pop=new PopupOverlay(mMapView, null);
	            OverlayItem item=myOverlayUser.getItem(msg.getData().getInt("arg0"));
	            pop.showPopup(view, item.getPoint(), 10);
	            view.setOnLongClickListener(new OnLongClickListener() {
					@Override
					public boolean onLongClick(View arg0) {
						new AlertDialog.Builder(GetPacketActivity.this).setTitle("ѡ�����")
						.setMessage(tapUser.getUsername())
						.setPositiveButton("��ta����", myListenerChat)
						.setNegativeButton("�鿴����", myListenerViewInfo).show();
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
			//��ת��ChatWithUserActivity������Ҫ����ĺ����û���clickUsername����������
			Bundle data=new Bundle();
			data.putString("clickUsername", tapUser.getUsername());
			Intent intent=new Intent(GetPacketActivity.this,ChatWithUserActivity.class);
			intent.putExtras(data);
			startActivity(intent);
			Toast.makeText(GetPacketActivity.this, "��"+tapUser.getUsername()+"����", Toast.LENGTH_SHORT).show();
		}

	};
	private android.content.DialogInterface.OnClickListener myListenerViewInfo = new android.content.DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			//�鿴���ϴ���
			Bundle data=new Bundle();
			data.putString("clickUsername", tapUser.getUsername());
			data.putInt("clickUserId", tapUser.getId());
			Intent intent=new Intent(GetPacketActivity.this,ViewInfo.class);
			intent.putExtras(data);
			startActivity(intent);
			Toast.makeText(GetPacketActivity.this, "�鿴"+tapUser.getUsername()+"������", Toast.LENGTH_SHORT).show();
		}
	};
	//�ڵ�ͼ�����ñ�ʶ
	public void setMapView(int index){
		points=new ArrayList<GeoPoint>();
		switch (index) {//index����ֵ����1��2��3
		case 1:
			actLocs=new String[2];
			//����Ϊȡ���λ��
			loclatitude=30.443;
			loclongitude=114.271;
			//�ϰ�
			double mlatitude1=30.443;
			double mlongitude1=114.271;
			GeoPoint point1 = new GeoPoint((int) (mlatitude1 * 1E6),(int) (mlongitude1 * 1E6));
			
			actLocs[0]="�ϰ˿�ݵ�";
			//У�ſ�
			double mlatitude2=30.4463;
			double mlongitude2=114.2688;
			GeoPoint point2 = new GeoPoint((int) (mlatitude2 * 1E6),(int) (mlongitude2 * 1E6));
			actLocs[1]="У�ſڿ�ݵ�";
			points.add(point1);
			points.add(point2);
			break;
		case 2:
			actLocs=new String[1];
			//����Ϊ������λ��
			loclatitude=30.4470;
			loclongitude=114.267;
			//԰�ս�
			double mlatitude3=30.4470;
			double mlongitude3=114.267;
			GeoPoint point3 = new GeoPoint((int) (mlatitude3 * 1E6),
					(int) (mlongitude3 * 1E6));
			actLocs[0]="԰�ս�";
			points.add(point3);
			break;
		case 3:
			actLocs=new String[1];
			//����ΪУҽԺλ��
			loclatitude=30.4488;
			loclongitude=114.271;
			//УҽԺ
			double mlatitude4=30.4488;
			double mlongitude4=114.271;
			GeoPoint point4 = new GeoPoint((int) (mlatitude4 * 1E6),
					(int) (mlongitude4 * 1E6));
			actLocs[0]="УҽԺ";
			points.add(point4);
			break;
		default:
			break;
		}
		
		mMapView = (MapView) findViewById(R.id.bmapsView);
		mMapView.setBuiltInZoomControls(true);
		// �����������õ����ſؼ�
		MapController mMapController = mMapView.getController();
		// �õ�mMapView�Ŀ���Ȩ,�����������ƺ�����ƽ�ƺ�����
		GeoPoint point = new GeoPoint((int) (loclatitude * 1E6),
				(int) (loclongitude * 1E6));
		// �ø����ľ�γ�ȹ���һ��GeoPoint����λ��΢�� (�� * 1E6)
		mMapController.setCenter(point);// ���õ�ͼ���ĵ�
		mMapController.setZoom(17);// ���õ�ͼzoom����
		
		//��ʶС�˵�λ��
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
		//���llmain����������
		if(llmain.getChildCount()>2){
			llmain.removeViews(2,llmain.getChildCount()-2);
		}
		for(int i=0;i<points.size();i++){
			//tvChildΪ��ť
			Button tvChild=new Button(GetPacketActivity.this);
			tvChild.setText(actLocs[i]);//actLocs[i]�����д�ŵ���ѡ��ͬ���Ӧ�� �ص�
			//tvChild.setBackgroundColor(Color.GRAY);
			tvChild.setTextColor(Color.WHITE);
			//���ð�ť͸����
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
					//�ص�id����ΪGeographyUser������
					data.putInt("geographyID", gID[index2-1][index1]);
					data.putString("loc",actLocs[index1]);
					//��ת��ShowBesideUser����ʾ��ͬ�ص����ں��ѡ���ʹ��Bundle��ֵ��ȥ
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
	
	//��ȡ�û���Ϣ
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
					Toast.makeText(GetPacketActivity.this, "δ���ӵ�������", Toast.LENGTH_SHORT).show();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(GetPacketActivity.this, "�������", Toast.LENGTH_SHORT).show();
		}
		
		
	}
}

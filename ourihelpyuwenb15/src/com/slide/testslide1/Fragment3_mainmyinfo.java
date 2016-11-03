package com.slide.testslide1;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Fragment3_mainmyinfo extends Fragment {
	private String username;
	private Button btEdit;
	private TextView tvUsername;
	private TextView tvInfo1;
	private TextView tvInfo2;
	private ImageView ivImage;
	private String strImage="";
	private View v;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		v=inflater.inflate(R.layout.fragment3_mainmyinfo, null);
		tvUsername=(TextView) v.findViewById(R.id.tvUsername);
		tvInfo1=(TextView) v.findViewById(R.id.tvInfo1);
		tvInfo2=(TextView) v.findViewById(R.id.tvInfo2);
		ivImage=(ImageView) v.findViewById(R.id.imageView1);
		btEdit=(Button) v.findViewById(R.id.btEdit);
		btEdit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i=new Intent(getActivity(),UpdateUserInfo.class);
				startActivity(i);
			}
		});
		Context con = getActivity();
		SharedPreferences sp = con.getSharedPreferences("test",android.content.Context.MODE_PRIVATE);
		username = sp.getString("name", "");
		Toast.makeText(getActivity(), username, 0).show();
		showInfo();
		return v;
	}
	
	
	private Handler  handler =new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				Context con=getActivity().getApplicationContext();
				SharedPreferences sp=con.getSharedPreferences("user",android.content.Context.MODE_WORLD_READABLE);
				Editor editor=sp.edit();
				editor.putString("Username", msg.getData().getString("Username"));
				editor.putInt("Id", msg.getData().getInt("ID"));
				editor.putString("CreditGrade", msg.getData().getString("CreditGrade"));
				editor.putString("PhotoUrl", msg.getData().getString("PhotoUrl"));
				editor.putString("QQNumber", msg.getData().getString("QQNumber"));
				editor.putString("Birthday", msg.getData().getString("Birthday"));
				editor.putString("IAutography", msg.getData().getString("IAutography"));
				editor.putString("MajorClass", msg.getData().getString("MajorClass"));
				editor.putString("DormitoryNumber", msg.getData().getString("DormitoryNumber"));
				editor.putString("Email", msg.getData().getString("Email"));
				editor.putString("Sex", msg.getData().getString("Sex"));
				editor.putInt("GoldBeanNumber", msg.getData().getInt("GoldBeanNumber"));
				editor.putString("Password", msg.getData().getString("Password"));
				editor.commit();
				
				break;

			case 2:
				Log.d("kkk", "111");
				Bitmap bm=(Bitmap) msg.obj;
				Log.d("kkk", "211");
				//Bitmap bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.doudou);
			    Bitmap output=ImageSetCorner.getRoundedCornerBitmap(bm, 2.0f);
			    //img_selecttoux.setImageBitmap(output);
				ivImage.setImageBitmap(output);
				Log.d("kkk", "311");
				break;
			default:
				break;
			}
		}
		
	};
	
	void showInfo(){
		AsyncHttpClient client= new AsyncHttpClient();
		String path=HttpUtil.URL+"UserServlet";
		RequestParams params=new RequestParams();
		params.add("action", "queryByUsername");
		params.add("username", username);
		try {
			client.post(path, params, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					// TODO Auto-generated method stub
					String result=new String(responseBody);
					
					try {
						JSONArray array = new JSONArray(result);
						int length = array.length();
						for (int i = 0; i < length; i++) {
							JSONObject object = array.getJSONObject(i);
							String str1="QQ号："+object.getString("QQNumber")+
									"\n生日："+object.getString("Birthday")+
									"\n个性签名："+object.getString("IAutography");
							Log.d("aaa", str1);
							String str2="专业班级："+object.getString("MajorClass")+
									"\n宿舍："+object.getString("DormitoryNumber");
							Log.d("aaa", str1);
							tvUsername.setText(object.getString("Username")+"\n"
							+object.getString("Email")+"\n"+
							object.getString("Sex")+"\n"+
							object.getString("GoldBeanNumber")+"豆");
							tvInfo1.setText(str1);
							tvInfo2.setText(str2);
							strImage=object.getString("PhotoUrl");
							Log.d("kkk", "PhotoUrl"+strImage);
							showImage();
							Message msg=new Message();
							msg.what=1;
							Bundle data=new Bundle();
							data.putInt("ID", object.getInt("ID"));
							data.putString("CreditGrade", object.getString("CreditGrade"));
							data.putString("Password", object.getString("Password"));
							data.putString("PhotoUrl", object.getString("PhotoUrl"));
							data.putString("QQNumber", object.getString("QQNumber"));
							data.putString("Birthday", object.getString("Birthday"));
							data.putString("IAutography", object.getString("IAutography"));
							data.putString("MajorClass", object.getString("MajorClass"));
							data.putString("DormitoryNumber", object.getString("DormitoryNumber"));
							data.putString("Username", object.getString("Username"));
							data.putString("Email", object.getString("Email"));
							data.putString("Sex", object.getString("Sex"));
							data.putInt("GoldBeanNumber", object.getInt("GoldBeanNumber"));
							msg.setData(data);
							handler.sendMessage(msg);
						}
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

}

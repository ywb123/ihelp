package com.ywb.uploadData.util;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.util.Log;

public class HttpUtil {
	public static String URL="http://1.ihelp2.sinaapp.com/servlet/";
	public static Bitmap getUserImage(String strImage){
		Bitmap bm=null;
		final String path="http://ihelp2-image.stor.sinaapp.com/image/"+strImage;
		try {
			URL url=new URL(path);
			HttpURLConnection conn=(HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			int code=conn.getResponseCode();
			
			if(code==200){
				InputStream is=conn.getInputStream();
				bm=BitmapFactory.decodeStream(is);
			}
			else{
				Log.d("kkk", "error1:"+code);
			}
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("kkk", "error2");
		}
		return bm;
	}
}

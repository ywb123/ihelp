package com.slide.testslide1;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver{
	private static final String TAG = "MyReceiver";
	private static final String MYTAG = "mytag";
	private SharedPreferences sharedPreferences;
	private String username;//登录用户名
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Bundle bundle = intent.getExtras();
        Log.d(TAG, "onReceive - " + intent.getAction());
         
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
             
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            System.out.println("收到了自定义消息。消息内容是：" + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            // 自定义消息不会展示在通知栏，完全要开发者写代码去处理
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.i(MYTAG,"收到了通知");
            // 在这里可以做些统计，或者做些其他工作
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.i(MYTAG,"用户点击打开了通知");
            Log.i(MYTAG,bundle.getString(JPushInterface.EXTRA_ALERT));
            Log.i(MYTAG,bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE));
            Log.i(MYTAG,bundle.getString(JPushInterface.EXTRA_EXTRA));
            String action=bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
            String result=bundle.getString(JPushInterface.EXTRA_EXTRA);
//            sharedPreferences=this.getSharedPreferences("test",MODE_WORLD_READABLE);
//    		username=sharedPreferences.getString("name", null);
            JPushInterface.reportNotificationOpened(context, bundle.getString(JPushInterface.EXTRA_MSG_ID));
        	Intent i = new Intent(context, MainActivity.class);  //自定义打开的界面。自定义跳转至主页
            Bundle data=new Bundle();
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
           // Map<String,Object> map=bundle.HashMap<String, Object>
            /*try {
            	Log.i(MYTAG,"aaa");
            	JSONArray array = new JSONArray(result);
            	Log.i(MYTAG,array+"");
				int length = array.length();
				for (int i = 0; i < length; i++) {
					Log.i(MYTAG,"bbb");
					//传回的JSONO数据中包含多个字段object
					JSONObject object = array.getJSONObject(i);
					Log.i(MYTAG,"ccc");
					String name=object.getString("name");
					Log.i(MYTAG,name);
			     } 
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				Log.e(MYTAG,"ddd");
			}*/
            /*if(action.equals("sendchatmessage")){
            	
                //定义点击通知后跳转的页面。
                Intent i = new Intent(context, ChatWithUserActivity.class);  //自定义打开的界面
                Bundle data=new Bundle();
    			data.putString("clickUsername","林凤妹");
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    			i.putExtras(data);
                context.startActivity(i);
                JPushInterface.reportNotificationOpened(context, bundle.getString(JPushInterface.EXTRA_MSG_ID));
            }
            else if(action.equals("sendgoldchange")){
            	JPushInterface.reportNotificationOpened(context, bundle.getString(JPushInterface.EXTRA_MSG_ID));
            	Intent i = new Intent(context, MainActivity.class);  //自定义打开的界面
                Bundle data=new Bundle();
    			//data.putString("clickUsername","林凤妹");
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    			//i.putExtras(data);
                context.startActivity(i);
                JPushInterface.reportNotificationOpened(context, bundle.getString(JPushInterface.EXTRA_MSG_ID));
            }*/
            // 在这里可以自己写代码去定义用户点击后的行为
            
   
        } else {
            Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }
	}
	
}

package com.slide.testslide1;

import java.util.ArrayList;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ywb.uploadData.util.HttpUtil;

import domin.TreeCave;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Fragment4_mainmytreecave extends Fragment {

	protected static final int SHOW = 0;
	private View v;
	private Button attribute;
	private Button flash;
	private ListView LV_Message;
	private ArrayList<TreeCave> ms=new ArrayList<TreeCave>();
	//private TextView Author;
	private TextView tvMessage;
	private TextView tvCommentTime;
	private TextView tvCommentNumber;
	private TextView tvPraiseNumber;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		v=inflater.inflate(R.layout.fragment4_mainmytreecave, null);
		
		attribute=(Button)v.findViewById(R.id.attribute);
		flash=(Button)v.findViewById(R.id.flash);
		LV_Message=(ListView) v.findViewById(R.id.LV_Message); 
		 LV_Message.setOnItemClickListener(new MyItemClickListener());
		 
		attribute.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(getActivity(),Treecave_AttributeActivity.class);
		    	startActivity(intent);
			}
		});
		
		flash.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				
				getTreeCaveMessage();		
			}
		});	
		getTreeCaveMessage();
		return v;	
	}
	public void getTreeCaveMessage(){
		AsyncHttpClient client= new AsyncHttpClient();
		String path=HttpUtil.URL+"TreeCaveServlet";
		RequestParams params=new RequestParams();
		params.add("action", "queryAllMessage");
		try {
			client.post(path, params, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					String result=new String(responseBody);
					try {
						ms=new ArrayList<TreeCave>();
						JSONArray array = new JSONArray(result);
						int length = array.length();
						for (int i = 0; i < length; i++) {
							JSONObject object = array.getJSONObject(i);
							
							TreeCave tc=new TreeCave();
							tc.setId(object.getInt("id"));
							tc.setCommentNumber(object.getInt("commentNumber"));
							tc.setPraiseNumber(object.getInt("praiseNumber"));
							tc.setCommentTime(object.getString("commentTime"));
							tc.setMessage(object.getString("message"));
							
							ms.add(tc);
							
						}
						
					    handler.sendEmptyMessage(SHOW);
					} catch (Exception e) {e.printStackTrace();}	
				}
				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					Toast.makeText(getActivity(), "未连接到服务器", Toast.LENGTH_SHORT).show();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();
		}
	}
	Handler handler =new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case SHOW:
				LV_Message.setAdapter(new MyAdapter());	
				break;
			default:
				break;
			}
		}
		
	};
	
	 public class MyItemClickListener implements OnItemClickListener{
	    	//perant指针指向某一个你想要操作的listview,view获取你要的控件的id后操作控件，position指item所在适配器的位置，
	    	public void onItemClick(AdapterView<?> LV_Message , View view, int position, long arg3){
	    		 
	    		 Intent intent=new Intent(getActivity(),Treecave_ResponseActivity.class);
				 Bundle bundle=new Bundle();
				 bundle.putInt("messageid",ms.get(position).getId());
				 Log.d("kkk", ""+ms.get(position).getId());
				 bundle.putString("content",ms.get(position).getMessage());
				 intent.putExtras(bundle);
			     startActivity(intent);
	    	}
	    }
	
	 private class MyAdapter extends BaseAdapter{
   
    	
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return ms.size();
		}

		@Override
		public Object getItem(int position) {
			return ms.get(position);

		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			if (convertView == null) {
				view = LayoutInflater.from(getActivity()).inflate(R.layout.main_list_item, null);
			} else {
				view = convertView;
			}
			tvCommentTime=(TextView) view.findViewById(R.id.tvCommentTime);
			tvCommentTime.setText(ms.get(position).getCommentTime());
			tvMessage= (TextView) view.findViewById(R.id.tvMessage);
			tvMessage.setText(ms.get(position).getMessage());
			tvCommentNumber= (TextView) view.findViewById(R.id.tvCommentNumber);
			tvCommentNumber.setText("评论("+ms.get(position).getCommentNumber()+")");
			tvPraiseNumber=(TextView) view.findViewById(R.id.tvPraiseNumber);
			tvPraiseNumber.setText("赞("+ms.get(position).getPraiseNumber()+")");
			return view;
		   
		}				
	}

}

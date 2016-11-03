package com.slide.testslide1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


public class Fragment0_index extends Fragment {

	private View v;
	
	private ImageView act1;
	private ImageView act2;
	private ImageView act3;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		v=inflater.inflate(R.layout.activity_index, null);
		act1=(ImageView) v.findViewById(R.id.acti1);
		act2=(ImageView) v.findViewById(R.id.acti2);
		act3=(ImageView) v.findViewById(R.id.acti3);
		
		act1.setOnClickListener(new MyOnClickListener());
		act2.setOnClickListener(new MyOnClickListener());
		act3.setOnClickListener(new MyOnClickListener());
		return v;
	}

	public class MyOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent=new Intent();
			Bundle data=new Bundle();
			switch (v.getId()) {
			case R.id.acti1:
				data.putInt("index", 1);
				break;
			case R.id.acti2:
				data.putInt("index", 2);
				break;
			case R.id.acti3:
				data.putInt("index", 3);
				break;
			default:
				break;
			
			}
			intent.putExtras(data);
			intent.setClass(getActivity(), GetPacketActivity.class);
			startActivity(intent);
		}
		
	}
	
	
}

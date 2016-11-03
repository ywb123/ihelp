package com.slide.testslide1;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class Friends_AddActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_friends__add);
	}
	public void return2(View v)
	{
		Intent intent=new Intent();
		Bundle extras=new Bundle();
		extras.putInt("index", 2);
		intent.putExtras(extras);
		intent.setClass(Friends_AddActivity.this,MainActivity.class);
		startActivity(intent);
	}
	public void addfriends(View v)
	{
		Intent intent=new Intent(this,Friends_MakesureActivity.class);
		startActivity(intent);
	}
	public void recommend(View v)
	{
		Intent intent=new Intent(this,Friends_RecommendActivity.class);
		startActivity(intent);
	}
	public void familiar(View v)
	{
		Intent intent=new Intent(this,Friends_FamiliarActivity.class);
		startActivity(intent);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.friends__add, menu);
		return true;
	}

}

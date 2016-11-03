package com.slide.testslide1;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.http.Header;

import com.image.setcorner.ImageSetCorner;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ywb.uploadData.util.HttpUtil;
import com.ywb.uploadData.util.UpLoadUtil;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class UpdateUserInfo extends Activity {
	
	/*
	 * 选择头像
	 */
	private String picturePath="";
	private String picName="";
	private String strImage="";
    private static final String MYTAG = "mytag";
	public static final int NONE = 0;
	public static final int PHOTOHRAPH = 1;// 拍照
	public static final int PHOTOZOOM = 2; // 缩放
	public static final int PHOTORESOULT = 3;// 结果
	public static final String IMAGE_UNSPECIFIED = "image/*";

	private EditText etUsername;
	private EditText etGender;
	private EditText etMajorclass;
	private EditText etDormitorynumber;
	private EditText etAutograph;
	private EditText etBirthday;
	private ImageView  img_selecttoux;  
	SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_update_user_info);
		
		sp=getApplicationContext().getSharedPreferences("user", MODE_PRIVATE);
		etUsername=(EditText) findViewById(R.id.et_name);
		etGender=(EditText) findViewById(R.id.et_gender);
		etMajorclass=(EditText) findViewById(R.id.et_majorclass);
		etDormitorynumber=(EditText) findViewById(R.id.et_dormitorynumber);
		etAutograph=(EditText) findViewById(R.id.et_autograph);
		etBirthday=(EditText) findViewById(R.id.et_birthday);
		img_selecttoux=(ImageView)findViewById(R.id.img_selecttoux);
		
		/*Bitmap bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.);
	    Bitmap output=getRoundedCornerBitmap(bitmap, 15.0f);
	    image.setImageBitmap(output);*/
		showInfo();
		
		/*
		 * 按住头像，选择相册中的图片
		 */
		//img_selecttoux.setOnClickListener(new MyOnClickListener());
		
		img_selecttoux.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//Toast.makeText(UpdateUserInfo.this, "选择啊啊啊啊 ", Toast.LENGTH_SHORT).show();
				new AlertDialog.Builder(UpdateUserInfo.this).setTitle("选择头像")
				.setMessage(sp.getString("Username", ""))
				.setPositiveButton("相册",photography)
				.setNegativeButton("拍照", takephoto)
				.show()
				;
				
			}
		});
	}
	//两个按钮是 DialogInterface中的监听器。。。
	//相册
	private android.content.DialogInterface.OnClickListener photography = new android.content.DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub
			//切换到手机相册
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					IMAGE_UNSPECIFIED);
			startActivityForResult(intent, PHOTOZOOM);			
		}
	}; 
	//拍照
	private android.content.DialogInterface.OnClickListener takephoto=new android.content.DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub
			//切换到拍照
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
					Environment.getExternalStorageDirectory(), "temp.jpg")));
			System.out.println("=============" + Environment.getExternalStorageDirectory());
			startActivityForResult(intent, PHOTOHRAPH);
		}
	};
	
	//用户选择照片后执行
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == NONE)
			return;
		// 拍照    ...裁剪
		if (requestCode == PHOTOHRAPH) {
			// 设置文件保存路径这里放在跟目录下
			File picture = new File(Environment.getExternalStorageDirectory()
					+"/temp.jpg");
			Log.i(MYTAG+"0",picture.getPath());
			//裁剪
			startPhotoZoom(Uri.fromFile(picture));
		}

		if (data == null)
			return;

		//相册  ...裁剪
		if (requestCode == PHOTOZOOM) {
			//获取图片位置：picturePath：图片在手机总的位置.jpg...
			Uri selectedImage = data.getData();
	        String[] filePathColumn = { MediaStore.Images.Media.DATA };      
			Cursor cursor=getContentResolver().query(selectedImage, filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex=cursor.getColumnIndex(filePathColumn[0]);
			picturePath=cursor.getString(columnIndex);			
			Log.i(MYTAG+"1", picturePath);
			cursor.close();
			//裁剪
			startPhotoZoom(data.getData());
		}
		// 处理结果
		if (requestCode == PHOTORESOULT) {
			Bundle extras = data.getExtras();
			if (extras != null) {
				Bitmap photo = extras.getParcelable("data");
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);// (0 - 100)压缩文件
				//设置显示头像
				
				Bitmap output=ImageSetCorner.getRoundedCornerBitmap(photo, 2.0f);
			    img_selecttoux.setImageBitmap(output);

				//上传到服务器
				final File file = new File(picturePath);
				try {
					FileOutputStream fout=new FileOutputStream(file);
					photo.compress(Bitmap.CompressFormat.PNG, 100, fout);
					fout.flush();
					fout.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				picName=file.getName();
				if (file != null) {
					new Thread(new Runnable(){
						@Override
						public void run() {
							// TODO Auto-generated method stub
							String request = UpLoadUtil.uploadFile(file, HttpUtil.URL+"UploadServlet");
							//uploadImage.setText(request);
						}}).start();
					
				}
			}
		}		
	}

	//剪裁照片
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 64);
		intent.putExtra("outputY", 64);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, PHOTORESOULT);
	}
	
	//显示用户资料信息
	private void  showInfo(){
		etUsername.setText(sp.getString("Username", ""));
		etGender.setText(sp.getString("Sex", ""));
		etMajorclass.setText(sp.getString("MajorClass", ""));
		etDormitorynumber.setText(sp.getString("DormitoryNumber", ""));
		etAutograph.setText(sp.getString("IAutography", ""));
		etBirthday.setText(sp.getString("Birthday", ""));
		strImage=sp.getString("PhotoUrl", "");
		showImage();
		
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
	private Handler handler=new Handler(){
		public void  handleMessage (Message msg){
			if(msg.what==2){
				Bitmap bm=(Bitmap) msg.obj;
				Bitmap output=ImageSetCorner.getRoundedCornerBitmap(bm, 2.0f);
				img_selecttoux.setImageBitmap(output);
			}
		}
	};
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.update_user_info, menu);
		return true;
	}
	public void submit(View v){
		String gender=etGender.getText().toString().trim();
		String majorclass=etMajorclass.getText().toString().trim();
		String dormitorynumber=etDormitorynumber.getText().toString().trim();
		String autograph=etAutograph.getText().toString().trim();
		String birthday=etBirthday.getText().toString().trim();
		String photoUrl=sp.getString("PhotoUrl", "");
		if(picName!=""){
			photoUrl=picName;
		}
		Log.d("kkk", photoUrl);
		
		AsyncHttpClient client= new AsyncHttpClient();
		RequestParams params=new RequestParams();
		params.add("action", "update");
		params.add("id", sp.getInt("Id", -1)+"");
		params.add("sex", gender);
		params.add("majorClass", majorclass);
		params.add("dormitoryNumber", dormitorynumber);
		params.add("iAutography", autograph);
		params.add("birthday", birthday);
		params.add("creditGrade", sp.getString("CreditGrade", ""));
		params.add("email", sp.getString("Email", ""));
		params.add("goldBeanNumber", sp.getInt("GoldBeanNumber", 0)+"");
		params.add("password", sp.getString("Password", ""));
		params.add("photoUrl", photoUrl);
		params.add("qqNumber", sp.getString("QQNumber", ""));
		String path=HttpUtil.URL+"UserServlet";
		try {
			client.post(path, params, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					// TODO Auto-generated method stub
					String str=new String(responseBody);
					
					if(str.equals("1")){
						Toast.makeText(UpdateUserInfo.this, "更新资料成功", Toast.LENGTH_SHORT).show();
						
						Intent intent=new Intent();
						Bundle data=new Bundle();
						data.putInt("index", 1);
						intent.putExtras(data);
						intent.setClass(UpdateUserInfo.this, MainActivity.class);
						startActivity(intent);
						finish();
						
					}
					else
						Toast.makeText(UpdateUserInfo.this, "更新失败", Toast.LENGTH_SHORT).show();
				}
				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					// TODO Auto-generated method stub
					Toast.makeText(UpdateUserInfo.this, "未连接到服务器", Toast.LENGTH_SHORT).show();
					
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(UpdateUserInfo.this, "网络错误", Toast.LENGTH_SHORT).show();
		}
	}
	public void back(View v){
		Intent intent=new Intent();
		Bundle data=new Bundle();
		data.putInt("index", 1);
		intent.putExtras(data);
		intent.setClass(UpdateUserInfo.this, MainActivity.class);
		startActivity(intent);
		finish();
	}

}

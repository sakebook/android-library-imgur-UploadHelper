android-library-imgur-UploadHelper
=================================

android image uplaod helper on imgur

How to use

1.Get id from Imgur
[Imgur api](http://api.imgur.com/)

2.import project and set library

3.sample


```Java:SampleActvity.java
public class SampleActivity extends Activity implements OnClickListener, UploadTaskCallback {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		((Button)findViewById(R.id.button)).setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, 200);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
	    super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 

	    switch(requestCode) { 
	    case 200:
	        if(resultCode == RESULT_OK){  

	            UploadHelper helper = new UploadHelper(this, this);
	            helper.setClientId("YOUR_CLIENT_ID");
	            helper.setSecretId("YOUR_SECRET_ID");
	            
	            helper.uploadData(imageReturnedIntent.getData());
	        	
	        }
	    }
	}
	
	@Override
	public void success(String url) {
		Toast.makeText(getApplicationContext(), url, Toast.LENGTH_LONG).show();
	}

	@Override
	public void fail(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void cancel(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
	}
}
```

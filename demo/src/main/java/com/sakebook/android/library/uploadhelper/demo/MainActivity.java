package com.sakebook.android.library.uploadhelper.demo;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.sakebook.android.uploadhelper.UploadHelper;
import com.sakebook.android.uploadhelper.UploadTaskCallback;

public class MainActivity extends AppCompatActivity implements UploadTaskCallback {

    private Uri localUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 200);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case 200:
                if(resultCode == RESULT_OK){

                    UploadHelper helper = new UploadHelper(this, this);
                    helper.setClientId(BuildConfig.CLIENT_ID);
                    helper.setSecretId(BuildConfig.CLIENT_SECRET);
                    localUri = data.getData();
                    helper.uploadData(localUri);
                }
        }
    }

    @Override
    public void success(String url) {
        Toast.makeText(this, url, Toast.LENGTH_LONG).show();
        ImageView imageView = (ImageView) findViewById(R.id.image);
        imageView.setImageURI(localUri);
    }

    @Override
    public void cancel(String message) {

    }

    @Override
    public void fail(String message) {

    }
}

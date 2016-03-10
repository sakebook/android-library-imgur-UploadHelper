package com.sakebook.android.uploadhelper;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

public class UploadHelper implements UploadTaskCallback {
	
	private Context mContext;
	private UploadTaskCallback callback;
	private UploadTask task;
	private View v;
	
	/**
	 * default constract
	 * @param context Context;
	 * @param callback Context; you need implement UploadTaskCallback;
	 */
	public UploadHelper(Context context, UploadTaskCallback callback) {
		this.mContext = context;
		this.callback = callback;
	}

	/**
	 * if you need enable target view. use this constract.
	 * @param context Context;
	 * @param callback Context; you need implement UploadTaskCallback;
	 * @param v View; trigger upload view;
	 */
	public UploadHelper(Context context, UploadTaskCallback callback, View v) {
		this.v = v;
		v.setEnabled(false);
		this.mContext = context;
		this.callback = callback;
	}
	
	/**
	 * default upload method;
	 * @param data Uri; ContentProvider;
	 */
	public void uploadData(Uri data){
		if (data != null) {
			task = new UploadTask(mContext, this);
			task.execute(data);
		}
	}

	/**
	 * custom uplaod method;
	 * @param data Uri; ContentProvider;
	 * @param title String; dialog title text;
	 * @param message String; dialog message text;
	 */
	public void uploadData(Uri data, String title, String message){
		if (data != null) {
			task = new UploadTask(mContext, this, title, message);
			task.execute(data);
		}
	}

	@Override
	public void success(String url) {
		if (!TextUtils.isEmpty(url)) {
			url = "http://imgur.com/"+url;
			Log.i(Const.TAG, "helper success: "+url);
			viewRecovery();
			this.callback.success(url);
		}else {
			Log.i(Const.TAG, "helper success but null...");
			viewRecovery();
			this.callback.fail(url);
		}
	}

	@Override
	public void cancel(String message) {
		Log.i(Const.TAG, "helper cancel: "+message);
		viewRecovery();
		this.callback.cancel(message);
	}

	@Override
	public void fail(String message) {
		Log.i(Const.TAG, "helper fail: "+message);
		viewRecovery();
		this.callback.fail(message);
	}
	
	/**
	 * Imgur client id;
	 * @param clientId String; set Imgur client id;
	 */
	public void setClientId(String clientId){
		if (TextUtils.isEmpty(Const.CLIENT_ID)) {
			Const.CLIENT_ID = clientId;
		}
	}

	/**
	 * Imgur secret id;
	 * @param secretId String; set Imgur secret id;
	 */
	public void setSecretId(String secretId){
		if (TextUtils.isEmpty(Const.CLIENT_SECRET_ID)) {
			Const.CLIENT_SECRET_ID = secretId;
		}
	}
	
	/**
	 * 
	 */
	private void viewRecovery(){
		if (this.v != null) {
			v.setEnabled(false);
		}
	}

}

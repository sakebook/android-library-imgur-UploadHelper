package com.sakebook.android.uploadhelper;

public interface UploadTaskCallback {
	
	public void success(String url);
	public void cancel(String message);
	public void fail(String message);

}

package com.sakebook.android.uploadhelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class ImgurAuthorization {

    private static ImgurAuthorization INSTANCE;

    private ImgurAuthorization() {}

    public static ImgurAuthorization getInstance() {
        if (INSTANCE == null)
            INSTANCE = new ImgurAuthorization();
        return INSTANCE;
    }

    public boolean isLoggedIn(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Const.SHARED_PREFERENCES_NAME, 0);
        return !TextUtils.isEmpty(prefs.getString("access_token", null));
    }

    public void addToHttpURLConnection(Context context, HttpURLConnection conn) {
        SharedPreferences prefs = context.getSharedPreferences(Const.SHARED_PREFERENCES_NAME, 0);
        String accessToken = prefs.getString("access_token", null);

        if (!TextUtils.isEmpty(accessToken)) {
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        }
        else {
            conn.setRequestProperty("Authorization", "Client-ID " + Const.CLIENT_ID);
        }
    }

    public void saveRefreshToken(Context context, String refreshToken, String accessToken, long expiresIn) {
        context.getSharedPreferences(Const.SHARED_PREFERENCES_NAME, 0)
                .edit()
                .putString("access_token", accessToken)
                .putString("refresh_token", refreshToken)
                .putLong("expires_in", expiresIn)
                .commit();
    }

    public String requestNewAccessToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Const.SHARED_PREFERENCES_NAME, 0);
        String refreshToken = prefs.getString("refresh_token", null);

        if (refreshToken == null) {
            Log.w(Const.TAG, "refresh token is null; cannot request access token. login first.");
            return null;
        }

        // clear previous access token
        prefs.edit().remove("access_token").commit();

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(Const.OAUTH_URL).openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Client-ID " + Const.CLIENT_ID);

            ArrayList<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("refresh_token", refreshToken));
            nvps.add(new BasicNameValuePair("client_id", Const.CLIENT_ID));
            nvps.add(new BasicNameValuePair("client_secret", Const.CLIENT_SECRET_ID));
            nvps.add(new BasicNameValuePair("grant_type", "refresh_token"));

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvps);

            OutputStream out = conn.getOutputStream();
            entity.writeTo(out);
            out.flush();
            out.close();

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream in = conn.getInputStream();
                handleAccessTokenResponse(context, in);
                in.close();
            }
            else {
                InputStream errorStream = conn.getErrorStream();
                StringBuilder sb = new StringBuilder();
                Scanner scanner = new Scanner(errorStream);
                while (scanner.hasNext()) {
                    sb.append(scanner.next());
                }
                Log.i(Const.TAG, "error response: " + sb.toString());
                errorStream.close();
            }

            return prefs.getString("access_token", null);

        } catch (Exception ex) {
            Log.e(Const.TAG, "Could not request new access token", ex);
            return null;
        } finally {
            try {
                conn.disconnect();
            } catch (Exception ignore) {}
        }
    }

    private void handleAccessTokenResponse(Context context, InputStream in) throws JSONException {
        StringBuilder sb = new StringBuilder();
        Scanner scanner = new Scanner(in);
        while (scanner.hasNext()) {
            sb.append(scanner.next());
        }

        JSONObject root = new JSONObject(sb.toString());
        String accessToken      = root.getString("access_token");
        String refreshToken     = root.getString("refresh_token");
        long expiresIn          = root.getLong("expires_in");
        String tokenType        = root.getString("token_type");
        String accountUsername  = root.getString("account_username");

        context.getSharedPreferences(Const.SHARED_PREFERENCES_NAME, 0)
                .edit()
                .putString("access_token", accessToken)
                .putString("refresh_token", refreshToken)
                .putLong("expires_in", expiresIn)
                .putString("token_type", tokenType)
                .putString("account_username", accountUsername)
                .commit();
    }

    public void logout(Context context) {
        context.getSharedPreferences(Const.SHARED_PREFERENCES_NAME, 0)
                .edit()
                .clear()
                .commit();
    }
}

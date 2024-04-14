package com.example.javaandroidapp.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.javaandroidapp.R;
import com.example.javaandroidapp.adapters.Callbacks;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;

public class OauthToken {
    private static final String SCOPES = "https://www.googleapis.com/auth/firebase.messaging";

    public interface AccessTokenCallback {
        void onSuccess(String accessToken);
        void onError(Exception exception);
    }

    public static void getAccessToken(Context context, AccessTokenCallback callback) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    InputStream inputStream = context.getResources().openRawResource(R.raw.service_account);
                    GoogleCredentials googleCredential = GoogleCredentials
                            .fromStream(inputStream)
                            .createScoped(Arrays.asList(SCOPES));
                    googleCredential.refreshIfExpired();
                    return googleCredential.getAccessToken().getTokenValue();
                } catch (IOException e) {
                    e.printStackTrace();
                    callback.onError(e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String accessToken) {
                if (accessToken != null) {
                    callback.onSuccess(accessToken);
                } else {
                    // Handle the case where access token is null
                    callback.onError(new Exception("Access token is null"));
                }
            }
        }.execute();
    }
}

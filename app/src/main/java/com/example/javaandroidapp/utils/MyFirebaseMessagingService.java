package com.example.javaandroidapp.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.javaandroidapp.R;
import com.example.javaandroidapp.activities.MerchantOrderDetails;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        // Save the token to your backend server or perform other actions
        Log.d("MyFirebaseMessaging", "Refreshed token: " + token);
        saveTokenToFirebase(token);
    }
    private void saveTokenToFirebase(String token) {
        // Save token using SharedPreference first
        // When user creates an account, we add the token to the firebase attribute
        // We then retrieve the token by the attribute to send notifications.
        SharedPreferences sharedPreferences = getSharedPreferences("Bulkify", MODE_PRIVATE);
        sharedPreferences.edit().putString("userIdToken", token).apply();
    }

}

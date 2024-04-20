package com.example.javaandroidapp.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.example.javaandroidapp.adapters.Callbacks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.api.models.QueryChannelsRequest;
import io.getstream.chat.android.client.channel.ChannelClient;
import io.getstream.chat.android.models.Channel;
import io.getstream.chat.android.models.FilterObject;
import io.getstream.chat.android.models.Filters;
import io.getstream.chat.android.models.querysort.QuerySortByField;
import io.getstream.chat.android.state.plugin.config.StatePluginConfig;
import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory;
import io.getstream.chat.java.exceptions.StreamException;
import io.getstream.chat.java.models.User;

public class ChatSystem {
    private static ChatSystem chatSystem = null;

    public static ChatClient client;
    public boolean signedIn;
    ApplicationInfo applicationInfo;
    String apiKey;
    String apiSecret;

    private ChatSystem(Context context, com.example.javaandroidapp.modals.User new_user) {
        try {
            applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (applicationInfo != null){
            apiKey = applicationInfo.metaData.getString("chatSecretKey");
            apiSecret = applicationInfo.metaData.getString("chatApiSecret");
        }
        StreamStatePluginFactory statePluginFactory = new StreamStatePluginFactory(new StatePluginConfig(), context);
        this.client = new ChatClient.Builder(apiKey, context).withPlugins(statePluginFactory).build();
        io.getstream.chat.android.models.User user = new io.getstream.chat.android.models.User.Builder().withId(new_user.getUid()).
                withImage(new_user.getProfileImage()).withName(new_user.getName()).build();
        if (this.client.fetchCurrentUser().execute().getOrNull() == null) {
            this.client.connectUser(user, getToken(new_user.getUid())).enqueue();
        }
    }
    public static synchronized ChatSystem getInstance() {
        if (chatSystem == null) {
            return null;
        }
        return chatSystem;
    }

    public static synchronized ChatSystem getInstance(Context context, com.example.javaandroidapp.modals.User user) {
        if (chatSystem == null) {
            chatSystem = new ChatSystem(context, user);
        }
        return chatSystem;
    }

    public String getToken(String uid) {
        return User.createToken(apiSecret, uid, null, null);
    }

    public static void createChannel(List<String> listOfUsers, Callbacks callback) throws StreamException {
        ChannelClient channelClient = client.channel("messaging", "");

        Map<String, Object> extraData = new HashMap<>();
        channelClient.create(listOfUsers, extraData).enqueue(result -> {
            if (result.isSuccess()) {
                Channel newChannel = result.getOrNull();
                callback.getChannel(newChannel);
            }
        });

    }
}

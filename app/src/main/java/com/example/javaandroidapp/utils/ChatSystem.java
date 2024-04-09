package com.example.javaandroidapp.utils;

import android.content.Context;

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

    public ChatClient client;
    public boolean signedIn;

    private ChatSystem(Context context, String uid) {
        String apiKey = "4n8ad58drzz3";
        StreamStatePluginFactory statePluginFactory = new StreamStatePluginFactory(new StatePluginConfig(), context);
        this.client = new ChatClient.Builder(apiKey, context).withPlugins(statePluginFactory).build();
        io.getstream.chat.android.models.User user = new io.getstream.chat.android.models.User.Builder().withId(uid).build();
        if (this.client.fetchCurrentUser().execute().getOrNull() == null) {
            this.client.connectUser(user, getToken(uid)).enqueue();
        }
    }
    public static synchronized ChatSystem getInstance() {
        if (chatSystem == null) {
            return null;
        }
        return chatSystem;
    }

    public static synchronized ChatSystem getInstance(Context context, String uid) {
        if (chatSystem == null) {
            chatSystem = new ChatSystem(context, uid);
        }
        return chatSystem;
    }

    public String getToken(String uid) {
        return User.createToken("4cr5hf963b2u9vde9uv32un2pjweaqkwupsau6q4cwwqxa88khzp3r5tdskxeb5c", uid, null, null);
    }

    public void createChannel(List<String> listOfUsers, Callbacks callback) throws StreamException {
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

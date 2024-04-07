package com.example.javaandroidapp.utils;

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
import io.getstream.chat.java.exceptions.StreamException;
import io.getstream.chat.java.models.User;

public class ChatSystem {
    public static String getToken(String uid) {
        String token = io.getstream.chat.java.models.User.createToken("4cr5hf963b2u9vde9uv32un2pjweaqkwupsau6q4cwwqxa88khzp3r5tdskxeb5c", uid, null, null);
        return token;
    }

    public static void createChannel(ChatClient client, List<String> listOfUsers, Callbacks callback) throws StreamException {
        ChannelClient channelClient = client.channel("messaging", "");

        Map<String, Object> extraData = new HashMap<>();
//
//        List<String> list = new ArrayList<>();
//        list.add("Iv8Qf1D6tyYfpvTd4fgne5vfhNr2");
//        list.add("pZDmZf7JtzZvCKtOf3JYonuf71m1");

//        FilterObject filter = Filters.and(Filters.in("members", list), Filters.eq("members_count", 2));
//        QuerySortByField<Channel> sort = QuerySortByField.descByName("last_message_at");
//        System.out.println("Testing");
//        QueryChannelsRequest request = new QueryChannelsRequest(filter, 0, 1, sort, 0, 2).withWatch().withState();
//        client.queryChannels(request).enqueue(result -> {
//            if (result.isSuccess()) {
//                System.out.println("result");
//            }
//        });

        channelClient.create(listOfUsers, extraData).enqueue(result -> {
            if (result.isSuccess()) {
                Channel newChannel = result.getOrNull();
                callback.getChannel(newChannel);
            }
        });

    }
}

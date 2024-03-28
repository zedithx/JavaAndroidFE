package com.example.javaandroidapp.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.javaandroidapp.R;
import com.google.firebase.auth.FirebaseAuth;


import org.json.JSONObject;

import java.util.Collections;

import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.models.Channel;
import io.getstream.chat.android.models.FilterObject;
import io.getstream.chat.android.models.Filters;
import io.getstream.chat.android.models.Message;
import io.getstream.chat.android.models.User;
import io.getstream.chat.android.models.querysort.QuerySortByField;
import io.getstream.chat.android.state.plugin.config.StatePluginConfig;
import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory;
import io.getstream.chat.android.ui.feature.channels.ChannelListActivity;
import io.getstream.chat.android.ui.feature.channels.ChannelListFragment;
import io.getstream.chat.android.ui.feature.channels.header.ChannelListHeaderView;
import io.getstream.chat.android.ui.feature.channels.list.ChannelListView;
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListHeaderViewModel;
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListHeaderViewModelBinding;
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModel;
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModelBinding;
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModelFactory;

public class ChannelActivity extends AppCompatActivity implements ChannelListFragment.HeaderActionButtonClickListener, ChannelListFragment.HeaderUserAvatarClickListener, ChannelListFragment.ChannelListItemClickListener, ChannelListFragment.SearchResultClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel_list);
        if (savedInstanceState == null) {
            String apiKey = "4n8ad58drzz3";
            StreamStatePluginFactory statePluginFactory = new StreamStatePluginFactory(new StatePluginConfig(), this);
            ChatClient client = new ChatClient.Builder(apiKey, this).withPlugins(statePluginFactory).build();
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String token = io.getstream.chat.java.models.User.createToken("4cr5hf963b2u9vde9uv32un2pjweaqkwupsau6q4cwwqxa88khzp3r5tdskxeb5c", uid, null, null);
            User user = new User.Builder().withId(uid).withName("Si Jun").build();
            client.connectUser(user, token).enqueue(result -> {
                if (result.isSuccess()) {
                    System.out.println(user.toString());
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, ChannelListFragment.newInstance())
                            .commit();
                }
            });
        }
    }

    @Override
    public void onChannelClick(@NonNull Channel channel) {

    }

    @Override
    public void onActionButtonClick() {

    }

    @Override
    public void onUserAvatarClick() {

    }

    @Override
    public void onSearchResultClick(@NonNull Message message) {

    }
}

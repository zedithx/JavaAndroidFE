package com.example.javaandroidapp.activities;

import static java.util.Collections.singletonList;

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
import com.example.javaandroidapp.databinding.ChannelListBinding;
import com.example.javaandroidapp.R;
import com.example.javaandroidapp.adapters.CallbackAdapter;
import com.example.javaandroidapp.utils.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
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

public class ChannelActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChannelListBinding binding = ChannelListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String apiKey = "4n8ad58drzz3";
        StreamStatePluginFactory statePluginFactory = new StreamStatePluginFactory(new StatePluginConfig(), this);
        ChatClient client = new ChatClient.Builder(apiKey, getApplicationContext()).withPlugins(statePluginFactory).build();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String token = io.getstream.chat.java.models.User.createToken("4cr5hf963b2u9vde9uv32un2pjweaqkwupsau6q4cwwqxa88khzp3r5tdskxeb5c", uid, null, null);
        User user = new User.Builder().withId(uid).withName("You Xiang").build();
        client.connectUser(user, token).enqueue(result -> {
            FilterObject filter = Filters.and(
                    Filters.in("members", singletonList(user.getId()))
            );

            ViewModelProvider.Factory factory = new ChannelListViewModelFactory.Builder()
                    .filter(filter)
                    .sort(ChannelListViewModel.DEFAULT_SORT)
                    .build();

            ChannelListViewModel channelsViewModel = new ViewModelProvider(this, factory).get(ChannelListViewModel.class);
            ChannelListHeaderViewModel viewModel = new ViewModelProvider(this).get(ChannelListHeaderViewModel.class);

            ChannelListHeaderViewModelBinding.bind(viewModel, binding.channelListHeaderView, this);
            ChannelListViewModelBinding.bind(channelsViewModel, binding.channelListView, this);
            binding.channelListView.setChannelItemClickListener(
                    channel -> startActivity(ChatActivity.newIntent(this, channel))
            );
        });

    }
}

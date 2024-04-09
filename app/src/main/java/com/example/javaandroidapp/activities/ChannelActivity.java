package com.example.javaandroidapp.activities;

import static java.util.Collections.singletonList;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.javaandroidapp.R;
import com.example.javaandroidapp.databinding.ChannelListBinding;
import com.example.javaandroidapp.adapters.CallbackAdapter;
import com.example.javaandroidapp.utils.ChatSystem;
import com.example.javaandroidapp.utils.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.models.FilterObject;
import io.getstream.chat.android.models.Filters;
import io.getstream.chat.android.models.User;
import io.getstream.chat.android.state.plugin.config.StatePluginConfig;
import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory;
import io.getstream.chat.android.ui.ChatUI;
import io.getstream.chat.android.ui.font.ChatFonts;
import io.getstream.chat.android.ui.font.TextStyle;
import io.getstream.chat.android.ui.helper.TransformStyle;
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListHeaderViewModel;
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListHeaderViewModelBinding;
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModel;
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModelBinding;
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModelFactory;
import io.getstream.chat.java.exceptions.StreamException;

public class ChannelActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        ChannelListBinding binding = ChannelListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        TextView header_name = findViewById(R.id.header_saved);
        header_name.setText("Chats");
        TextView title_name = findViewById(R.id.title_saved);
        title_name.setVisibility(View.GONE);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ImageView back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent Main = new Intent(ChannelActivity.this, TransitionLandingActivity.class);
                startActivity(Main);
            }
        });
        Users.getUser(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance().getCurrentUser(), new CallbackAdapter() {
            @Override
            public void getUser(com.example.javaandroidapp.modals.User user_acc) {
                User user = new User.Builder().withId(uid).withName(user_acc.getName()).withImage(user_acc.getProfileImage()).build();
                FilterObject filter = Filters.and(
                        Filters.in("members", singletonList(user.getId()))
                );

                ViewModelProvider.Factory factory = new ChannelListViewModelFactory.Builder()
                        .filter(filter)
                        .sort(ChannelListViewModel.DEFAULT_SORT)
                        .build();

                ChannelListViewModel channelsViewModel = new ViewModelProvider(ChannelActivity.this, factory).get(ChannelListViewModel.class);
                ChannelListViewModelBinding.bind(channelsViewModel, binding.channelListView, ChannelActivity.this);
                binding.channelListView.setChannelItemClickListener(
                        channel -> startActivity(ChatActivity.newIntent(ChannelActivity.this, channel))
                );

            }
        });
    }
}

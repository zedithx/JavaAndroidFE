package com.example.javaandroidapp.activities;

import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.javaandroidapp.R;
import com.example.javaandroidapp.adapters.CallbackAdapter;
import com.example.javaandroidapp.modals.Listing;
import com.example.javaandroidapp.modals.User;
import com.example.javaandroidapp.utils.ChatSystem;
import com.example.javaandroidapp.utils.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.util.DateTime;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import io.getstream.chat.android.models.Channel;
import io.getstream.chat.java.exceptions.StreamException;

public class IndivOrdersActivity extends AppCompatActivity {
    String sellerUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Listing listing = (Listing) getIntent().getSerializableExtra("listing");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        setContentView(R.layout.view_indiv_orders);

        ImageButton backBtn = findViewById(R.id.backBtn);
        TextView listingName = findViewById(R.id.listingName);
        TextView currentOrder = findViewById(R.id.currentOrder);
        TextView minOrder = findViewById(R.id.minOrder);
        TextView expiryText = findViewById(R.id.expiryDate);
        ImageView productImg = findViewById(R.id.listingImage);
        LinearLayout mainLayout = findViewById(R.id.inflaterLayout);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listingName.setText(listing.getName());
        int minOrderNum = listing.getMinOrder().intValue();
        int currOrderNum = listing.getCurrentOrder().intValue();
        minOrder.setText("" + minOrderNum);
        currentOrder.setText("" + currOrderNum);
        String expiryCountdown = listing.getExpiryCountdown();
        Date expiryDate = listing.getExpiry();
        if (expiryDate.after(new Date())) {
            expiryText.setText("" + expiryCountdown);
        } else {
            expiryText.setText("Expired");
        }
        new ImageLoadTask(listing.getImageList().get(0), productImg).execute();
        ChatSystem chatSystem = ChatSystem.getInstance(getApplicationContext(), uid);

        db.collection("orders").whereEqualTo("listingId", listing.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (!querySnapshot.isEmpty()) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : querySnapshot) {
                            LayoutInflater inflater = getLayoutInflater();
                            View buyerLayout = inflater.inflate(R.layout.buyer_fragment, mainLayout, false);
                            TextView buyerName = buyerLayout.findViewById(R.id.buyer_name);
                            TextView variation = buyerLayout.findViewById(R.id.variation);
                            TextView amt = buyerLayout.findViewById(R.id.amt);
                            ImageView buyerPic = buyerLayout.findViewById(R.id.buyer_pic);
                            ImageButton chatBtn = buyerLayout.findViewById(R.id.chat_btn);

                            variation.setText(queryDocumentSnapshot.getString("variant"));
                            amt.setText("" + queryDocumentSnapshot.getDouble("quantity").intValue());
                            DocumentReference docRef = (DocumentReference) queryDocumentSnapshot.get("user");
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot doc = task.getResult();
                                        buyerName.setText(doc.getString("name"));
                                        Glide.with(IndivOrdersActivity.this).load(doc.getString("profileImage")).into(buyerPic);
                                        if (!doc.getId().equals(uid)) {
                                            chatBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    List<String> list = new ArrayList<>();
                                                    //get person uid
                                                    list.add(uid);
                                                    Users.getUserFromId(db, doc.getId(), new CallbackAdapter() {
                                                        @Override
                                                        public void getUser(User new_user) {
                                                            sellerUserId = new_user.getUid();
                                                        }
                                                    });
                                                    list.add(sellerUserId);
                                                    try {
                                                        chatSystem.createChannel(list, new CallbackAdapter() {
                                                            @Override
                                                            public void getChannel(Channel channel) {
                                                                startActivity(ChatActivity.newIntent(IndivOrdersActivity.this, channel));
                                                            }
                                                        });
                                                    } catch (StreamException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                }
                                            });
                                        }else{
                                            chatBtn.setClickable(false);
                                            chatBtn.setImageResource(R.drawable.gray_chatbox);
                                        }
                                    }
                                }
                            });
                            mainLayout.addView(buyerLayout);
                        }
                    }
                }
            }
        });

    }
}

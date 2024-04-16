package com.example.javaandroidapp.activities;

import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import java.util.Arrays;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.Timer;

import io.getstream.chat.android.models.Channel;
import io.getstream.chat.java.exceptions.StreamException;

public class IndivOrdersActivity extends AppCompatActivity {
    String sellerUserId;
    Dictionary<String, Integer> variationDict = new Hashtable<>();
    ChatSystem chatSystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Listing listing = (Listing) getIntent().getSerializableExtra("listing");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = fbUser.getUid();
        setContentView(R.layout.view_indiv_orders);

        ImageButton backBtn = findViewById(R.id.backBtn);
        TextView listingName = findViewById(R.id.listingName);
        TextView currentOrder = findViewById(R.id.currentOrder);
        TextView minOrder = findViewById(R.id.minOrder);
        TextView expiryText = findViewById(R.id.expiryDate);
        ImageView productImg = findViewById(R.id.listingImage);
        LinearLayout mainLayout = findViewById(R.id.inflaterLayout);
        LinearLayout variationAmtLayout = findViewById(R.id.variation_amount_layout);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listingName.setText(listing.getName());
        int minOrderNum = listing.getMinOrder().intValue();
        int curOrderNum = listing.getCurrentOrder().intValue();
        minOrder.setText("" + minOrderNum);
        currentOrder.setText("" + curOrderNum);
        String expiryCountdown = listing.getExpiryCountdown();
        Date expiryDate = listing.getExpiry();
        if (expiryDate.after(new Date())) {
            expiryText.setText("" + expiryCountdown);
        } else {
            expiryText.setText("Expired");
        }
//        new ImageLoadTask(listing.getImageList().get(0), productImg).execute();
        Glide.with(IndivOrdersActivity.this).load(listing.getImageList().get(0)).into(productImg);

        ArrayList<String> variationNames = listing.getVariationNames();
        for (String variantName : variationNames) {
            variationDict.put(variantName, 0);
        }
        Users.getUser(db, fbUser, new CallbackAdapter() {
                    @Override
                    public void getUser(com.example.javaandroidapp.modals.User user_acc) {
                        chatSystem = ChatSystem.getInstance(getApplicationContext(), user_acc);
                    }
                });
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
                            String orderVariant = queryDocumentSnapshot.getString("variant");
                            variation.setText(orderVariant);
                            int amount = queryDocumentSnapshot.getDouble("quantity").intValue();
                            amt.setText("" + amount);

                            int prev = variationDict.put(orderVariant, variationDict.get(orderVariant) + amount);
                            Log.d("print dict", "" + orderVariant + ": "+ variationDict.get(orderVariant) + ", prev: " + prev);

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
                                        } else {
                                            chatBtn.setClickable(false);
                                            chatBtn.setImageResource(R.drawable.gray_chatbox);
                                        }
                                    }
                                }
                            });
                            mainLayout.addView(buyerLayout);
                        }
                        Enumeration<String> keys = variationDict.keys();
                        int totalOrders = 0;
                        while (keys.hasMoreElements()) {
                            LayoutInflater inflater = getLayoutInflater();
                            View relativeLayout = inflater.inflate(R.layout.relative_layout_var_amt, variationAmtLayout, false);
                            TextView newVarText = relativeLayout.findViewById(R.id.var);
                            TextView newVarAmt = relativeLayout.findViewById(R.id.var_amt);
                            String variantName = keys.nextElement();
                            newVarText.setText(variantName);
                            newVarAmt.setText("" + variationDict.get(variantName));
                            totalOrders += variationDict.get(variantName);
                            variationAmtLayout.addView(relativeLayout);
                        }
                    }
                }
            }
        });
    }
}

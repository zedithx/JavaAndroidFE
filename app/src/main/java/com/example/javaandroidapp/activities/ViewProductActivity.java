package com.example.javaandroidapp.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.javaandroidapp.R;
import com.example.javaandroidapp.adapters.CallbackAdapter;
import com.example.javaandroidapp.objects.Listing;
import com.example.javaandroidapp.objects.Order;
import com.example.javaandroidapp.utils.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class ViewProductActivity extends AppCompatActivity {
    static DecimalFormat df = new DecimalFormat("#.00");
    static ImageButton backBtn, addOrder, minusOrder;
    Button buyBtn;
    ArrayList<RoundedButton> varBtnList;
    static TextView priceDollars, priceCents, productDescription, amtToOrder, strikePrice;
    LinearLayout descriptionLayout, ownerLayout, buyPanelLayout, extendedBuyLayout;
    int count = 0;
    static int amt;
    static int focusedBtnId = 1;
    static boolean savedListing = false;
    static boolean buyClicked = false;
    static BuyFragment buyFrag;
    static double displayedPrice;

    public static Listing listing;

    public static FirebaseFirestore db;

    public static FirebaseUser fbUser;

    // get images for product id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        fbUser = mAuth.getCurrentUser();
        // get listing object from listing clicked
        listing = (Listing) getIntent().getSerializableExtra("listing");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_page);
        amt = 1;

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                .replace(R.id.buyFrameLayout, new BuyFragment()).commit();

        // back button
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(ViewProductActivity.this, TransitionLandingActivity.class);
                startActivity(backIntent);
                finish();
            }
        });

        // progress bar
        ProgressBar orderProgressBar = (ProgressBar) findViewById(R.id.orderProgressBar);

        orderProgressBar.setMax(listing.getMinOrder()); // set min required order
        int maxValue = orderProgressBar.getMax();
        orderProgressBar.setProgress(listing.getCurrentOrder(), false); //set current number of orders
        int progressBarValue = orderProgressBar.getProgress();


        //add images
        loadImages(listing.getImageList());

        TextView productName = findViewById(R.id.productName);
        TextView minOrdersView = findViewById(R.id.numOrders2);
        TextView currOrdersView = findViewById(R.id.numOrders1);
        productName.setText(listing.getName());
        currOrdersView.setText("" + listing.getCurrentOrder());
        minOrdersView.setText("/" + listing.getMinOrder());
        priceDollars = findViewById(R.id.priceDollars);
        priceCents = findViewById((R.id.priceCents));
        // get price dynamically
        displayedPrice = listing.getPrice();
        setPrice(displayedPrice, priceDollars, priceCents);
        strikePrice = findViewById(R.id.originalPrice);
        strikePrice.setText("S$" + df.format(listing.getOldPrice()));
        strikePrice.setPaintFlags(strikePrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


        LinearLayout variationBtnParentLayout = findViewById(R.id.varBtns);
        // get arrayList of variation ids
        // get names, prices of each variation and store in name and price arraylists respectively
        int varCount = listing.getVariationNames().size(); // testing with 3 variations


        ArrayList<String> varBtnName = listing.getVariationNames();
        ArrayList<Double> varBtnPrice = listing.getVariationAdditionalPrice();

        //btn layout params
        createBtnPanel(listing, varBtnName, varBtnPrice, variationBtnParentLayout);

        // map product variation details to variation buttons

        productDescription = findViewById(R.id.productDescription);
        productDescription.setText(listing.getDescription());

        descriptionLayout = findViewById(R.id.descriptionLayout);
        GradientDrawable descriptionBg = RoundedButton.RoundedRect(25);
        descriptionBg.setColor(Color.argb(15, 10, 10, 10));
        descriptionLayout.setBackground(descriptionBg);
        productDescription.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        productDescription.setMaxLines(8);
        ownerLayout = findViewById(R.id.productOwnerLayout);
        ownerLayout.setBackground(descriptionBg);

        ownerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sellerListing = new Intent(ViewProductActivity.this, SellerListingActivity.class);
                sellerListing.putExtra("sellerInfo", listing.getCreatedBy());
                startActivity(sellerListing);
            }
        });


        RelativeLayout alphaRelative = findViewById(R.id.alphaRelative);

    }


    public static class BuyFragment extends Fragment {

        static LinearLayout popUpLayout;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
            {
                return inflater.inflate(R.layout.buy_popup_fragment, parent, false);
            }
        }
        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            // saved order button
            ImageButton saveListingBtn = view.findViewById(R.id.saveBtn);
            // check whether listing is saved to toggle heart icon
            Users.isSaved(db, fbUser, listing, new CallbackAdapter() {
                @Override
                public void onResult(boolean isSuccess) {
                    savedListing = isSuccess;
                    System.out.println(isSuccess);
                    saveListingBtn.setImageResource(savedListing ? R.drawable.red_heart_filled : R.drawable.red_heart_empty);
                }
            });
            saveListingBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // add to save attribute array in firebase
                    if (!savedListing) {
                        // save listing under User object
                        Users.saveListing(db, fbUser, listing, new CallbackAdapter() {
                            @Override
                            public void onResult(boolean isSuccess) {
                                if (isSuccess) {
                                    savedListing = !savedListing;
                                    saveListingBtn.setImageResource(savedListing ? R.drawable.red_heart_filled : R.drawable.red_heart_empty);
                                }
                            }
                        });
                    } else {
                        // remove saved listing under User object
                        Users.removeSavedListing(db, fbUser, listing, new CallbackAdapter() {
                            @Override
                            public void onResult(boolean isSuccess) {
                                if (isSuccess) {
                                    savedListing = !savedListing;
                                    saveListingBtn.setImageResource(savedListing ? R.drawable.red_heart_filled : R.drawable.red_heart_empty);
                                }
                            }
                        });
                    }
                }
            });
            Button joinBtn = view.findViewById(R.id.buyOrderBtn);

            LinearLayout chooseVarBtnLayout = view.findViewById(R.id.chooseVarBtnLayout);
            Button blankFillLayout = view.findViewById(R.id.blankFillBtn);
            popUpLayout = view.findViewById(R.id.popupLayout);

            blankFillLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    popUpLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
                    if (popUpLayout.getVisibility() == View.VISIBLE) {
                        collapseCard();
                    }
                    blankFillLayout.setVisibility(View.GONE);


                }
            });
            TextView amtToOrder = view.findViewById(R.id.amtToOrder);
            ImageButton addOrder = view.findViewById(R.id.addOrder);
            ImageButton minusOrder = view.findViewById(R.id.minusOrder);

            ArrayList<String> varBtnName = listing.getVariationNames();
            ArrayList<Double> varBtnPrice = listing.getVariationAdditionalPrice();

//            createBtnPanel(product, varBtnName, varBtnPrice, chooseVarBtnLayout);
//            chooseVarBtnLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            Spinner varSpinner = view.findViewById(R.id.varSpinner);
            ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, varBtnName);
            varSpinner.setAdapter(adapter);
            TextView subTotal = view.findViewById(R.id.subTotalText);
            displayedPrice = listing.getPrice() + varBtnPrice.get(focusedBtnId);
            subTotal.setText("S$ " + df.format(amt * displayedPrice));

            varSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    focusedBtnId = position;
                    displayedPrice = listing.getPrice() + varBtnPrice.get(focusedBtnId);
                    subTotal.setText("S$ " + df.format(amt * displayedPrice));

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            joinBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    popUpLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    if (popUpLayout.getVisibility() == View.GONE) {
                        expandCard();
                        blankFillLayout.setVisibility(View.VISIBLE);
                        buyClicked = true;
                    }else if (popUpLayout.getVisibility() == View.VISIBLE){
                        MakeOrder newOrder = new MakeOrder(amt, listing, varBtnName.get(focusedBtnId));
                        Intent joinOrderIntent = new Intent(getContext(), OrderConfirmationActivity.class);
                        joinOrderIntent.putExtra("new_order", (Serializable) newOrder);
                        startActivity(joinOrderIntent);
                    }



                    // variation btn panel


                    // Change order amounts and change price
                    addOrder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            amt += 1;
                            amtToOrder.setText("" + amt);
                            displayedPrice = listing.getPrice() + varBtnPrice.get(focusedBtnId);
                            subTotal.setText("S$ " + df.format(amt * displayedPrice));
                        }
                    });
                    minusOrder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            amt = amt > 1 ? amt - 1 : 1;
                            amtToOrder.setText("" + amt);
                            displayedPrice = listing.getPrice() + varBtnPrice.get(focusedBtnId);
                            subTotal.setText("S$ " + df.format(amt * displayedPrice));

                        }
                    });

                }

            });


        }

        private void expandCard() {
            popUpLayout.setVisibility(View.VISIBLE);
//            popUpLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//            int endHeight = popUpLayout.getMeasuredHeight();
//            popUpLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));

            ValueAnimator anim = ValueAnimator.ofInt(0, 900);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int value = (Integer) valueAnimator.getAnimatedValue();
                    popUpLayout.getLayoutParams().height = value;
                    popUpLayout.requestLayout();
                }
            });
            anim.setDuration(200); // Duration in milliseconds
            anim.start();
        }

        private void collapseCard() {
            ValueAnimator anim = ValueAnimator.ofInt(popUpLayout.getMeasuredHeight(), 0);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int value = (Integer) valueAnimator.getAnimatedValue();
                    popUpLayout.getLayoutParams().height = value;
                    popUpLayout.requestLayout();
                }
            });
            anim.setDuration(200); // Duration in milliseconds
            anim.start();
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    popUpLayout.setVisibility(View.GONE);
                }
            });
        }


    }


    static void setPrice(double displayedPrice, TextView priceDollars, TextView priceCents) {
        priceDollars.setText("S$" + ("" + df.format(displayedPrice)).split("\\.")[0]);
        String cents = df.format(displayedPrice).contains(".") ? df.format(displayedPrice).split("\\.")[1] : "00";
        priceCents.setText("." + (cents.length() > 1 ? cents : cents + "0"));
    }


    void loadImages(ArrayList<String> getImages) {

        // in pdt class
        ArrayList<String> imageList = new ArrayList<>(getImages);
        ImageView productImages = findViewById(R.id.imageViewer);
        RelativeLayout imageViewLayout = findViewById(R.id.imageViewLayout);
        Glide.with(imageViewLayout).load(imageList.get(0)).into(productImages);
        ImageButton prevBtn = findViewById(R.id.prevBtn);
        ImageButton nextBtn = findViewById(R.id.nextBtn);
        prevBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // TODO - probably change to swipe and remove arrows. Use the dots to represent
                count = count <= 0 ? imageList.size() - 1 : count - 1;
                String image = imageList.get(count);
                Glide.with(imageViewLayout).load(image).into(productImages);

            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count = count < imageList.size() - 1 ? count + 1 : 0;
                String image = imageList.get(count);
                Glide.with(imageViewLayout).load(image).into(productImages);

            }


        });


        // bottom modal

    }
// onclick -> extendedBuyLayout -> fill parent
    // generate all variation buttons
    // ...

    static void createBtnPanel(Listing listing, ArrayList<String> varBtnName, ArrayList<Double> varBtnPrice, LinearLayout variationBtnParentLayout) {

        LinearLayout.LayoutParams varBtnParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        varBtnParams.setMargins(15, 15, 15, 15);
        ArrayList<RoundedButton> varBtnList = new ArrayList<>();


        for (int i = 0; i < varBtnName.size(); i++) {
            int btnId = i + 1;
            RoundedButton newVarBtn = new RoundedButton(variationBtnParentLayout.getContext());
            newVarBtn.setLayoutParams(varBtnParams);
            newVarBtn.setId(btnId);
            String varText = varBtnName.get(i) + "\n" + (varBtnPrice.get(i) > 0 ? "+" + varBtnPrice.get(i) : "-");
            newVarBtn.setText(varText);
            varBtnList.add(newVarBtn);
            newVarBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    focusedBtnId = newVarBtn.getId();
                    for (RoundedButton btn : varBtnList) {
                        GradientDrawable drawable = RoundedButton.RoundedRect(25);
                        drawable.setColor((focusedBtnId == btn.getId() ? Color.argb(150, 255, 30, 7) : Color.argb(15, 10, 10, 10)));
                        btn.setBackground(drawable);
                        displayedPrice = listing.getPrice() + varBtnPrice.get(btnId - 1);
                        setPrice(displayedPrice, priceDollars, priceCents);
                    }
                }
            });
            variationBtnParentLayout.addView(newVarBtn);
        }


    }

    static class RoundedButton extends androidx.appcompat.widget.AppCompatButton {
        public RoundedButton(Context context) {
            super(context);
            init();
        }

        private void init() {
            GradientDrawable drawable = RoundedRect(25);
            drawable.setColor(Color.argb(15, 10, 10, 10));
            setBackground(drawable);
            setTextColor(Color.BLACK);
            setPadding(5, 0, 0, 5);
        }

        static GradientDrawable RoundedRect(int rad) {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setCornerRadius(rad);
            return drawable;
        }
    }
}

class MakeOrder implements Serializable {
    int amount;
    Listing listing;
    String variantName;
    MakeOrder(int amount, Listing listing, String variantName){
        amount = amount;
        listing = listing;
        variantName = variantName;
    }
}
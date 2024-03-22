package com.example.javaandroidapp.activities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.javaandroidapp.R;
import com.example.javaandroidapp.objects.Listing;

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
    static int amt = 1;
    static int focusedBtnId = 1;
    static boolean savedOrder = false;
    static boolean buyClicked = false;
    static BuyFragment buyFrag;
    static double displayedPrice;

    public static Listing listing;

    // get images for product id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // get listing object from listing clicked
        listing = (Listing) getIntent().getSerializableExtra("listing");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_page);

        buyFrag = new BuyFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        // back button
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        loadImages(listing.getImageList()) ;

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


        // buy button panel pop up

        ft.replace(R.id.buyFrameLayout, buyFrag);
        ft.commit();


        RelativeLayout alphaRelative = findViewById(R.id.alphaRelative);
//        alphaRelative.setOnTouchListener(new View.OnTouchListener(){
//            public boolean onTouch(View v, MotionEvent e){
//                if(e.getAction() == MotionEvent.ACTION_UP){
//                    int x = (int) e.getX();
//                    int y = (int) e.getY();
//                    Rect r = new Rect ( 0, 0, 0, 0 );
//                    buyFrag.getView().getHitRect(r);
//                    boolean intersects = r.contains(x,y);
//                    if ( !intersects ) {
//                        Log.d(TAG, "pressed outside the buyFrag");
//                        LinearLayout chooseVarTextLayout = buyFrag.getView().findViewById(R.id.chooseVarTextLayout);
//                        chooseVarTextLayout.removeAllViewsInLayout();
//                    }
//                    return true;
//                }
//                return false;
//            }
//        });


    }


    public static class BuyFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
            {
                return inflater.inflate(R.layout.buy_popup_fragment, parent, false);
            }
        }
        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {

            ;

            // saved order button
            ImageButton saveOrderBtn = view.findViewById(R.id.saveBtn);
            saveOrderBtn.setImageResource( savedOrder ? R.drawable.heart_filled : R.drawable.heart_empty);
            saveOrderBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    savedOrder = !savedOrder;
                    //post req to set savedOrder as true
                    saveOrderBtn.setImageResource(savedOrder ? R.drawable.heart_filled : R.drawable.heart_empty);
                }
            });
            Button joinBtn = view.findViewById(R.id.buyOrderBtn);

            LinearLayout chooseVarBtnLayout = view.findViewById(R.id.chooseVarBtnLayout);
            Button blankFillLayout = view.findViewById(R.id.blankFillBtn);
            blankFillLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout popUpLayout = view.findViewById(R.id.popupLayout);
                    popUpLayout.setVisibility(View.GONE);
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
            subTotal.setText("S$ "+ df.format(amt * displayedPrice));

            varSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    focusedBtnId = position;
                    displayedPrice = listing.getPrice() + varBtnPrice.get(focusedBtnId);
                    subTotal.setText("S$ "+ df.format(amt * displayedPrice));

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            joinBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buyClicked = true;
                    LinearLayout popUpLayout = view.findViewById(R.id.popupLayout);
                    popUpLayout.setVisibility(View.VISIBLE);
                    blankFillLayout.setVisibility(View.VISIBLE);


                    // variation btn panel



                    // Order amount
                    addOrder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            amt += 1;
                            amtToOrder.setText("" + amt);
                            displayedPrice = listing.getPrice() + varBtnPrice.get(focusedBtnId);
                            subTotal.setText("S$ "+ df.format(amt * displayedPrice));
                        }
                    });
                    minusOrder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            amt = amt > 1 ? amt - 1 : 1;
                            amtToOrder.setText("" + amt);
                            displayedPrice = listing.getPrice() + varBtnPrice.get(focusedBtnId);
                            subTotal.setText("S$ "+ df.format(amt * displayedPrice));

                        }
                    });

                }

            });


        }

    }


    static void setPrice(double displayedPrice, TextView priceDollars, TextView priceCents) {
        priceDollars.setText("S$" + ("" + displayedPrice).split("\\.")[0]);
        String cents = ("" + displayedPrice).contains(".") ? ("" + displayedPrice).split("\\.")[1] : "00";
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

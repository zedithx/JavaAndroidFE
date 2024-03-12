package com.example.javaandroidapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ViewProductActivity extends AppCompatActivity {
    ImageButton backBtn, addOrder, minusOrder, saveOrderBtn;
    Button buyBtn;
    ArrayList<RoundedButton> varBtnList;
    TextView priceDollars, priceCents, productDescription, amtToOrder, strikePrice;
    LinearLayout descriptionLayout, ownerLayout, buyPanelLayout;
    int count = 0;
    int amt = 1;
    int focusedBtnId = 1;
    boolean savedOrder = false;


    // get images for product id
    int[] getImages = {R.drawable.test_kangol, R.drawable.test_goodluckbunch, R.drawable.test_springheads};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_page);

        // create new product instance
        Product product = Product.instantiateProduct();

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

        orderProgressBar.setMax(product.getMinOrderAmt()); // set min required order
        int maxValue = orderProgressBar.getMax();
        orderProgressBar.setProgress(product.getCurrOrderAmt(), false); //set current number of orders
        int progressBarValue = orderProgressBar.getProgress();


        //add images
        loadImages(getImages);

        TextView minOrdersView = findViewById(R.id.numOrders2);
        TextView currOrdersView = findViewById(R.id.numOrders1);
        currOrdersView.setText("" + product.getCurrOrderAmt());
        minOrdersView.setText("/" + product.getMinOrderAmt());
        priceDollars = findViewById(R.id.priceDollars);
        priceCents = findViewById((R.id.priceCents));
        // get price dynamically
        setPrice(product.getCurrentPrice(), priceDollars, priceCents);
        strikePrice = findViewById(R.id.originalPrice);
        strikePrice.setText("S$" + product.getOriginalPrice());
        strikePrice.setPaintFlags(strikePrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


        LinearLayout variationBtnParentLayout = findViewById(R.id.varBtns);
        // get arrayList of variation ids
        // get names, prices of each variation and store in name and price arraylists respectively
        int varCount = product.getVariationNames().size(); // testing with 3 variations
        ArrayList<String> varBtnName = product.getVariationNames();
        ArrayList<Double> varBtnPrice = product.getVariationAdditionalPrice();

        varBtnList = new ArrayList<>();

        //btn layout params
        LinearLayout.LayoutParams varBtnParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        varBtnParams.setMargins(15, 15, 15, 15);

        // map product variation details to variation buttons
        for (int i = 0; i < varCount; i++) {
            int btnId = i + 1;
            RoundedButton newVarBtn = new RoundedButton(this);
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
                        setPrice(product.getCurrentPrice() + varBtnPrice.get(btnId - 1), priceDollars, priceCents);
                    }
                }
            });
            variationBtnParentLayout.addView(newVarBtn);
        }
        productDescription = findViewById(R.id.productDescription);
        productDescription.setText(product.getProductDescription());

        descriptionLayout = findViewById(R.id.descriptionLayout);
        GradientDrawable descriptionBg = RoundedButton.RoundedRect(25);
        descriptionBg.setColor(Color.argb(15, 10, 10, 10));
        descriptionLayout.setBackground(descriptionBg);
        productDescription.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        productDescription.setMaxLines(8);
        ownerLayout = findViewById(R.id.productOwnerLayout);
        ownerLayout.setBackground(descriptionBg);

        // Order amount
        addOrder = findViewById(R.id.addOrder);
        minusOrder = findViewById(R.id.minusOrder);
        amtToOrder = findViewById(R.id.amtToOrder);
        addOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amt += 1;
                amtToOrder.setText("" + amt);
            }
        });
        minusOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amt = amt > 1 ? amt - 1 : 1;
                amtToOrder.setText("" + amt);
            }
        });

        // buy button panel pop up
        buyPanelLayout = findViewById(R.id.buyPanelLayout);


        // saved order button
        saveOrderBtn = findViewById(R.id.saveBtn);
        saveOrderBtn.setImageResource(savedOrder ? R.drawable.heart_filled : R.drawable.heart_empty);
        saveOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savedOrder = !savedOrder;
                //post req to set savedOrder as true
                saveOrderBtn.setImageResource(savedOrder ? R.drawable.heart_filled : R.drawable.heart_empty);
            }
        });

    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    static void setPrice(double displayedPrice, TextView priceDollars, TextView priceCents) {
        priceDollars.setText("S$" + ("" + displayedPrice).split("\\.")[0]);
        String cents = ("" + displayedPrice).contains(".") ? ("" + displayedPrice).split("\\.")[1] : "00";
        priceCents.setText("." + (cents.length() > 1 ? cents : cents + "0"));
    }

    void loadImages(int[] getImages) {

        ArrayList<Integer> imageList = new ArrayList();
        for (int imageRes : getImages) {
            imageList.add(imageRes);
        }// in pdt class
        ImageView productImages = findViewById(R.id.imageViewer);
        ImageButton prevBtn = findViewById(R.id.prevBtn);
        ImageButton nextBtn = findViewById(R.id.nextBtn);
        prevBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                count = count <= 0 ? imageList.size() - 1 : count - 1;
                int image_id = imageList.get(count);

                productImages.setImageResource(image_id);

            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count = count < imageList.size() - 1 ? count + 1 : 0;
                int image_id = imageList.get(count);
                productImages.setImageResource(image_id);

            }

            ;
        });
    }

    void getProductOwner() {

    }

}

class RoundedButton extends androidx.appcompat.widget.AppCompatButton {
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

//

class Product {
    static Product pdtInst;
    private static int instanceCount = 0;
    private int productId;
    private int sellerId;
    private double currentPrice;
    private double originalPrice;
    private int currOrderAmt;
    private int minOrderAmt;
    private String productName;
    private String productDescription;
    private ArrayList<String> variationNames = new ArrayList<>();
    private ArrayList<Double> variationAdditionalPrice = new ArrayList<>();
    private ArrayList<Integer> imageList = new ArrayList<>();
    private boolean savedOrder;

    private Product() { // instantiate the product instance, there can be only one instance per ViewProduct activity
        // get product info from backend and populate attributes
        String[] varNameList = {"Small", "Medium", "Large", "Extra Large"};
        double[] varPriceList = {0.0, 0.5, 1.2, 2.8};
        int[] getImages = {R.drawable.test_kangol, R.drawable.test_goodluckbunch, R.drawable.test_springheads};

        ///
        productId = 1;
        sellerId = 10;
        currentPrice = 123.80;
        originalPrice = 140.30;
        currOrderAmt = 23;
        minOrderAmt = 40;
        productName = "Kangol Tote Bag (S/M/L/XL)";
        productDescription = "Officially born in Cleator, Cumbria in the U.K., Kangol gained notoriety as a brand for providing berets to the British army in WWII, most notably for General Bernard Montgomery. The anglo tradition continued in the post war years as Kangol outfitted the English Olympic Team with berets for the 1948 opening ceremonies.\n\nOfficially born in Cleator, Cumbria in the U.K., Kangol gained notoriety as a brand for providing berets to the British army in WWII, most notably for General Bernard Montgomery. The anglo tradition continued in the post war years as Kangol outfitted the English Olympic Team with berets for the 1948 opening ceremonies.";
        savedOrder = false;

        for (String varName : varNameList) {
            variationNames.add(varName);
        }
        for (double varPrice : varPriceList) {
            variationAdditionalPrice.add(varPrice);
        }
        for (int imageRes : getImages) {
            imageList.add(imageRes);
        }
    }
    public static Product instantiateProduct() { // singleton creator static method
        if (instanceCount == 0) {
            instanceCount = 1;
            pdtInst = new Product();
        }
        return pdtInst;
    }

    int getProductId(){
        return productId;
    }
    int getSellerId(){
        return sellerId;
    }
    double getCurrentPrice(){
        return currentPrice;
    }
    double getOriginalPrice(){
        return originalPrice;
    }
    int getCurrOrderAmt(){
        return currOrderAmt;
    }
    int getMinOrderAmt(){
        return minOrderAmt;
    }
    String getProductName(){
        return productName;
    }
    String getProductDescription(){
        return productDescription;
    }
    ArrayList<String> getVariationNames(){
        return variationNames;
    }
    ArrayList<Integer> getImageList(){
        return imageList;
    }
    ArrayList<Double> getVariationAdditionalPrice(){
        return variationAdditionalPrice;
    }
}

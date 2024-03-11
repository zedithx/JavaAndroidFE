package com.example.javaandroidapp;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.javaandroidapp.R;

import org.jetbrains.annotations.TestOnly;
import org.junit.Test;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class ViewProductActivity extends AppCompatActivity {
    ImageButton backBtn, prevBtn, nextBtn, addOrder, minusOrder;
    ArrayList<Integer> imageList;
    ArrayList<RoundedButton> varBtnList;
    ImageView productImages;
    TextView priceDollars, priceCents, productDescription, amtToOrder, strikePrice;
    LinearLayout descriptionLayout;
    int count = 0;
    int amt = 1;
    int focusedBtnId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_page);
        //TODO 1: Instantiate and ArrayList obj
        //TODO 2: Add the image Ids to the ArrayList
        //TODO 3: Get references to the nextBtn and productImages widgets using findViewById
        //TODO 4: for nextBtn, invoke setOnClickListener method

        imageList = new ArrayList();
        imageList.add(R.drawable.test_kangol);
        imageList.add(R.drawable.test_goodluckbunch);
        imageList.add(R.drawable.test_springheads);
        prevBtn = findViewById(R.id.prevBtn);
        nextBtn = findViewById(R.id.nextBtn);
        productImages = findViewById(R.id.imageViewer);


        // back button
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // progress bar
        int minOrder = 40;
        int currOrder = 23;

        ProgressBar orderProgressBar = (ProgressBar) findViewById(R.id.orderProgressBar);

        orderProgressBar.setMax(minOrder); // set min required order
        int maxValue = orderProgressBar.getMax();
        orderProgressBar.setProgress(currOrder, false); //set current number of orders
        int progressBarValue = orderProgressBar.getProgress();


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

        TextView minOrdersView = findViewById(R.id.numOrders2);
        TextView currOrdersView = findViewById(R.id.numOrders1);
        currOrdersView.setText("" + currOrder);
        minOrdersView.setText("/" + minOrder);
        priceDollars = findViewById(R.id.priceDollars);
        priceCents = findViewById((R.id.priceCents));
        // get price dynamically
        priceDollars.setText("S$" + 123);
        priceCents.setText("." + 80);
        String originalPrice = "140.30";
        strikePrice = findViewById(R.id.originalPrice);
        strikePrice.setText("S$" + originalPrice);
        strikePrice.setPaintFlags(strikePrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


        LinearLayout variationBtnParentLayout = findViewById(R.id.varBtns);
        // get arrayList of variation ids
        // get names, prices of each variation and store in name and price arraylists respectively
        int varCount = 4; // testing with 3 variations
        ArrayList<String> varBtnName = new ArrayList<>();
        varBtnName.add("Small");
        varBtnName.add("Medium");
        varBtnName.add("Large");
        varBtnName.add("Extra Large");

        ArrayList<String> varBtnPrice = new ArrayList<>();
        varBtnPrice.add("-");
        varBtnPrice.add("+" + 0.50);
        varBtnPrice.add("+" + 1.20);
        varBtnPrice.add("+" + 2.80);

        varBtnList = new ArrayList<>();

        String description = "Officially born in Cleator, Cumbria in the U.K., Kangol gained notoriety as a brand for providing berets to the British army in WWII, most notably for General Bernard Montgomery. The anglo tradition continued in the post war years as Kangol outfitted the English Olympic Team with berets for the 1948 opening ceremonies.";

        //btn layout params
        LinearLayout.LayoutParams varBtnParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        varBtnParams.setMargins(15, 15, 15, 15);

        // map product variation details to variation buttons
        for (int i = 0; i < varCount; i++) {
            int btnId = i + 1;
            RoundedButton newVarBtn = new RoundedButton(this);
            newVarBtn.setLayoutParams(varBtnParams);
            newVarBtn.setId(btnId);
            String varText = varBtnName.get(i) + "\n" + varBtnPrice.get(i);
            newVarBtn.setText(varText);
            varBtnList.add(newVarBtn);
            newVarBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    focusedBtnId = newVarBtn.getId();
                    for (RoundedButton btn : varBtnList) {
                        GradientDrawable drawable = RoundedButton.RoundedRect(25);
                        drawable.setColor((focusedBtnId == btn.getId() ? Color.argb(150, 255,30,7) : Color.argb(15, 10, 10, 10)));
                        btn.setBackground(drawable);
                    }
                }
            });
            variationBtnParentLayout.addView(newVarBtn);
        }
        productDescription = findViewById(R.id.productDescription);
        productDescription.setText(description);

        descriptionLayout = findViewById(R.id.descriptionLayout);
        GradientDrawable descriptionBg = RoundedButton.RoundedRect(25);
        descriptionBg.setColor(Color.argb(15, 10, 10, 10));
        descriptionLayout.setBackground(descriptionBg);

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


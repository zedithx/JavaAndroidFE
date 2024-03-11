package com.example.javaandroidapp;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
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

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class ViewProductActivity extends AppCompatActivity {
    ImageButton backBtn;
    ArrayList<Integer> imageList;
    ImageButton prevBtn;
    ImageButton nextBtn;
    ImageView productImages;
    int count = 0;

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
        backBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

        // progress bar
        ProgressBar orderProgressBar = (ProgressBar) findViewById(R.id.orderProgressBar);

        orderProgressBar.setMax(50); // set min required order
        int maxValue = orderProgressBar.getMax();
        orderProgressBar.setProgress(30, false); //set current number of orders
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

        // TODO 2.1: init a layout object and assign the layout holding the variation buttons, using id
        // TODO 2.2: get number of product variations, backend should have an ArrayList of variation ids
        // TODO 2.3: map and generate new Button objects and .setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)), and assign id base on count number, .setId()
        // TODO 2.4: and map each variation name to button text value, .setText()
        // TODO 2.5: add button instance to layout object with .addView(btn1)

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
        varBtnPrice.add("+0.50");
        varBtnPrice.add("+1.20");
        varBtnPrice.add("+2.80");


        //btn layout params
//        android:textColor="@color/black"
//        android:background="@color/lightgray"
//        android:layout_margin="15dp"
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
            variationBtnParentLayout.addView(newVarBtn);
        }
    }
}

class RoundedButton extends androidx.appcompat.widget.AppCompatButton {
    public RoundedButton(Context context) {
        super(context);
        init();
    }

    private void init(){
        GradientDrawable drawable = RoundedRect();
        drawable.setColor(Color.LTGRAY);
        setBackground(drawable);
        setTextColor(Color.BLACK);
        setPadding(5,0,0,5);
    }

    static GradientDrawable RoundedRect(){
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(25);
        return drawable;
    }
}


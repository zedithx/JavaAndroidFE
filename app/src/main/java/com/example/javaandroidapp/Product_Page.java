package com.example.javaandroidapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.javaandroidapp.R;

import java.util.ArrayList;

public class Product_Page extends AppCompatActivity {

    ArrayList<Integer> imageList;
    Button prevBtn;
    Button nextBtn;
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
        ProgressBar orderProgressBar = (ProgressBar) findViewById(R.id.orderProgressBar);
        orderProgressBar.setMax(50); // set min required order
        int maxValue = orderProgressBar.getMax();
        orderProgressBar.setProgress(40,false); //set current number of orders
        int progressBarValue = orderProgressBar.getProgress();

        prevBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                int index = count;
                count = count < imageList.size() ? count : 0;
                int image_id = imageList.get(index);

                productImages.setImageResource(image_id);
            }
        });
    }
}


package com.example.javaandroidapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.javaandroidapp.R;

public class LandingOrdersActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set page as view
        setContentView(R.layout.landing_order);
        TextView header_name = findViewById(R.id.header_saved);
        header_name.setText("Orders");
        TextView title_name = findViewById(R.id.title_saved);
        title_name.setText("My Orders");
        ImageView back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent Main = new Intent(LandingOrdersActivity.this, LandingActivity.class);
                startActivity(Main);
            }
        });
    }
}

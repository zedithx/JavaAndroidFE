package com.example.javaandroidapp.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.javaandroidapp.R;

import org.w3c.dom.Text;

public class OrderConfirmationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_confirmation);

        MakeOrder orderDetails = (MakeOrder) getIntent().getSerializableExtra("new_order");

    }
}

package com.example.prm392_cinema;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PaymentNotification extends AppCompatActivity {

Button btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_notification);

        bindings();

       btnBack.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intentSuccess = new Intent(PaymentNotification.this, HomeActivity.class);

               startActivity(intentSuccess);
           }
       });




    }


    private void bindings() {
        btnBack = findViewById(R.id.btnBack);


    }
}
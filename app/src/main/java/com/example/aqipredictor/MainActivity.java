package com.example.aqipredictor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent = new Intent(this,Menu.class);
        startActivity(intent);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                startActivity(intent);
                finish();
            }
        }, 3000);

    }
}

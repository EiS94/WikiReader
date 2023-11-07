package com.schaubeck.wikireader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.util.Objects;

public class Splashscreen extends AppCompatActivity {
    private static final int SPLASH_TIME_OUT = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        Objects.requireNonNull(getSupportActionBar()).hide();

        //SplashScreen
        new Handler().postDelayed(() -> {
            Intent homeIntent = new Intent(Splashscreen.this, MainActivity.class);
            startActivity(homeIntent);
            finish();
        }, SPLASH_TIME_OUT);

    }
}
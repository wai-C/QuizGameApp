package com.example.quizgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.quizgame.databinding.ActivityMainBinding;
import com.example.quizgame.databinding.ActivitySplashScreenBinding;

public class SplashScreenActivity extends AppCompatActivity {

    private ActivitySplashScreenBinding splashScreenBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashScreenBinding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(splashScreenBinding.getRoot());

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.splash_screen_anim);
        splashScreenBinding.textViewSplash.startAnimation(animation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Open The main Act
                Intent iToMainAct = new Intent(SplashScreenActivity.this,MainActivity.class);
                startActivity(iToMainAct);
                finish();
            }
        }, 5000);
    }
}
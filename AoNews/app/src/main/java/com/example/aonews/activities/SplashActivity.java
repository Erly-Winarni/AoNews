package com.example.aonews.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.aonews.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView ivLogo = findViewById(R.id.iv_splash_logo);
        TextView tvAppName = findViewById(R.id.tv_splash_name);
        TextView tvTagline = findViewById(R.id.tv_splash_tagline);

        // Animate logo
        ScaleAnimation scaleAnim = new ScaleAnimation(0.3f, 1.0f, 0.3f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnim.setDuration(800);
        scaleAnim.setFillAfter(true);

        AlphaAnimation alphaAnim = new AlphaAnimation(0f, 1f);
        alphaAnim.setDuration(800);
        alphaAnim.setFillAfter(true);

        AnimationSet animSet = new AnimationSet(true);
        animSet.addAnimation(scaleAnim);
        animSet.addAnimation(alphaAnim);
        ivLogo.startAnimation(animSet);

        // Animate text with delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            AlphaAnimation textAnim = new AlphaAnimation(0f, 1f);
            textAnim.setDuration(600);
            textAnim.setFillAfter(true);
            tvAppName.startAnimation(textAnim);
            tvTagline.startAnimation(textAnim);
        }, 500);

        // Navigate to MainActivity after splash
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, SPLASH_DURATION);
    }
}

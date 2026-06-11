package com.example.aonews.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.aonews.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView ivLogo = findViewById(R.id.iv_splash_logo);
        TextView tvAppName = findViewById(R.id.tv_splash_name);
        TextView tvTagline = findViewById(R.id.tv_splash_tagline);

        // --- State Awal (Tersembunyi) ---
        ivLogo.setScaleX(0f);
        ivLogo.setScaleY(0f);
        ivLogo.setAlpha(0f);
        
        tvAppName.setAlpha(0f);
        tvAppName.setTranslationY(100f);
        
        tvTagline.setAlpha(0f);
        tvTagline.setTranslationY(50f);

        // --- 1. Animasi Masuk Logo (Zoom + Putar) ---
        ivLogo.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .alpha(1f)
                .rotation(360f)
                .setDuration(1200)
                .setInterpolator(new OvershootInterpolator())
                .withEndAction(() -> {
                    // Balik ke ukuran normal sedikit
                    ivLogo.animate().scaleX(1f).scaleY(1f).setDuration(400).start();
                })
                .start();

        // --- 2. Animasi Nama Aplikasi (Muncul dari Bawah) ---
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            tvAppName.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(1000)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        }, 800);

        // --- 3. Animasi Tagline (Fade in) ---
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            tvTagline.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(1000)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        }, 1200);

        // --- 4. Efek Melayang (Floating) ---
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startFloatingAnimation(ivLogo);
        }, 2000);

        // --- 5. Animasi Meluncur (Blast Off) & Pindah Halaman ---
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            
            // Roket Meluncur ke Atas dengan sangat cepat
            ivLogo.animate()
                    .translationY(-2500f)
                    .scaleX(0.4f)
                    .scaleY(2.0f) // Efek stretch/meregang
                    .setDuration(800)
                    .setInterpolator(new AccelerateInterpolator())
                    .start();

            // Teks menghilang perlahan
            tvAppName.animate().alpha(0f).scaleX(0.8f).scaleY(0.8f).setDuration(400).start();
            tvTagline.animate().alpha(0f).setDuration(400).start();

            // Pindah ke MainActivity setelah roket "keluar" dari layar
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                // Transisi fade yang mulus
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }, 600);
            
        }, SPLASH_DURATION - 800);
    }

    private void startFloatingAnimation(View view) {
        view.animate()
                .translationYBy(-30f)
                .setDuration(1500)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new android.animation.AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(android.animation.Animator animation) {
                        view.animate()
                                .translationYBy(30f)
                                .setDuration(1500)
                                .setInterpolator(new AccelerateDecelerateInterpolator())
                                .setListener(new android.animation.AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(android.animation.Animator animation) {
                                        startFloatingAnimation(view); // Loop terus
                                    }
                                }).start();
                    }
                }).start();
    }
}

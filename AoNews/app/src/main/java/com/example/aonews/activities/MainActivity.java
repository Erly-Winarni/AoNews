package com.example.aonews.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.aonews.AoNewsApplication;
import com.example.aonews.R;
import com.example.aonews.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;
    private static final int SPLASH_DURATION = 3500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, false);
        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(window, window.getDecorView());
        controller.hide(WindowInsetsCompat.Type.systemBars());
        controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        binding.appBar.setVisibility(View.GONE);
        binding.bottomNavigation.setVisibility(View.GONE);
        binding.navHostFragment.setVisibility(View.GONE);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }

        BottomNavigationView bottomNav = binding.bottomNavigation;
        AppBarConfiguration appBarConfig = new AppBarConfiguration.Builder(
                R.id.navigation_news,
                R.id.navigation_blogs,
                R.id.navigation_reports,
                R.id.navigation_bookmarks
        ).build();

        if (navController != null) {
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfig);
            NavigationUI.setupWithNavController(bottomNav, navController);
        }

        runSplashAnimation();
    }

    private void runSplashAnimation() {
        View splashLayout = binding.layoutSplash.getRoot();
        View ivLogo = binding.layoutSplash.ivSplashLogo;
        View tvAppName = binding.layoutSplash.tvSplashName;
        View tvTagline = binding.layoutSplash.tvSplashTagline;

        ivLogo.setScaleX(0f);
        ivLogo.setScaleY(0f);
        ivLogo.setAlpha(0f);
        tvAppName.setAlpha(0f);
        tvAppName.setTranslationY(100f);
        tvTagline.setAlpha(0f);
        tvTagline.setTranslationY(50f);

        ivLogo.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .alpha(1f)
                .rotation(360f)
                .setDuration(1200)
                .setInterpolator(new OvershootInterpolator())
                .withEndAction(() -> ivLogo.animate().scaleX(1f).scaleY(1f).setDuration(400).start())
                .start();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            tvAppName.animate().alpha(1f).translationY(0f).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
        }, 800);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            tvTagline.animate().alpha(1f).translationY(0f).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
        }, 1200);

        new Handler(Looper.getMainLooper()).postDelayed(() -> startFloatingAnimation(ivLogo), 2000);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            ivLogo.animate()
                    .translationY(-3000f)
                    .scaleX(0.3f)
                    .scaleY(2.5f)
                    .setDuration(1000)
                    .setInterpolator(new AccelerateInterpolator())
                    .start();

            tvAppName.animate().alpha(0f).scaleX(0.8f).scaleY(0.8f).setDuration(400).start();
            tvTagline.animate().alpha(0f).setDuration(400).start();

            splashLayout.animate()
                    .alpha(0f)
                    .setDuration(800)
                    .withEndAction(() -> {
                        splashLayout.setVisibility(View.GONE);
                        showMainUI();
                    })
                    .start();
            
        }, SPLASH_DURATION - 1000);
    }

    private void showMainUI() {
        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controller.show(WindowInsetsCompat.Type.systemBars());
        binding.appBar.setAlpha(0f);
        binding.bottomNavigation.setAlpha(0f);
        binding.navHostFragment.setAlpha(0f);

        binding.appBar.setVisibility(View.VISIBLE);
        binding.bottomNavigation.setVisibility(View.VISIBLE);
        binding.navHostFragment.setVisibility(View.VISIBLE);

        binding.appBar.animate().alpha(1f).setDuration(600).start();
        binding.bottomNavigation.animate().alpha(1f).setDuration(600).start();
        binding.navHostFragment.animate().alpha(1f).setDuration(600).start();
    }

    private void startFloatingAnimation(View view) {
        if (binding.layoutSplash.getRoot().getVisibility() != View.VISIBLE) return;
        
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
                                        startFloatingAnimation(view);
                                    }
                                }).start();
                    }
                }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        SharedPreferences prefs = getSharedPreferences(AoNewsApplication.PREFS_NAME, MODE_PRIVATE);
        boolean isDark = prefs.getBoolean(AoNewsApplication.KEY_DARK_MODE, true);
        MenuItem themeItem = menu.findItem(R.id.action_toggle_theme);
        if (themeItem != null) {
            themeItem.setIcon(isDark ? R.drawable.ic_sun : R.drawable.ic_moon);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_toggle_theme) {
            toggleTheme();
            return true;
        } else if (item.getItemId() == R.id.action_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleTheme() {
        SharedPreferences prefs = getSharedPreferences(AoNewsApplication.PREFS_NAME, MODE_PRIVATE);
        boolean isDark = prefs.getBoolean(AoNewsApplication.KEY_DARK_MODE, true);
        boolean newMode = !isDark;
        prefs.edit().putBoolean(AoNewsApplication.KEY_DARK_MODE, newMode).apply();
        AppCompatDelegate.setDefaultNightMode(
                newMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController != null && navController.navigateUp() || super.onSupportNavigateUp();
    }
}

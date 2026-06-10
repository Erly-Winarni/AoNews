package com.example.aonews.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        // Setup Navigation
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }

        // Setup Bottom Navigation
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        // Set icon based on current theme
        SharedPreferences prefs = getSharedPreferences(AoNewsApplication.PREFS_NAME, MODE_PRIVATE);
        boolean isDark = prefs.getBoolean(AoNewsApplication.KEY_DARK_MODE, true);
        MenuItem themeItem = menu.findItem(R.id.action_toggle_theme);
        themeItem.setIcon(isDark ? R.drawable.ic_sun : R.drawable.ic_moon);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_toggle_theme) {
            toggleTheme();
            return true;
        } else if (item.getItemId() == R.id.action_search) {
            // Open search activity
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

package com.example.aonews.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import com.example.aonews.R;
import com.example.aonews.database.DatabaseHelper;
import com.example.aonews.databinding.ActivityDetailBinding;
import com.example.aonews.models.Article;
import com.example.aonews.utils.DateUtils;
import com.example.aonews.utils.SpaceFactProvider;
import com.bumptech.glide.Glide;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_ARTICLE_ID = "article_id";
    public static final String EXTRA_ARTICLE_TITLE = "article_title";
    public static final String EXTRA_ARTICLE_URL = "article_url";
    public static final String EXTRA_ARTICLE_IMAGE = "article_image";
    public static final String EXTRA_ARTICLE_SITE = "article_site";
    public static final String EXTRA_ARTICLE_SUMMARY = "article_summary";
    public static final String EXTRA_ARTICLE_DATE = "article_date";

    private ActivityDetailBinding binding;
    private DatabaseHelper dbHelper;
    private ExecutorService executor;
    private Article currentArticle;
    private boolean isBookmarked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = DatabaseHelper.getInstance(this);
        executor = Executors.newSingleThreadExecutor();

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        // Get article data from Intent
        extractArticleFromIntent();

        // Setup UI
        setupUI();

        // Check bookmark status on background thread
        checkBookmarkStatus();
    }

    private void extractArticleFromIntent() {
        Intent intent = getIntent();
        currentArticle = new Article();
        currentArticle.setId(intent.getIntExtra(EXTRA_ARTICLE_ID, 0));
        currentArticle.setTitle(intent.getStringExtra(EXTRA_ARTICLE_TITLE));
        currentArticle.setUrl(intent.getStringExtra(EXTRA_ARTICLE_URL));
        currentArticle.setImageUrl(intent.getStringExtra(EXTRA_ARTICLE_IMAGE));
        currentArticle.setNewsSite(intent.getStringExtra(EXTRA_ARTICLE_SITE));
        currentArticle.setSummary(intent.getStringExtra(EXTRA_ARTICLE_SUMMARY));
        currentArticle.setPublishedAt(intent.getStringExtra(EXTRA_ARTICLE_DATE));
    }

    private void setupUI() {
        binding.tvDetailTitle.setText(currentArticle.getTitle());
        binding.tvDetailSite.setText(currentArticle.getNewsSite() != null ? currentArticle.getNewsSite() : "Unknown");
        binding.tvDetailDate.setText(DateUtils.formatDate(currentArticle.getPublishedAt()));
        binding.tvDetailSummary.setText(currentArticle.getSummary());

        // Setup Daily Fact Card in Detail
        setupFactCard();

        if (currentArticle.getImageUrl() != null && !currentArticle.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(currentArticle.getImageUrl())
                    .placeholder(R.drawable.placeholder_space)
                    .error(R.drawable.placeholder_space)
                    .centerCrop()
                    .into(binding.ivDetailImage);
        } else {
            binding.ivDetailImage.setImageResource(R.drawable.placeholder_space);
        }

        // Open in browser button using Custom Tabs
        binding.btnOpenInBrowser.setOnClickListener(v -> {
            String url = currentArticle.getUrl();
            if (url != null && !url.isEmpty()) {
                try {
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    builder.setShowTitle(true);
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(this, Uri.parse(url));
                } catch (Exception e) {
                    // Fallback to normal browser if Custom Tabs fail
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                }
            } else {
                Toast.makeText(this, "URL not available", Toast.LENGTH_SHORT).show();
            }
        });

        // Share button
        binding.btnShare.setOnClickListener(v -> shareArticle());
    }

    private void setupFactCard() {
        // Daily Fact Card in Detail
        binding.layoutFact.tvFactContent.setText(SpaceFactProvider.getDailyFact());
        binding.layoutFact.cardDailyFact.setOnClickListener(v -> {
            Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            binding.layoutFact.tvFactContent.startAnimation(fadeIn);
            binding.layoutFact.tvFactContent.setText(SpaceFactProvider.getRandomFact());
        });
    }

    private void checkBookmarkStatus() {
        executor.execute(() -> {
            isBookmarked = dbHelper.isBookmarked(currentArticle.getId());
            runOnUiThread(this::updateBookmarkIcon);
        });
    }

    private void updateBookmarkIcon() {
        if (binding.fabBookmark != null) {
            binding.fabBookmark.setImageResource(
                    isBookmarked ? R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark_outline
            );
        }
        invalidateOptionsMenu();
    }

    private void shareArticle() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, currentArticle.getTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                currentArticle.getTitle() + "\n\n" + currentArticle.getUrl());
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem bookmarkItem = menu.findItem(R.id.action_bookmark);
        if (bookmarkItem != null) {
            bookmarkItem.setIcon(isBookmarked ?
                    R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark_outline);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_bookmark) {
            toggleBookmark();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleBookmark() {
        executor.execute(() -> {
            if (isBookmarked) {
                dbHelper.removeBookmark(currentArticle.getId());
                isBookmarked = false;
                runOnUiThread(() -> {
                    Toast.makeText(this, "Removed from bookmarks", Toast.LENGTH_SHORT).show();
                    updateBookmarkIcon();
                });
            } else {
                dbHelper.addBookmark(currentArticle);
                isBookmarked = true;
                runOnUiThread(() -> {
                    Toast.makeText(this, "Saved to bookmarks", Toast.LENGTH_SHORT).show();
                    updateBookmarkIcon();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}

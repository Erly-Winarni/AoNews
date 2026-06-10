package com.example.aonews.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.aonews.adapters.ArticleAdapter;
import com.example.aonews.databinding.ActivitySearchBinding;
import com.example.aonews.viewmodels.ArticleViewModel;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;
    private ArticleViewModel viewModel;
    private ArticleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Search Space News");
        }

        viewModel = new ViewModelProvider(this).get(ArticleViewModel.class);

        setupRecyclerView();
        setupSearchView();
        observeViewModel();
    }

    private void setupRecyclerView() {
        adapter = new ArticleAdapter(this);
        binding.rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSearchResults.setAdapter(adapter);

        adapter.setOnArticleClickListener((article, sharedView) -> {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(DetailActivity.EXTRA_ARTICLE_ID, article.getId());
            intent.putExtra(DetailActivity.EXTRA_ARTICLE_TITLE, article.getTitle());
            intent.putExtra(DetailActivity.EXTRA_ARTICLE_URL, article.getUrl());
            intent.putExtra(DetailActivity.EXTRA_ARTICLE_IMAGE, article.getImageUrl());
            intent.putExtra(DetailActivity.EXTRA_ARTICLE_SITE, article.getNewsSite());
            intent.putExtra(DetailActivity.EXTRA_ARTICLE_SUMMARY, article.getSummary());
            intent.putExtra(DetailActivity.EXTRA_ARTICLE_DATE, article.getPublishedAt());
            startActivity(intent);
        });
    }

    private void setupSearchView() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && !query.trim().isEmpty()) {
                    viewModel.searchArticles(query.trim());
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        binding.searchView.requestFocus();
    }

    private void observeViewModel() {
        viewModel.getArticlesLiveData().observe(this, articles -> {
            adapter.setArticles(articles);
            if (articles == null || articles.isEmpty()) {
                binding.tvNoResults.setVisibility(View.VISIBLE);
                binding.rvSearchResults.setVisibility(View.GONE);
            } else {
                binding.tvNoResults.setVisibility(View.GONE);
                binding.rvSearchResults.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getLoadingLiveData().observe(this, loading -> {
            binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

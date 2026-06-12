package com.example.aonews.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.aonews.activities.DetailActivity;
import com.example.aonews.adapters.ArticleAdapter;
import com.example.aonews.databinding.FragmentNewsBinding;
import com.example.aonews.viewmodels.ArticleViewModel;

public class NewsFragment extends Fragment {

    private FragmentNewsBinding binding;
    private ArticleViewModel viewModel;
    private ArticleAdapter adapter;
    private int currentOffset = 0;
    private boolean isLoading = false;
    private static final int PAGE_SIZE = 20;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentNewsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ArticleViewModel.class);

        setupRecyclerView();
        setupSwipeRefresh();
        setupRefreshButton();
        observeViewModel();
        loadArticles(true);
    }

    private void setupRecyclerView() {
        adapter = new ArticleAdapter(requireContext());
        adapter.setShowFact(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        binding.rvArticles.setLayoutManager(layoutManager);
        binding.rvArticles.setAdapter(adapter);

        binding.rvArticles.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 3) {
                    currentOffset += PAGE_SIZE;
                    viewModel.fetchArticles(currentOffset);
                }
            }
        });

        adapter.setOnArticleClickListener((article, sharedView) -> {
            Intent intent = new Intent(requireActivity(), DetailActivity.class);
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

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(() -> loadArticles(true));
        binding.swipeRefresh.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light
        );
    }

    private void setupRefreshButton() {
        binding.btnRetry.setOnClickListener(v -> {
            binding.layoutError.setVisibility(View.GONE);
            loadArticles(true);
        });
    }

    private void loadArticles(boolean reset) {
        if (reset) {
            currentOffset = 0;
            adapter.clearArticles();
        }
        viewModel.fetchArticles(currentOffset);
    }

    private void observeViewModel() {
        viewModel.getArticlesLiveData().observe(getViewLifecycleOwner(), articles -> {
            binding.swipeRefresh.setRefreshing(false);
            if (articles != null && !articles.isEmpty()) {
                if (currentOffset == 0) {
                    adapter.setArticles(articles);
                } else {
                    adapter.addArticles(articles);
                }
                binding.layoutError.setVisibility(View.GONE);
                binding.rvArticles.setVisibility(View.VISIBLE);
            } else if (adapter.getItemCount() == 0) {
                showError("No articles available");
            }
        });

        viewModel.getLoadingLiveData().observe(getViewLifecycleOwner(), loading -> {
            isLoading = loading;
            if (!binding.swipeRefresh.isRefreshing()) {
                binding.progressBar.setVisibility(loading && currentOffset == 0 ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null && adapter.getItemCount() == 0) {
                showError(error);
            } else if (error != null) {
                Toast.makeText(requireContext(), "⚠ " + error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getIsOfflineLiveData().observe(getViewLifecycleOwner(), isOffline -> {
            binding.tvOfflineBanner.setVisibility(isOffline ? View.VISIBLE : View.GONE);
        });
    }

    private void showError(String message) {
        binding.rvArticles.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.GONE);
        binding.layoutError.setVisibility(View.VISIBLE);
        binding.tvErrorMessage.setText(message);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

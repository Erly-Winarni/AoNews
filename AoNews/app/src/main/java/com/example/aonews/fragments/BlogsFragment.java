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
import com.example.aonews.activities.DetailActivity;
import com.example.aonews.adapters.ArticleAdapter;
import com.example.aonews.databinding.FragmentBlogsBinding;
import com.example.aonews.viewmodels.ArticleViewModel;

public class BlogsFragment extends Fragment {

    private FragmentBlogsBinding binding;
    private ArticleViewModel viewModel;
    private ArticleAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBlogsBinding.inflate(inflater, container, false);
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
        viewModel.fetchBlogs(0);
    }

    private void setupRecyclerView() {
        adapter = new ArticleAdapter(requireContext());
        binding.rvBlogs.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvBlogs.setAdapter(adapter);

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
        binding.swipeRefresh.setOnRefreshListener(() -> viewModel.fetchBlogs(0));
        binding.swipeRefresh.setColorSchemeResources(android.R.color.holo_blue_bright);
    }

    private void setupRefreshButton() {
        binding.btnRetry.setOnClickListener(v -> {
            binding.layoutError.setVisibility(View.GONE);
            viewModel.fetchBlogs(0);
        });
    }

    private void observeViewModel() {
        viewModel.getBlogsLiveData().observe(getViewLifecycleOwner(), blogs -> {
            binding.swipeRefresh.setRefreshing(false);
            if (blogs != null && !blogs.isEmpty()) {
                adapter.setArticles(blogs);
                binding.layoutError.setVisibility(View.GONE);
                binding.rvBlogs.setVisibility(View.VISIBLE);
            } else if (adapter.getItemCount() == 0) {
                showError("No blogs available");
            }
        });

        viewModel.getLoadingLiveData().observe(getViewLifecycleOwner(), loading -> {
            if (!binding.swipeRefresh.isRefreshing()) {
                binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null && adapter.getItemCount() == 0) showError(error);
        });

        viewModel.getIsOfflineLiveData().observe(getViewLifecycleOwner(), isOffline ->
                binding.tvOfflineBanner.setVisibility(isOffline ? View.VISIBLE : View.GONE));
    }

    private void showError(String message) {
        binding.rvBlogs.setVisibility(View.GONE);
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

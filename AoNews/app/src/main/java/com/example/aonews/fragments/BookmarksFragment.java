package com.example.aonews.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.aonews.activities.DetailActivity;
import com.example.aonews.adapters.ArticleAdapter;
import com.example.aonews.databinding.FragmentBookmarksBinding;
import com.example.aonews.viewmodels.ArticleViewModel;

public class BookmarksFragment extends Fragment {

    private FragmentBookmarksBinding binding;
    private ArticleViewModel viewModel;
    private ArticleAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBookmarksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ArticleViewModel.class);

        setupRecyclerView();
        observeViewModel();
        viewModel.loadBookmarks();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadBookmarks();
    }

    private void setupRecyclerView() {
        adapter = new ArticleAdapter(requireContext());
        adapter.setShowFact(true);
        
        binding.rvBookmarks.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvBookmarks.setAdapter(adapter);

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

    private void observeViewModel() {
        viewModel.getBookmarksLiveData().observe(getViewLifecycleOwner(), bookmarks -> {
            if (bookmarks != null && !bookmarks.isEmpty()) {
                adapter.setArticles(bookmarks);
                binding.layoutEmpty.setVisibility(View.GONE);
                binding.rvBookmarks.setVisibility(View.VISIBLE);
            } else {
                adapter.clearArticles();
                binding.rvBookmarks.setVisibility(View.VISIBLE);
                binding.layoutEmpty.setVisibility(View.GONE);

                if (adapter.getItemCount() == 1) {
                     binding.layoutEmpty.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

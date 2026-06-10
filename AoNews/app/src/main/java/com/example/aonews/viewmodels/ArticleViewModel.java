package com.example.aonews.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.aonews.database.DatabaseHelper;
import com.example.aonews.models.Article;
import com.example.aonews.models.ArticleResponse;
import com.example.aonews.network.RetrofitClient;
import com.example.aonews.utils.NetworkUtils;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Article>> articlesLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Article>> blogsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Article>> reportsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Article>> bookmarksLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isOfflineLiveData = new MutableLiveData<>(false);

    private final DatabaseHelper dbHelper;
    private final ExecutorService executor = Executors.newFixedThreadPool(3);

    public ArticleViewModel(@NonNull Application application) {
        super(application);
        dbHelper = DatabaseHelper.getInstance(application);
    }

    // LiveData Getters
    public LiveData<List<Article>> getArticlesLiveData() { return articlesLiveData; }
    public LiveData<List<Article>> getBlogsLiveData() { return blogsLiveData; }
    public LiveData<List<Article>> getReportsLiveData() { return reportsLiveData; }
    public LiveData<List<Article>> getBookmarksLiveData() { return bookmarksLiveData; }
    public LiveData<Boolean> getLoadingLiveData() { return loadingLiveData; }
    public LiveData<String> getErrorLiveData() { return errorLiveData; }
    public LiveData<Boolean> getIsOfflineLiveData() { return isOfflineLiveData; }

    // ===================== ARTICLES =====================

    public void fetchArticles(int offset) {
        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            loadArticlesFromDb("articles");
            isOfflineLiveData.postValue(true);
            return;
        }
        isOfflineLiveData.postValue(false);
        loadingLiveData.postValue(true);

        RetrofitClient.getInstance().getApiService()
                .getArticles(20, offset, "-published_at")
                .enqueue(new Callback<ArticleResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ArticleResponse> call,
                                           @NonNull Response<ArticleResponse> response) {
                        loadingLiveData.postValue(false);
                        if (response.isSuccessful() && response.body() != null) {
                            List<Article> articles = response.body().getResults();
                            articlesLiveData.postValue(articles);
                            // Save to SQLite on background thread
                            executor.execute(() -> dbHelper.saveArticles(articles, "articles"));
                        } else {
                            errorLiveData.postValue("Failed to load articles");
                            loadArticlesFromDb("articles");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ArticleResponse> call, @NonNull Throwable t) {
                        loadingLiveData.postValue(false);
                        errorLiveData.postValue(t.getMessage());
                        loadArticlesFromDb("articles");
                        isOfflineLiveData.postValue(true);
                    }
                });
    }

    public void fetchBlogs(int offset) {
        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            loadArticlesFromDb("blogs");
            isOfflineLiveData.postValue(true);
            return;
        }
        isOfflineLiveData.postValue(false);
        loadingLiveData.postValue(true);

        RetrofitClient.getInstance().getApiService()
                .getBlogs(20, offset)
                .enqueue(new Callback<ArticleResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ArticleResponse> call,
                                           @NonNull Response<ArticleResponse> response) {
                        loadingLiveData.postValue(false);
                        if (response.isSuccessful() && response.body() != null) {
                            List<Article> blogs = response.body().getResults();
                            blogsLiveData.postValue(blogs);
                            executor.execute(() -> dbHelper.saveArticles(blogs, "blogs"));
                        } else {
                            errorLiveData.postValue("Failed to load blogs");
                            loadBlogsFromDb();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ArticleResponse> call, @NonNull Throwable t) {
                        loadingLiveData.postValue(false);
                        errorLiveData.postValue(t.getMessage());
                        loadBlogsFromDb();
                        isOfflineLiveData.postValue(true);
                    }
                });
    }

    public void fetchReports(int offset) {
        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            loadArticlesFromDb("reports");
            isOfflineLiveData.postValue(true);
            return;
        }
        isOfflineLiveData.postValue(false);
        loadingLiveData.postValue(true);

        RetrofitClient.getInstance().getApiService()
                .getReports(20, offset)
                .enqueue(new Callback<ArticleResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ArticleResponse> call,
                                           @NonNull Response<ArticleResponse> response) {
                        loadingLiveData.postValue(false);
                        if (response.isSuccessful() && response.body() != null) {
                            List<Article> reports = response.body().getResults();
                            reportsLiveData.postValue(reports);
                            executor.execute(() -> dbHelper.saveArticles(reports, "reports"));
                        } else {
                            errorLiveData.postValue("Failed to load reports");
                            loadReportsFromDb();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ArticleResponse> call, @NonNull Throwable t) {
                        loadingLiveData.postValue(false);
                        errorLiveData.postValue(t.getMessage());
                        loadReportsFromDb();
                        isOfflineLiveData.postValue(true);
                    }
                });
    }

    public void searchArticles(String query) {
        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            errorLiveData.postValue("No internet connection");
            return;
        }
        loadingLiveData.postValue(true);

        RetrofitClient.getInstance().getApiService()
                .searchArticles(query, 20, 0)
                .enqueue(new Callback<ArticleResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ArticleResponse> call,
                                           @NonNull Response<ArticleResponse> response) {
                        loadingLiveData.postValue(false);
                        if (response.isSuccessful() && response.body() != null) {
                            articlesLiveData.postValue(response.body().getResults());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ArticleResponse> call, @NonNull Throwable t) {
                        loadingLiveData.postValue(false);
                        errorLiveData.postValue(t.getMessage());
                    }
                });
    }

    // ===================== LOCAL DB =====================

    private void loadArticlesFromDb(String type) {
        executor.execute(() -> {
            List<Article> cached = dbHelper.getArticlesByType(type);
            articlesLiveData.postValue(cached);
        });
    }

    private void loadBlogsFromDb() {
        executor.execute(() -> {
            List<Article> cached = dbHelper.getArticlesByType("blogs");
            blogsLiveData.postValue(cached);
        });
    }

    private void loadReportsFromDb() {
        executor.execute(() -> {
            List<Article> cached = dbHelper.getArticlesByType("reports");
            reportsLiveData.postValue(cached);
        });
    }

    // ===================== BOOKMARKS =====================

    public void loadBookmarks() {
        executor.execute(() -> {
            List<Article> bookmarks = dbHelper.getAllBookmarks();
            bookmarksLiveData.postValue(bookmarks);
        });
    }

    public void toggleBookmark(Article article) {
        executor.execute(() -> {
            if (dbHelper.isBookmarked(article.getId())) {
                dbHelper.removeBookmark(article.getId());
            } else {
                dbHelper.addBookmark(article);
            }
            loadBookmarks();
        });
    }

    public boolean isBookmarked(int articleId) {
        return dbHelper.isBookmarked(articleId);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}

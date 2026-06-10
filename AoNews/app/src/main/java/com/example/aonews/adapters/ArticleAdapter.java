package com.example.aonews.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.aonews.R;
import com.example.aonews.models.Article;
import com.example.aonews.utils.DateUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import java.util.ArrayList;
import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {

    private List<Article> articles = new ArrayList<>();
    private final Context context;
    private OnArticleClickListener listener;

    public interface OnArticleClickListener {
        void onArticleClick(Article article, View sharedView);
    }

    public ArticleAdapter(Context context) {
        this.context = context;
    }

    public void setOnArticleClickListener(OnArticleClickListener listener) {
        this.listener = listener;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles != null ? articles : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addArticles(List<Article> newArticles) {
        int startPos = articles.size();
        articles.addAll(newArticles);
        notifyItemRangeInserted(startPos, newArticles.size());
    }

    public void clearArticles() {
        articles.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_article, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        Article article = articles.get(position);
        holder.bind(article);
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    class ArticleViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final ImageView ivThumbnail;
        private final TextView tvTitle;
        private final TextView tvNewsSite;
        private final TextView tvDate;
        private final TextView tvSummary;

        ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_article);
            ivThumbnail = itemView.findViewById(R.id.iv_thumbnail);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvNewsSite = itemView.findViewById(R.id.tv_news_site);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvSummary = itemView.findViewById(R.id.tv_summary);
        }

        void bind(Article article) {
            tvTitle.setText(article.getTitle());
            tvNewsSite.setText(article.getNewsSite() != null ? article.getNewsSite() : "Unknown");
            tvDate.setText(DateUtils.getTimeAgo(article.getPublishedAt()));
            tvSummary.setText(article.getSummary());

            // Load image with Glide
            if (article.getImageUrl() != null && !article.getImageUrl().isEmpty()) {
                Glide.with(context)
                        .load(article.getImageUrl())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .placeholder(R.drawable.placeholder_space)
                        .error(R.drawable.placeholder_space)
                        .centerCrop()
                        .into(ivThumbnail);
            } else {
                ivThumbnail.setImageResource(R.drawable.placeholder_space);
            }

            // Click listener
            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onArticleClick(article, ivThumbnail);
                }
            });
        }
    }
}

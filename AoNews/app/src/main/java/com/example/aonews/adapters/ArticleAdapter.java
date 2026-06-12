package com.example.aonews.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.aonews.R;
import com.example.aonews.database.DatabaseHelper;
import com.example.aonews.models.Article;
import com.example.aonews.utils.DateUtils;
import com.example.aonews.utils.SpaceFactProvider;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import java.util.ArrayList;
import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_FACT = 0;
    private static final int TYPE_ARTICLE = 1;

    private List<Article> articles = new ArrayList<>();
    private final Context context;
    private OnArticleClickListener listener;
    private boolean showFact = false;
    private final DatabaseHelper dbHelper;

    public interface OnArticleClickListener {
        void onArticleClick(Article article, View sharedView);
    }

    public ArticleAdapter(Context context) {
        this.context = context;
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    public void setShowFact(boolean showFact) {
        this.showFact = showFact;
        notifyDataSetChanged();
    }

    public void setOnArticleClickListener(OnArticleClickListener listener) {
        this.listener = listener;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles != null ? articles : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addArticles(List<Article> newArticles) {
        int startPos = getItemCount();
        articles.addAll(newArticles);
        notifyItemRangeInserted(startPos, newArticles.size());
    }

    public void clearArticles() {
        articles.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 && showFact) ? TYPE_FACT : TYPE_ARTICLE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_FACT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_daily_fact, parent, false);
            return new FactViewHolder(view, dbHelper);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_article, parent, false);
            return new ArticleViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FactViewHolder) {
            ((FactViewHolder) holder).bind();
            setAnimation(holder.itemView);
        } else if (holder instanceof ArticleViewHolder) {
            int articlePos = showFact ? position - 1 : position;
            if (articlePos < articles.size()) {
                Article article = articles.get(articlePos);
                ((ArticleViewHolder) holder).bind(article);
            }
        }
    }

    private void setAnimation(View viewToAnimate) {
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        animation.setDuration(800);
        viewToAnimate.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        int count = articles.size();
        if (showFact) count++;
        return count;
    }

    static class FactViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvFactContent;
        private final View cardView;
        private final ImageButton btnShareFact;
        private final ImageButton btnFavoriteFact;
        private final DatabaseHelper dbHelper;

        FactViewHolder(@NonNull View itemView, DatabaseHelper dbHelper) {
            super(itemView);
            this.dbHelper = dbHelper;
            tvFactContent = itemView.findViewById(R.id.tv_fact_content);
            cardView = itemView.findViewById(R.id.card_daily_fact);
            btnShareFact = itemView.findViewById(R.id.btn_share_fact);
            btnFavoriteFact = itemView.findViewById(R.id.btn_favorite_fact);
        }

        void bind() {
            String currentFact = SpaceFactProvider.getDailyFact();
            updateUI(currentFact);
            
            cardView.setOnClickListener(v -> {
                Animation fadeIn = AnimationUtils.loadAnimation(v.getContext(), android.R.anim.fade_in);
                String newFact = SpaceFactProvider.getRandomFact();
                tvFactContent.startAnimation(fadeIn);
                updateUI(newFact);
            });

            btnShareFact.setOnClickListener(v -> {
                String fact = tvFactContent.getText().toString();
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Space Fact of the Day");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "🚀 Space Fact of the Day:\n\n" + fact + "\n\nShared via AoNews");
                v.getContext().startActivity(Intent.createChooser(shareIntent, "Share fact via"));
            });

            btnFavoriteFact.setOnClickListener(v -> {
                String fact = tvFactContent.getText().toString();
                if (dbHelper.isFactFavorite(fact)) {
                    dbHelper.removeFavoriteFact(fact);
                    btnFavoriteFact.setImageResource(R.drawable.ic_bookmark_outline);
                    Toast.makeText(v.getContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                } else {
                    dbHelper.addFavoriteFact(fact);
                    btnFavoriteFact.setImageResource(R.drawable.ic_bookmark_filled);
                    Toast.makeText(v.getContext(), "Saved to favorites!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void updateUI(String fact) {
            tvFactContent.setText(fact);
            boolean isFav = dbHelper.isFactFavorite(fact);
            btnFavoriteFact.setImageResource(isFav ? R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark_outline);
        }
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

            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onArticleClick(article, ivThumbnail);
                }
            });
        }
    }
}

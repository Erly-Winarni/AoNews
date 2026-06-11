package com.example.aonews.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.aonews.models.Article;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "aonews.db";
    private static final int DATABASE_VERSION = 2;

    // Table Articles
    public static final String TABLE_ARTICLES = "articles";
    public static final String COL_ID = "id";
    public static final String COL_TITLE = "title";
    public static final String COL_URL = "url";
    public static final String COL_IMAGE_URL = "image_url";
    public static final String COL_NEWS_SITE = "news_site";
    public static final String COL_SUMMARY = "summary";
    public static final String COL_PUBLISHED_AT = "published_at";
    public static final String COL_UPDATED_AT = "updated_at";
    public static final String COL_TYPE = "type"; // articles, blogs, reports

    // Table Bookmarks
    public static final String TABLE_BOOKMARKS = "bookmarks";

    // Table Favorite Facts
    public static final String TABLE_FACTS = "favorite_facts";
    public static final String COL_FACT_TEXT = "fact_text";

    private static final String CREATE_ARTICLES_TABLE =
            "CREATE TABLE " + TABLE_ARTICLES + " (" +
            COL_ID + " INTEGER PRIMARY KEY, " +
            COL_TITLE + " TEXT NOT NULL, " +
            COL_URL + " TEXT, " +
            COL_IMAGE_URL + " TEXT, " +
            COL_NEWS_SITE + " TEXT, " +
            COL_SUMMARY + " TEXT, " +
            COL_PUBLISHED_AT + " TEXT, " +
            COL_UPDATED_AT + " TEXT, " +
            COL_TYPE + " TEXT DEFAULT 'articles'" +
            ")";

    private static final String CREATE_BOOKMARKS_TABLE =
            "CREATE TABLE " + TABLE_BOOKMARKS + " (" +
            COL_ID + " INTEGER PRIMARY KEY, " +
            COL_TITLE + " TEXT NOT NULL, " +
            COL_URL + " TEXT, " +
            COL_IMAGE_URL + " TEXT, " +
            COL_NEWS_SITE + " TEXT, " +
            COL_SUMMARY + " TEXT, " +
            COL_PUBLISHED_AT + " TEXT, " +
            COL_UPDATED_AT + " TEXT" +
            ")";

    private static final String CREATE_FACTS_TABLE =
            "CREATE TABLE " + TABLE_FACTS + " (" +
            COL_FACT_TEXT + " TEXT PRIMARY KEY" +
            ")";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ARTICLES_TABLE);
        db.execSQL(CREATE_BOOKMARKS_TABLE);
        db.execSQL(CREATE_FACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL(CREATE_FACTS_TABLE);
        } else {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICLES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKMARKS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_FACTS);
            onCreate(db);
        }
    }

    // ===================== ARTICLES =====================

    public void saveArticles(List<Article> articles, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_ARTICLES, COL_TYPE + "=?", new String[]{type});
            for (Article article : articles) {
                ContentValues values = new ContentValues();
                values.put(COL_ID, article.getId());
                values.put(COL_TITLE, article.getTitle());
                values.put(COL_URL, article.getUrl());
                values.put(COL_IMAGE_URL, article.getImageUrl());
                values.put(COL_NEWS_SITE, article.getNewsSite());
                values.put(COL_SUMMARY, article.getSummary());
                values.put(COL_PUBLISHED_AT, article.getPublishedAt());
                values.put(COL_UPDATED_AT, article.getUpdatedAt());
                values.put(COL_TYPE, type);
                db.insertWithOnConflict(TABLE_ARTICLES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public List<Article> getArticlesByType(String type) {
        List<Article> articles = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ARTICLES, null,
                COL_TYPE + "=?", new String[]{type},
                null, null, COL_PUBLISHED_AT + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                articles.add(cursorToArticle(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return articles;
    }

    // ===================== BOOKMARKS =====================

    public boolean addBookmark(Article article) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ID, article.getId());
        values.put(COL_TITLE, article.getTitle());
        values.put(COL_URL, article.getUrl());
        values.put(COL_IMAGE_URL, article.getImageUrl());
        values.put(COL_NEWS_SITE, article.getNewsSite());
        values.put(COL_SUMMARY, article.getSummary());
        values.put(COL_PUBLISHED_AT, article.getPublishedAt());
        values.put(COL_UPDATED_AT, article.getUpdatedAt());
        long result = db.insertWithOnConflict(TABLE_BOOKMARKS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        return result != -1;
    }

    public boolean removeBookmark(int articleId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_BOOKMARKS, COL_ID + "=?",
                new String[]{String.valueOf(articleId)});
        return rows > 0;
    }

    public boolean isBookmarked(int articleId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BOOKMARKS, new String[]{COL_ID},
                COL_ID + "=?", new String[]{String.valueOf(articleId)},
                null, null, null);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        return exists;
    }

    public List<Article> getAllBookmarks() {
        List<Article> articles = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BOOKMARKS, null, null, null,
                null, null, COL_PUBLISHED_AT + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                articles.add(cursorToArticle(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return articles;
    }

    // ===================== FAVORITE FACTS =====================

    public boolean addFavoriteFact(String factText) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FACT_TEXT, factText);
        long result = db.insertWithOnConflict(TABLE_FACTS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        return result != -1;
    }

    public boolean removeFavoriteFact(String factText) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_FACTS, COL_FACT_TEXT + "=?", new String[]{factText});
        return rows > 0;
    }

    public boolean isFactFavorite(String factText) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FACTS, null,
                COL_FACT_TEXT + "=?", new String[]{factText},
                null, null, null);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        return exists;
    }

    public List<String> getAllFavoriteFacts() {
        List<String> facts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FACTS, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                facts.add(cursor.getString(cursor.getColumnIndexOrThrow(COL_FACT_TEXT)));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return facts;
    }

    private Article cursorToArticle(Cursor cursor) {
        Article article = new Article();
        article.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
        article.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)));
        article.setUrl(cursor.getString(cursor.getColumnIndexOrThrow(COL_URL)));
        article.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE_URL)));
        article.setNewsSite(cursor.getString(cursor.getColumnIndexOrThrow(COL_NEWS_SITE)));
        article.setSummary(cursor.getString(cursor.getColumnIndexOrThrow(COL_SUMMARY)));
        article.setPublishedAt(cursor.getString(cursor.getColumnIndexOrThrow(COL_PUBLISHED_AT)));
        article.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COL_UPDATED_AT)));
        return article;
    }
}

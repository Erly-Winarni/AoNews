# 🚀 AoNews — Space News Android App

> **Aplikasi berita luar angkasa berbasis Android (Java)**
> Mengambil data real-time dari [Spaceflight News API v4](https://api.spaceflightnewsapi.net/v4/docs/) dengan dukungan mode offline, dark/light theme, dan penyimpanan lokal SQLite.

---

## 📑 Daftar Isi

1. [Informasi Aplikasi](#-informasi-aplikasi)
2. [Checklist Penilaian](#-checklist-penilaian)
3. [Cara Setup dan Menjalankan](#-cara-setup-dan-menjalankan)
4. [Struktur Proyek](#-struktur-proyek)
5. [Arsitektur & Alur Data](#-arsitektur--alur-data)
6. [API Endpoint](#-api-endpoint)
7. [Skema Database SQLite](#-skema-database-sqlite)
8. [Tema Aplikasi](#-tema-aplikasi)
9. [Dependencies](#-dependencies)
10. [Troubleshooting](#-troubleshooting)

---

## 📱 Informasi Aplikasi

| Atribut | Detail |
|---|---|
| **Nama Aplikasi** | AoNews |
| **Package Name** | `com.example.aonews` |
| **Bahasa** | Java |
| **IDE** | Android Studio |
| **Compile SDK** | 36 |
| **Version** | 1.0 |
| **API Sumber** | https://api.spaceflightnewsapi.net/v4/ |
| **Tema** | Berita & Informasi Luar Angkasa (Space) |

---

## ✅ Checklist Penilaian

### 1. Activity

| Kriteria | Status | Implementasi |
|---|---|---|
| Minimal 2 Activity berbeda | ✅ | `SplashActivity`, `MainActivity`, `DetailActivity`, `SearchActivity` |

### 2. Intent

| Kriteria | Status | Implementasi |
|---|---|---|
| Intent untuk berpindah antar Activity | ✅ | Fragment → `DetailActivity` via `Intent.putExtra()` (kirim data artikel) |
| Intent untuk komunikasi data | ✅ | `DetailActivity` menerima: `id`, `title`, `url`, `image`, `site`, `summary`, `date` |

### 3. RecyclerView

| Kriteria | Status | Implementasi |
|---|---|---|
| Gunakan RecyclerView untuk menampilkan data | ✅ | `ArticleAdapter` + `RecyclerView` di semua 4 fragment |
| ViewHolder pattern | ✅ | `ArticleViewHolder` dengan view binding manual |

### 4. Fragment & Navigation

| Kriteria | Status | Implementasi |
|---|---|---|
| Minimal 2 Fragment | ✅ | 4 fragment: `NewsFragment`, `BlogsFragment`, `ReportsFragment`, `BookmarksFragment` |
| Navigation Component | ✅ | `nav_graph.xml` + `NavHostFragment` + `NavigationUI.setupWithNavController()` |

### 5. Background Thread

| Kriteria | Status | Implementasi |
|---|---|---|
| Operasi di background menggunakan Executor / Handler | ✅ | `ExecutorService` (3 thread pool) di `ArticleViewModel` untuk semua operasi SQLite |

### 6. Networking

| Kriteria | Status | Implementasi |
|---|---|---|
| Retrofit untuk ambil data dari API | ✅ | `RetrofitClient` singleton + `SpaceNewsApiService` interface |
| Data ditampilkan di aplikasi | ✅ | LiveData → RecyclerView di semua fragment |
| Tombol refresh saat gagal / offline | ✅ | `btn_retry` + `SwipeRefreshLayout` di setiap fragment |

### 7. Local Data Persistent

| Kriteria | Status | Implementasi |
|---|---|---|
| SQLite untuk menyimpan data lokal | ✅ | `DatabaseHelper` tabel `articles` (cache) + tabel `bookmarks` |
| SharedPreferences untuk data lokal | ✅ | Menyimpan preferensi tema (dark/light) |
| Data tampil saat offline | ✅ | Auto-load dari SQLite + banner "Offline" |
| Dua tema (Dark / Light) | ✅ | `AppCompatDelegate` + `values-night/themes.xml`, toggle via toolbar |

---

## 🛠️ Cara Setup dan Menjalankan

### Prasyarat

- Android Studio
- JDK 21
- Android SDK **API 36** ter-install
- Koneksi internet (untuk sinkronisasi Gradle pertama kali)

### Langkah 1 — Ekstrak & Buka Proyek

```
1. Ekstrak file AoNews.zip
2. Buka Android Studio
3. File → Open → pilih folder AoNews/
4. Tunggu indexing selesai
```

### Langkah 2 — Sinkronisasi Gradle

```
File → Sync Project with Gradle Files
```

Atau klik tombol **Sync Now** pada notifikasi yang muncul di bagian atas editor.

### Langkah 3 — Cek SDK

```
Tools → SDK Manager
```

Pastikan **(API 36)** sudah ter-install. Jika belum, centang dan klik **Apply**.

### Langkah 4 — Jalankan

```
Run → Run 'app'   (atau tekan Shift+F10)
```

Pilih emulator atau hubungkan perangkat fisik dengan USB Debugging aktif.

---

## 📁 Struktur Proyek

```
AoNews/
├── app/
│   ├── build.gradle
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/aonews/app/
│       │   ├── AoNewsApplication.java
│       │   ├── activities/
│       │   │   ├── SplashActivity.java
│       │   │   ├── MainActivity.java
│       │   │   ├── DetailActivity.java
│       │   │   └── SearchActivity.java
│       │   ├── fragments/
│       │   │   ├── NewsFragment.java
│       │   │   ├── BlogsFragment.java
│       │   │   ├── ReportsFragment.java
│       │   │   └── BookmarksFragment.java
│       │   ├── adapters/
│       │   │   └── ArticleAdapter.java
│       │   ├── viewmodels/
│       │   │   └── ArticleViewModel.java
│       │   ├── network/
│       │   │   ├── RetrofitClient.java
│       │   │   └── SpaceNewsApiService.java
│       │   ├── database/
│       │   │   └── DatabaseHelper.java
│       │   ├── models/
│       │   │   ├── Article.java
│       │   │   └── ArticleResponse.java
│       │   └── utils/
│       │       ├── NetworkUtils.java
│       │       └── DateUtils.java
│       └── res/
│           ├── layout/           (9 file XML layout)
│           ├── navigation/       (nav_graph.xml)
│           ├── menu/             (3 file menu)
│           ├── drawable/         (12 vector icon + placeholder)
│           ├── anim/             (4 animasi transisi)
│           ├── values/           (colors, strings, themes, dimens)
│           └── values-night/     (themes dark)
├── build.gradle
├── settings.gradle
├── gradle.properties
└── README.md
```

---

## 🔄 Arsitektur & Alur Data

### Pola: MVVM + Repository Pattern (sederhana)

```
┌─────────────────────────────────────────────────────────┐
│                      UI Layer                           │
│  Fragment / Activity  →  observe LiveData               │
└──────────────────────────┬──────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────┐
│                   ViewModel Layer                       │
│  ArticleViewModel  →  LiveData<List<Article>>           │
│  ExecutorService   →  background thread untuk SQLite    │
└────────────┬──────────────────────────┬─────────────────┘
             │                          │
┌────────────▼──────────┐  ┌────────────▼──────────────────┐
│   Network Layer       │  │   Database Layer              │
│   Retrofit + OkHttp   │  │   SQLite (DatabaseHelper)     │
│   SpaceNewsApiService │  │   Tabel: articles, bookmarks  │
└───────────────────────┘  └───────────────────────────────┘
```

### Alur Saat Online

```
Fragment.onViewCreated()
    └─▶ ViewModel.fetchArticles(offset=0)
            └─▶ NetworkUtils.isNetworkAvailable() = TRUE
                    └─▶ Retrofit.getArticles() [Main Thread — async]
                            └─▶ onResponse()
                                    ├─▶ LiveData.postValue(articles)   → UI update
                                    └─▶ Executor.execute {             → Background
                                            DatabaseHelper.saveArticles()
                                        }
```

### Alur Saat Offline

```
Fragment.onViewCreated()
    └─▶ ViewModel.fetchArticles(offset=0)
            └─▶ NetworkUtils.isNetworkAvailable() = FALSE
                    ├─▶ isOfflineLiveData.postValue(TRUE) → tampil banner orange
                    └─▶ Executor.execute {                → Background
                            DatabaseHelper.getArticlesByType("articles")
                            LiveData.postValue(cachedList)
                        }
```

---

## 📡 API Endpoint

**Base URL:** `https://api.spaceflightnewsapi.net/v4/`

| Method | Endpoint | Parameter | Digunakan di |
|---|---|---|---|
| GET | `/articles/` | `limit=20`, `offset=N`, `ordering=-published_at` | NewsFragment |
| GET | `/blogs/` | `limit=20`, `offset=N` | BlogsFragment |
| GET | `/reports/` | `limit=20`, `offset=N` | ReportsFragment |
| GET | `/articles/` | `search=query`, `limit=20`, `offset=0` | SearchActivity |

### Contoh Response

```json
{
  "count": 21523,
  "next": "https://api.spaceflightnewsapi.net/v4/articles/?limit=20&offset=20",
  "previous": null,
  "results": [
    {
      "id": 32123,
      "title": "SpaceX launches Starship on 5th test flight",
      "url": "https://spacenews.com/...",
      "image_url": "https://spacenews.com/wp-content/uploads/...",
      "news_site": "SpaceNews",
      "summary": "SpaceX successfully launched...",
      "published_at": "2024-10-13T14:00:00Z",
      "updated_at": "2024-10-13T15:30:00Z"
    }
  ]
}
```

---

## 💾 Skema Database SQLite

**Nama database:** `aonews.db`  
**Versi:** 1

### Tabel `articles` — Cache Data API

| Kolom | Tipe | Keterangan |
|---|---|---|
| `id` | INTEGER PRIMARY KEY | ID artikel dari API |
| `title` | TEXT NOT NULL | Judul artikel |
| `url` | TEXT | URL artikel asli |
| `image_url` | TEXT | URL gambar thumbnail |
| `news_site` | TEXT | Nama sumber berita |
| `summary` | TEXT | Ringkasan artikel |
| `published_at` | TEXT | Tanggal terbit (ISO 8601) |
| `updated_at` | TEXT | Tanggal update terakhir |
| `type` | TEXT | Kategori: `articles` / `blogs` / `reports` |

> Cache di-replace setiap kali fetch berhasil. Data lama terhapus per tipe.

### Tabel `bookmarks` — Artikel Tersimpan User

| Kolom | Tipe | Keterangan |
|---|---|---|
| `id` | INTEGER PRIMARY KEY | ID artikel |
| `title` | TEXT NOT NULL | Judul |
| `url` | TEXT | URL |
| `image_url` | TEXT | URL gambar |
| `news_site` | TEXT | Sumber |
| `summary` | TEXT | Ringkasan |
| `published_at` | TEXT | Tanggal terbit |
| `updated_at` | TEXT | Tanggal update |

> Bookmark bersifat permanen — tidak terhapus oleh refresh cache.

### SharedPreferences

| Key | Tipe | Default | Keterangan |
|---|---|---|---|
| `dark_mode` | Boolean | `true` | Preferensi tema gelap/terang |

---

## 🎨 Tema Aplikasi

### Dark Mode — Space Theme (Default)

| Elemen | Warna | Hex |
|---|---|---|
| Background | Deep Space Black | `#0A0E1A` |
| Surface (Card) | Dark Navy | `#111827` |
| Surface Variant | `#1E2433` | |
| Text Utama | Off White | `#E8ECF4` |
| Text Sekunder | Muted Blue | `#9BA3BC` |
| Aksen Primer | Space Blue | `#4FC3F7` |
| Aksen Sekunder | Nebula Orange | `#FF8A65` |

### Light Mode

| Elemen | Warna | Hex |
|---|---|---|
| Background | Soft White | `#F5F7FF` |
| Surface (Card) | White | `#FFFFFF` |
| Text Utama | Dark Navy | `#0D1B2A` |
| Aksen Primer | Space Blue | `#4FC3F7` |

> Toggle tema melalui ikon **☀️ / 🌙** di toolbar kanan atas. Preferensi disimpan otomatis.

---

## 📦 Dependencies

| Library | Versi | Fungsi |
|---|---|---|
| `androidx.appcompat:appcompat` | 1.6.1 | Kompatibilitas AppCompat |
| `com.google.android.material:material` | 1.11.0 | Material Design 3 components |
| `androidx.constraintlayout:constraintlayout` | 2.1.4 | Layout fleksibel |
| `androidx.navigation:navigation-fragment` | 2.7.6 | Navigation Component (Fragment) |
| `androidx.navigation:navigation-ui` | 2.7.6 | Navigation Component (UI) |
| `androidx.recyclerview:recyclerview` | 1.3.2 | Daftar artikel |
| `com.squareup.retrofit2:retrofit` | 2.9.0 | HTTP client untuk API |
| `com.squareup.retrofit2:converter-gson` | 2.9.0 | Parse JSON → Object Java |
| `com.squareup.okhttp3:okhttp` | 4.12.0 | HTTP engine Retrofit |
| `com.squareup.okhttp3:logging-interceptor` | 4.12.0 | Log request/response HTTP |
| `com.github.bumptech.glide:glide` | 4.16.0 | Load & cache gambar |
| `androidx.lifecycle:lifecycle-viewmodel` | 2.7.0 | ViewModel |
| `androidx.lifecycle:lifecycle-livedata` | 2.7.0 | LiveData |
| `androidx.swiperefreshlayout:swiperefreshlayout` | 1.1.0 | Pull-to-refresh |
| `androidx.cardview:cardview` | 1.0.0 | Card UI untuk item artikel |

---

## 🔧 Troubleshooting

| Masalah | Solusi |
|---|---|
| **Gradle sync gagal** | `File → Invalidate Caches → Restart` |
| **"SDK not found"** | `Tools → SDK Manager` → install Android API 36 |
| **Gambar tidak muncul** | Pastikan internet aktif; API kadang lambat, coba refresh |
| **App crash saat buka** | Pastikan `AndroidManifest.xml` sudah include `<uses-permission INTERNET>` |
| **Navigation error** | Pastikan nama class fragment di `nav_graph.xml` sesuai package |
| **Data tidak muncul offline** | Buka app minimal sekali saat online agar cache tersimpan |
| **Toggle tema tidak berubah** | Pastikan `AoNewsApplication` terdaftar di `<application android:name=...>` |

---

## 👤 Informasi Pengembang

| | |
|---|---|
| **Aplikasi** | AoNews |
| **Platform** | Android (Java) |
| **IDE** | Android Studio |
| **API** | Spaceflight News API v4 |
| **Tahun** | 2026 |

---

*AoNews — Your Window to the Universe 🌌*

package com.example.aonews.utils;

import java.util.Calendar;
import java.util.Random;

public class SpaceFactProvider {
    private static final String[] FACTS = {
        "Satu hari di Venus lebih lama dibandingkan satu tahun di Bumi.",
        "Merkurius adalah planet terkecil di tata surya kita, hanya sedikit lebih besar dari Bulan.",
        "Matahari mencakup 99,86% massa di seluruh tata surya kita.",
        "Bintang neutron dapat berputar dengan kecepatan 600 rotasi per detik.",
        "Jejak kaki astronot di Bulan akan tetap ada di sana selama 100 juta tahun karena tidak ada angin.",
        "Jika dua potong logam sejenis bersentuhan di luar angkasa, mereka akan menyatu secara permanen (Cold Welding).",
        "Gunung berapi terbesar di tata surya adalah Olympus Mons yang terletak di Mars, tingginya 3x Everest.",
        "Ada lebih banyak bintang di alam semesta daripada butiran pasir di seluruh pantai di Bumi.",
        "Luar angkasa sepenuhnya sunyi karena tidak ada atmosfer untuk merambatkan gelombang suara.",
        "Venus adalah planet terpanas, dengan suhu permukaan rata-rata di atas 450°C.",
        "Saturnus memiliki 146 bulan, jumlah terbanyak dibandingkan planet lain di tata surya.",
        "Baju luar angkasa NASA berharga sekitar 12 juta dolar AS per unitnya.",
        "Bintik Merah Besar Jupiter adalah badai raksasa yang telah berlangsung selama lebih dari 300 tahun.",
        "Enceladus, salah satu bulan Saturnus, memantulkan 90% cahaya matahari yang diterimanya.",
        "Pusat galaksi kita terdeteksi beraroma seperti rum dan memiliki rasa seperti raspberry.",
        "Cahaya dari bintang yang kita lihat malam ini mungkin berasal dari ribuan tahun yang lalu.",
        "Di luar angkasa, tubuh astronot bisa bertambah tinggi hingga 5 cm karena tulang belakang merenggang.",
        "Jupiter sangat besar sehingga semua planet lain di tata surya bisa masuk ke dalamnya.",
        "Hujan berlian kemungkinan terjadi di planet raksasa gas seperti Neptunus dan Uranus."
    };

    public static String getDailyFact() {
        Calendar calendar = Calendar.getInstance();
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        return FACTS[dayOfYear % FACTS.length];
    }

    public static String getRandomFact() {
        return FACTS[new Random().nextInt(FACTS.length)];
    }
}

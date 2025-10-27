package com.example.prm392_cinema.Models;

// Giữ cho model đơn giản nhất có thể để đảm bảo việc ánh xạ
public class Movie {

    private int movieId;
    private String title;
    private String poster;
    private int duration; // Giữ lại duration vì MovieAdapter cũng dùng

    // Các trường khác từ API (description, director, ...) được tạm thời bỏ qua
    // để tập trung sửa lỗi hiển thị chính.

    // Getters
    public int getMovieId() {
        return movieId;
    }

    public String getTitle() {
        return title;
    }

    public String getPoster() {
        return poster;
    }

    public int getDuration() {
        return duration;
    }
}

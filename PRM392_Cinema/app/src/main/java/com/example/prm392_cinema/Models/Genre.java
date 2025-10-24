package com.example.prm392_cinema.Models;

import androidx.annotation.NonNull;
import com.google.gson.annotations.SerializedName;

public class Genre {
    private int genreId;
    @SerializedName("genreName")
    private String name;

    // Getters
    public int getGenreId() {
        return genreId;
    }

    public String getName() {
        return name;
    }

    // Setters
    public void setGenreId(int genreId) {
        this.genreId = genreId;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Override toString() so the spinner can display the genre name
    @NonNull
    @Override
    public String toString() {
        return name;
    }
}

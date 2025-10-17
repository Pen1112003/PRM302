package com.example.prm392_cinema.Models;

public class Movie {

    private int movieId;
    private String title;
    private String description;
    private String releaseDate;
    private float rating;
    private int duration;
    private String language;
    private String genre;
    private String linkTrailerl;
    private String posterUrl;

    public Movie(int movieId, String title, String description, String releaseDate, float rating, int duration, String language, String genre, String linkTrailerl, String getPosterUrl) {
        this.movieId = movieId;
        this.title = title;
        this.description = description;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.duration = duration;
        this.language = language;
        this.genre = genre;
        this.linkTrailerl = linkTrailerl;
        this.posterUrl = getPosterUrl;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String getPosterUrl) {
        this.posterUrl = getPosterUrl;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getLinkTrailerl() {
        return linkTrailerl;
    }

    public void setLinkTrailerl(String linkTrailerl) {
        this.linkTrailerl = linkTrailerl;
    }
}

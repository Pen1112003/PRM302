package com.example.prm392_cinema.Models;

public class Showtime {
    private int showtimeId;
    private int movieId;
    private int hallId;
    private int seatPrice;
    private String hallName;
    private String showDate; // String in ISO format for simplicity

    // Constructor, getters and setters

    public Showtime(int showtimeId, int movieId, int hallId, int seatPrice, String showDate,String hallName) {
        this.showtimeId = showtimeId;
        this.movieId = movieId;
        this.hallId = hallId;
        this.seatPrice = seatPrice;
        this.showDate = showDate;
        this.hallName = hallName;
    }

    public String getHallName() {
        return hallName;
    }

    public void setHallName(String hallName) {
        this.hallName = hallName;
    }

    public int getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(int showtimeId) {
        this.showtimeId = showtimeId;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public int getHallId() {
        return hallId;
    }

    public void setHallId(int hallId) {
        this.hallId = hallId;
    }

    public int getSeatPrice() {
        return seatPrice;
    }

    public void setSeatPrice(int seatPrice) {
        this.seatPrice = seatPrice;
    }

    public String getShowDate() {
        return showDate;
    }

    public void setShowDate(String showDate) {
        this.showDate = showDate;
    }
}

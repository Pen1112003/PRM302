package com.example.prm392_cinema.Models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TicketInfo {
    @SerializedName("movieTitle")
    private String movieTitle;
    @SerializedName("showtimeDate")
    private String showtimeDate;
    @SerializedName("showtimeTime")
    private String showtimeTime;
    @SerializedName("roomName")
    private String roomName;
    @SerializedName("seatNumbers")
    private List<String> seatNumbers;
    @SerializedName("price")
    private double price;
    @SerializedName("poster")
    private String poster;

    // Getters
    public String getMovieTitle() { return movieTitle; }
    public String getShowtimeDate() { return showtimeDate; }
    public String getShowtimeTime() { return showtimeTime; }
    public String getRoomName() { return roomName; }
    public List<String> getSeatNumbers() { return seatNumbers; }
    public double getPrice() { return price; }
    public String getPoster() { return poster; }
}

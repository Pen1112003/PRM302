package com.example.prm392_cinema.model;

import com.google.gson.annotations.SerializedName;

public class ShowtimeDetail {
    private int showtimeId;
    private int theaterId;
    private int movieId;
    private int roomId;
    private String showDate;
    @SerializedName("showTime1")
    private String showTime;
    private Room room;
    private Theater theater;

    public int getShowtimeId() {
        return showtimeId;
    }

    public int getTheaterId() {
        return theaterId;
    }

    public int getMovieId() {
        return movieId;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getShowDate() {
        return showDate;
    }

    public String getShowTime() {
        return showTime;
    }

    public Room getRoom() {
        return room;
    }

    public Theater getTheater() {
        return theater;
    }

    public static class Room {
        private String roomName;

        public String getRoomName() {
            return roomName;
        }
    }

    public static class Theater {
        private String theaterName;

        public String getTheaterName() {
            return theaterName;
        }
    }
}

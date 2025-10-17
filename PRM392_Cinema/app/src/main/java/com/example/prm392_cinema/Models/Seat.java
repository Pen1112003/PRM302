package com.example.prm392_cinema.Models;

import java.util.Objects;

public class Seat {
    public static final int STATUS_AVAILABLE = 0;
    public static final int STATUS_BOOKED = 1;
    public static final int STATUS_SELECTED = 2;

    public static final String TYPE_NORMAL = "Ghế Thường";
    public static final String TYPE_VIP = "Ghế VIP";

    //from model
    private int seatId;
    private String seatType;
    private int price;
    private boolean isSeat;
    private String name;
    private boolean isSold;
    private int colIndex;
    private int seatIndex;

    //from client
    private int status;  //checked, ordered, ready

    public Seat(int seatId, String seatType, int price, boolean isSeat, String name, boolean isSold, int colIndex, int seatIndex) {
        this.seatId = seatId;
        this.seatType = seatType;
        this.price = price;
        this.isSeat = isSeat;
        this.name = name;
        this.isSold = isSold;
        this.colIndex = colIndex;
        this.seatIndex = seatIndex;
        this.status = isSold ? STATUS_BOOKED : STATUS_AVAILABLE;
    }

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isSeat() {
        return isSeat;
    }

    public void setSeat(boolean seat) {
        isSeat = seat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSold() {
        return isSold;
    }

    public void setSold(boolean sold) {
        isSold = sold;
    }

    public int getColIndex() {
        return colIndex;
    }

    public void setColIndex(int colIndex) {
        this.colIndex = colIndex;
    }

    public int getSeatIndex() {
        return seatIndex;
    }

    public void setSeatIndex(int seatIndex) {
        this.seatIndex = seatIndex;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isBooked() {
        return status == STATUS_BOOKED;
    }

    public boolean isAvailable() {
        return status == STATUS_AVAILABLE;
    }

    public boolean isSelected() {
        return status == STATUS_SELECTED;
    }

    public boolean isNormal() {
        return Objects.equals(seatType, TYPE_NORMAL);
    }

    public boolean isVip() {
        return Objects.equals(seatType, TYPE_VIP);
    }
}
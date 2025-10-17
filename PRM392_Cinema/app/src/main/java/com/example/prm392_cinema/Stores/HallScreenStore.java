package com.example.prm392_cinema.Stores;

import com.example.prm392_cinema.Services.FabService;

import java.util.ArrayList;
import java.util.List;

public class HallScreenStore {
    public static int showTimeId;
    public static int movieId;
    public static String hallName;
    public static String showTime;

    public static List<Integer> listSeatId = new ArrayList<>();
    public static FabService.OrderFabDto orderFabDto = null;
}

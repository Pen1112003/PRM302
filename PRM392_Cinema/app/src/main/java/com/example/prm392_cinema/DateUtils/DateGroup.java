package com.example.prm392_cinema.DateUtils;

import com.example.prm392_cinema.Models.Showtime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class DateGroup {
    private String date;
    private List<Showtime> showtimes;

    public DateGroup(String date, List<Showtime> showtimes) {
        this.date = date;
        this.showtimes = showtimes;
    }

    public String getDate() {
        return date;
    }

    public String getFormatDate() {
        try {
            // Định dạng ban đầu của chuỗi ngày (yyyy/MM/dd)
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            // Định dạng ngày cần chuyển đổi (EEEE, dd/MM/yyyy)
            SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, dd/MM/yyyy", new Locale("vi", "VN"));

            // Chuyển chuỗi ngày thành đối tượng Date
            Date date = inputFormat.parse(getDate());

            // Định dạng lại ngày theo yêu cầu
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;  // Trả về null nếu có lỗi trong khi chuyển đổi
        }
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Showtime> getShowtimes() {
        return showtimes;
    }

    public void setShowtimes(List<Showtime> showtimes) {
        this.showtimes = showtimes;
    }

    public static List<DateGroup> groupShowtimesByDate(List<Showtime> showtimes) {
        Map<String, List<Showtime>> groupedByDate = new HashMap<>();

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat isoDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
        isoDateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        for (Showtime showtime : showtimes) {
            try {
                Date showDate = isoDateFormatter.parse(showtime.getShowDate());
                String dateKey = dateFormatter.format(showDate);
                assert showDate != null;
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(showDate);

                showtime.setShowDate(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));

                if (!groupedByDate.containsKey(dateKey)) {
                    groupedByDate.put(dateKey, new ArrayList<>());
                }
                groupedByDate.get(dateKey).add(showtime);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        List<DateGroup> dateGroups = new ArrayList<>();
        for (Map.Entry<String, List<Showtime>> entry : groupedByDate.entrySet()) {
            entry.getValue().sort(new Comparator<Showtime>() {
                @Override
                public int compare(Showtime o1, Showtime o2) {
                    return o1.getShowDate().compareTo(o2.getShowDate());
                }
            });

            dateGroups.add(new DateGroup(entry.getKey(), entry.getValue()));
        }

        dateGroups.sort(new Comparator<DateGroup>() {
            @Override
            public int compare(DateGroup o1, DateGroup o2) {
                return o1.date.compareTo(o2.date);
            }
        });

        return dateGroups;
    }
}

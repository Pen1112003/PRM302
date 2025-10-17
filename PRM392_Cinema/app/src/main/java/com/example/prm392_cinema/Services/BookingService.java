package com.example.prm392_cinema.Services;


import com.example.prm392_cinema.Models.Movie;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Headers;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface BookingService {
    @GET("/api/Booking/{bookingId}")
    Call<ResDTO> getBookingDetail(
            @Path("bookingId") String bookingId
    );

    @POST("/api/Booking/create-booking")
    Call<CreateBookingResponseDto> createBooking(@Body CreateBookingDto dto);

    @PUT("/api/Booking")
    Call<BookingDetailDTO> updateBookingStatus(
            @Query("bookingId") String bookingId,
            @Query("status") String status
    );

    public class CreateBookingDto {
        public int userId;
        public int showTimeId;
        public List<Integer> listSeatId;
        public int totalPrice;

        public CreateBookingDto(int userId, int showTimeId, List<Integer> listSeatId, int totalPrice) {
            this.userId = userId;
            this.showTimeId = showTimeId;
            this.listSeatId = listSeatId;
            this.totalPrice = totalPrice;
        }
    }

    public class CreateBookingResponseDto {
        public CreateBookingResponseResultDto result;
    }

    public class CreateBookingResponseResultDto {
        public CreateBookingResponseResultDataDto data;
    }

    public class CreateBookingResponseResultDataDto {
        public int bookingId;

    }

    @GET("/api/Booking/user/{userId}")
    Call<ResAllDTO> getBookings(
            @Path("userId") String userId
    );

    @PUT("/api/Booking/update-status-booking")
    Call<ResDTO> updateBookingStatus(
            @Body UpdateBookingStatusRequest request
    );

    public class UpdateBookingStatusRequest {
        private String bookingId;
        private int status;

        public UpdateBookingStatusRequest(String bookingId, int status) {
            this.bookingId = bookingId;
            this.status = status;
        }

    }

    public class ResAllDTO {
        public boolean success;
        public List<BookingDetailAllDTO> result;

    }

    public class ResDTO {
        public boolean success;
        public BookingDetailDTO result;

    }

    public class BookingDetailDTO {
        public int bookingId;
        public String userName;
        public String hallName;
        public String movieName;
        public String showDate;
        public String bookingDate;
        public ArrayList<SeatDetail> seatNames;
        public ArrayList<FabDetail> fabDetails;
        public String status;
        public int totalPrice;


    }

    public class BookingDetailAllDTO {
        public int bookingId;
        public String userId;
        public String showTimeId;
        public String bookingDate;
        public ArrayList<SeatDetail> bookingSeats;
        public ArrayList<FabDetail> bookingFoodBeverages;
        public String status;
        public int totalPrice;
        public int movieId;

        public UserDetail user;
        public ShowTimeDetail showtime;


    }

    public class FabDetail {
        public String foodName;
        public int amount;
        public int price;


    }

    public class SeatDetail {
        public String seatNumber;
        public String seatType;
        public int seatPrice;
        public int hallId;


    }

    public class UserDetail {
        public String fullName;
        public String email;


    }

    public class ShowTimeDetail {
        public String showDate;
        public HallDetail hall;
        public Movie movie;


        public class HallDetail {
            public String hallName;
            public int hallId;
            public int totalSeats;


        }
    }
}

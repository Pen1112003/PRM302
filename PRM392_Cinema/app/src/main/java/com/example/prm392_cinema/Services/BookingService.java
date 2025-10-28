package com.example.prm392_cinema.Services;

import com.example.prm392_cinema.Models.Movie;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BookingService {

    @POST("/api/Booking/book-ticket")
    Call<String> createBooking(@Body CreateBookingDto dto);

    @POST("/api/Booking/PaymentVNPAY") 
    Call<String> getPaymentUrl(@Query("transactionId") String transactionId);

    @GET("/api/Booking/search") 
    Call<List<BookingDetailAllDTO>> searchTickets(@Query("userId") String userId);

    @GET("/api/Booking/detail/{orderId}")
    Call<BookingDetailItemDTO> getBookingDetailById(@Path("orderId") int orderId);

    @PUT("/api/payment/update-order-status")
    Call<Void> updateOrderStatus(@Body UpdateOrderStatusDto dto);

    public class UpdateOrderStatusDto {
        public String transactionId;

        public UpdateOrderStatusDto(String transactionId) {
            this.transactionId = transactionId;
        }
    }

    public class CreateBookingDto {
        public int showtimeId;
        public String userId;
        public List<Integer> seatIds;
        public List<Integer> productIds;
        public List<Integer> quantity;
        public double totalPrice;
        public double ticketPrice;
        public int discountId;
        public String paymentMethod;

        public CreateBookingDto(int showtimeId, String userId, List<Integer> seatIds, List<Integer> productIds, List<Integer> quantity, double totalPrice, double ticketPrice, int discountId, String paymentMethod) {
            this.showtimeId = showtimeId;
            this.userId = userId;
            this.seatIds = seatIds;
            this.productIds = productIds;
            this.quantity = quantity;
            this.totalPrice = totalPrice;
            this.ticketPrice = ticketPrice;
            this.discountId = discountId;
            this.paymentMethod = paymentMethod;
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

    @GET("/api/Booking/{bookingId}")
    Call<ResDTO> getBookingDetail(@Path("bookingId") String bookingId);

    @PUT("/api/Booking")
    Call<BookingDetailDTO> updateBookingStatus(@Query("bookingId") String bookingId, @Query("status") String status);

    @GET("/api/Booking/user/{userId}")
    Call<ResAllDTO> getBookings(@Path("userId") String userId);

    @PUT("/api/Booking/update-status-booking")
    Call<ResDTO> updateBookingStatus(@Body UpdateBookingStatusRequest request);

    public class UpdateBookingStatusRequest {
        private String bookingId;
        private int status;

        public UpdateBookingStatusRequest(String bookingId, int status) {
            this.bookingId = bookingId;
            this.status = status;
        }
    }

    public class BookingDetailAllDTO {
        public int orderId;
        public String customerName;
        public String phoneNumber;
        public String email;
        public String movieTitle;
        public int movieId;
        public String showtime;
        public String roomName;
        public List<String> seats;
        public List<Integer> productIds;
        public List<Integer> quantity;
        public double totalAmount;
        public double ticketPrice;
        public int discountId;
        public String status;
        public String paymentMethod;
        public String paymentStatus;
        public String userId;
        public String description;
        public String director;
        public String cast;
        public String releaseDate;
        public int duration;
        public String language;
        public String poster;
        public String movieStatus;
        public int genreId;

        public int getOrderId() { return orderId; }
        public String getCustomerName() { return customerName; }
        public String getPhoneNumber() { return phoneNumber; }
        public String getEmail() { return email; }
        public String getMovieTitle() { return movieTitle; }
        public int getMovieId() { return movieId; }
        public String getShowtime() { return showtime; }
        public String getRoomName() { return roomName; }
        public List<String> getSeats() { return seats; }
        public List<Integer> getProductIds() { return productIds; }
        public List<Integer> getQuantity() { return quantity; }
        public double getTotalAmount() { return totalAmount; }
        public double getTicketPrice() { return ticketPrice; }
        public int getDiscountId() { return discountId; }
        public String getStatus() { return status; }
        public String getPaymentMethod() { return paymentMethod; }
        public String getPaymentStatus() { return paymentStatus; }
        public String getUserId() { return userId; }
        public String getDescription() { return description; }
        public String getDirector() { return director; }
        public String getCast() { return cast; }
        public String getReleaseDate() { return releaseDate; }
        public int getDuration() { return duration; }
        public String getLanguage() { return language; }
        public String getPoster() { return poster; }
        public String getMovieStatus() { return movieStatus; }
        public int getGenreId() { return genreId; }
    }

    public class BookingDetailItemDTO {
        public int orderId;
        public String customerName;
        public String phoneNumber;
        public String email;
        public String movieTitle;
        public int movieId;
        public ShowtimeDetailInItem showtime;
        public String roomName;
        public List<String> seats;
        public double ticketPrice;
        public double totalAmount;
        public String status;
        public String paymentMethod;
        public String orderDate;
        public String qrCode;
        public OrderInItem order;
        public String description;
        public String director;
        public String cast;
        public String releaseDate;
        public int duration;
        public String language;
        public String poster;
        public String movieStatus;
        public int genreId;
        public PaymentDetailInItem payment;
        public List<Integer> productIds;
        public List<Integer> quantity;

        public int getOrderId() { return orderId; }
        public String getCustomerName() { return customerName; }
        public String getPhoneNumber() { return phoneNumber; }
        public String getEmail() { return email; }
        public String getMovieTitle() { return movieTitle; }
        public int getMovieId() { return movieId; }
        public ShowtimeDetailInItem getShowtime() { return showtime; }
        public String getRoomName() { return roomName; }
        public List<String> getSeats() { return seats; }
        public double getTicketPrice() { return ticketPrice; }
        public double getTotalAmount() { return totalAmount; }
        public String getStatus() { return status; }
        public String getPaymentMethod() { return paymentMethod; }
        public String getOrderDate() { return orderDate; }
        public String getQrCode() { return qrCode; }
        public OrderInItem getOrder() { return order; }
        public String getDescription() { return description; }
        public String getDirector() { return director; }
        public String getCast() { return cast; }
        public String getReleaseDate() { return releaseDate; }
        public int getDuration() { return duration; }
        public String getLanguage() { return language; }
        public String getPoster() { return poster; }
        public String getMovieStatus() { return movieStatus; }
        public int getGenreId() { return genreId; }
        public PaymentDetailInItem getPayment() { return payment; }
        public List<Integer> getProductIds() { return productIds; }
        public List<Integer> getQuantity() { return quantity; }
    }

    public class ShowtimeDetailInItem {
        public int showtimeId;
        public String movieTitle;
        public int movieId;
        public String roomName;
        public String format;
        public String showDate;
        public String showTime;
    }

    public class OrderInItem {
        public int orderId;
        public int showtimeId;
        public String userId;
        public String orderDate;
        public double totalAmount;
        public String status;
        public Integer discountId;
        public List<TicketInOrderInItem> tickets;
        public List<PaymentInOrderInItem> payments;
        public int movieId;
    }

    public class TicketInOrderInItem {
        public int ticketId;
        public int orderId;
        public String movieTitle;
        public String showtime;
        public String roomName;
        public List<String> seatNumbers;
        public double price;
        public String status;
        public String userId;
        public String email;
    }

    public class PaymentInOrderInItem {
        public int paymentId;
        public String paymentMethod;
        public String transactionId;
        public String paymentDate;
        public String paymentStatus;
    }

    public class PaymentDetailInItem {
        public int paymentId;
        public String paymentMethod;
        public String transactionId;
        public String paymentDate;
        public String paymentStatus;
    }

    public class ResAllDTO {
        public boolean success;
        public List<BookingDetailAllDTO> result;

        public boolean isSuccess() { return success; }
        public List<BookingDetailAllDTO> getResult() { return result; }
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
        public List<SeatDetail> seatNames;
        public List<FabDetail> fabDetails;
        public String status;
        public int totalPrice;
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

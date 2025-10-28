package com.example.prm392_cinema.Models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TransactionDetails {
    @SerializedName("orderId")
    private int orderId;
    @SerializedName("orderDate")
    private String orderDate;
    @SerializedName("totalOrderAmount")
    private double totalOrderAmount;
    @SerializedName("orderStatus")
    private String orderStatus;
    @SerializedName("userId")
    private String userId;
    @SerializedName("userEmail")
    private String userEmail;
    @SerializedName("customerName")
    private String customerName;
    @SerializedName("phoneNumber")
    private String phoneNumber;
    @SerializedName("theaterName")
    private String theaterName;
    @SerializedName("paymentId")
    private int paymentId;
    @SerializedName("paymentDate")
    private String paymentDate;
    @SerializedName("paymentTotalAmount")
    private double paymentTotalAmount;
    @SerializedName("paymentDiscountAmount")
    private double paymentDiscountAmount;
    @SerializedName("paymentMethod")
    private String paymentMethod;
    @SerializedName("paymentStatus")
    private String paymentStatus;
    @SerializedName("transactionId")
    private String transactionId;
    @SerializedName("tickets")
    private List<TicketInfo> tickets;
    @SerializedName("products")
    private List<Product> products;

    // Getters
    public int getOrderId() { return orderId; }
    public String getOrderDate() { return orderDate; }
    public double getTotalOrderAmount() { return totalOrderAmount; }
    public String getOrderStatus() { return orderStatus; }
    public String getUserId() { return userId; }
    public String getUserEmail() { return userEmail; }
    public String getCustomerName() { return customerName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getTheaterName() { return theaterName; }
    public int getPaymentId() { return paymentId; }
    public String getPaymentDate() { return paymentDate; }
    public double getPaymentTotalAmount() { return paymentTotalAmount; }
    public double getPaymentDiscountAmount() { return paymentDiscountAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getTransactionId() { return transactionId; }
    public List<TicketInfo> getTickets() { return tickets; }
    public List<Product> getProducts() { return products; }
}

package com.example.prm392_cinema.Services;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FabService {
    @GET("/api/fab")
    Call<GetFabsResponseDto> getFabs();

    public class GetFabsResponseDto {
        public GetFabsResultResponseDto result;
    }

    public class GetFabsResultResponseDto {
        public List<FabDto> fABList;
    }

    public class FabDto {
        public int foodId;
        public String name;
        public String description;
        public int price;
    }


    @POST("/api/fab/order/{orderId}")
    Call<OrderFabsResponse> orderFabs(@Path("orderId") int orderId, @Body OrderFabDto dto);

    public class OrderFabsResponse{

    }

    public class OrderFabDto {
        public List<FabOrderDto> listFABOrder;

        public OrderFabDto(List<FabOrderDto> listFABOrder) {
            this.listFABOrder = listFABOrder;
        }
    }

    public class FabOrderDto {
        public int fABId;
        public int amount;

        public FabOrderDto(int fABId, int amount) {
            this.fABId = fABId;
            this.amount = amount;
        }
    }
}

package com.example.prm392_cinema.Services;

import com.example.prm392_cinema.Models.Genre;
import com.example.prm392_cinema.Models.Movie;
import com.example.prm392_cinema.Models.Showtime;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Path;

public interface MovieService {

    @GET("/api/Movie/AllMovie")
    Call<List<Movie>> getAllMovies();

    @GET("/api/Movie/FilterMovies")
    Call<List<Movie>> getFilteredMovies(
            @Query("status") String status,
            @Query("genreId") Integer genreId,
            @Query("theaterId") Integer theaterId,
            @Query("date") String date
    );

    @GET("/api/Genre/GetAllGenres")
    Call<List<Genre>> getAllGenres();

    @GET("/api/Movie/MovieById/{id}")
    Call<MovieDto> getMovieDetail(@Path("id") int id);

    // Updated DTO to match the API response
    public class MovieDto {
        public int movieId;
        public String title;
        public String description;
        public String director;
        public String cast;
        public String releaseDate;
        public int duration;
        public String language;
        public String status;
        public String bannerImage;
        public List<Integer> genreIds;
        public String trailer;

        @SerializedName("poster")
        public String poster;
    }

    @GET("/api/showtimes")
    Call<GetShowtimesResponse> getShowtimes(@Query("movieId") int movieId);

    public class GetShowtimesResponse {
        public GetShowtimesResponse2 result;
    }

    public class GetShowtimesResponse2 {
        public List<ShowtimeDto> data;
    }

    public class ShowtimeDto extends Showtime {
        public HallDto hall;

        public ShowtimeDto(int showtimeId, int movieId, int hallId, int seatPrice, String showDate, String hallNumber) {
            super(showtimeId, movieId, hallId, seatPrice, showDate, hallNumber);
        }
    }

    public class HallDto {
        public String hallName;
    }
}

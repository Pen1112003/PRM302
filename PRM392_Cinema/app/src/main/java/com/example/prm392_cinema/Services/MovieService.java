package com.example.prm392_cinema.Services;

import com.example.prm392_cinema.Models.Movie;
import com.example.prm392_cinema.Models.Showtime;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import retrofit2.http.Path;

public interface MovieService {
    @GET("/api/movies")
    Call<List<Movie>> getMovies(
            @Query("genre") String genre,
            @Query("language") String language
    );

    @GET("/api/movies/{id}")
    Call<MovieDto> getMovieDetail(@Path("id") int id);

    public class MovieDto {
        public int movieId;
        public String title;
        public String description;
        public String releaseDate;
        public int duration;
        public float rating;
        public String genre;
        public String language;
        public String linkTrailer;
        public String posterUrl;
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

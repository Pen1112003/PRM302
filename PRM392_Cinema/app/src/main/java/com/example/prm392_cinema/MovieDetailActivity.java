package com.example.prm392_cinema;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_cinema.Services.ApiClient;
import com.example.prm392_cinema.Services.MovieService;
import com.example.prm392_cinema.Stores.AuthStore;
import com.example.prm392_cinema.Stores.HallScreenStore;
import com.example.prm392_cinema.model.Showtime;
import com.example.prm392_cinema.model.TheaterShowtime;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailActivity extends AppCompatActivity implements DateAdapter.OnDateClickListener, ShowtimeAdapter.OnShowtimeClickListener {
    private RecyclerView datesRecyclerView, showtimesRecyclerView;
    private int movieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movie_detail);

        datesRecyclerView = findViewById(R.id.datesRecyclerView);
        datesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        showtimesRecyclerView = findViewById(R.id.showtimesRecyclerView);
        showtimesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (getIntent() == null) return;

        movieId = getIntent().getIntExtra("movieId", 0);
        HallScreenStore.movieId = movieId;
        getMovieDetail(movieId);

        setupDatesRecyclerView();

        findViewById(R.id.btnSignOut).setOnClickListener(v -> handleSignOut());
        findViewById(R.id.backIcon).setOnClickListener(v -> handleBack());
        (findViewById(R.id.btnHistory)).setOnClickListener(v -> navigateHistory());
    }

    private void setupDatesRecyclerView() {
        List<Calendar> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 7; i++) {
            dates.add((Calendar) calendar.clone());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        DateAdapter dateAdapter = new DateAdapter(this, dates, this);
        datesRecyclerView.setAdapter(dateAdapter);

        // Initial load for the current date
        onDateClick(dates.get(0));
    }

    @Override
    public void onDateClick(Calendar date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String selectedDate = dateFormat.format(date.getTime());
        getShowtimesForMovie(movieId, selectedDate);
    }

    @Override
    public void onShowtimeClick(Showtime showtime) {
        Intent intent = new Intent(this, HallActivity.class);
        intent.putExtra("showtimeId", showtime.getShowtimeId());
        startActivity(intent);
    }

    private void navigateHistory()
    {
        Intent intent = new Intent(MovieDetailActivity.this, HistoryOrder.class);
        startActivity(intent);
    }

    private void handleSignOut() {
        AuthStore.clear();
        Intent intent = new Intent(MovieDetailActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void handleBack() {
        finish();
    }

    private void playVideo(String videoUrl) {
        if (videoUrl == null || videoUrl.isEmpty()) {
            Toast.makeText(this, "Trailer không có sẵn.", Toast.LENGTH_SHORT).show();
            return;
        }

        FragmentManager fm = getSupportFragmentManager();
        VideoPlayerFragment videoPlayerFragment = VideoPlayerFragment.newInstance(videoUrl);
        videoPlayerFragment.show(fm, "fragment_video_player");
    }

    private void getMovieDetail(int movieId) {
        MovieService apiService = ApiClient.getRetrofitInstance().create(MovieService.class);

        Call<MovieService.MovieDto> call = apiService.getMovieDetail(movieId);
        call.enqueue(new Callback<MovieService.MovieDto>() {
            @Override
            public void onResponse(Call<MovieService.MovieDto> call, Response<MovieService.MovieDto> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(MovieDetailActivity.this, "Lỗi khi tải thông tin phim", Toast.LENGTH_SHORT).show();
                    return;
                }

                MovieService.MovieDto res = response.body();
                Log.d("MovieDetailActivity", "Poster URL: " + res.poster);
                Log.d("MovieDetailActivity", "Trailer URL: " + res.trailer);

                ((TextView) findViewById(R.id.title)).setText(res.title);
                ((TextView) findViewById(R.id.description)).setText(res.description);
                ((TextView) findViewById(R.id.release)).setText("Phát hành: " + res.releaseDate);
                ((TextView) findViewById(R.id.duration)).setText("Thời lượng: " + res.duration + " phút");

                Picasso.get()
                        .load(res.poster)
                        .placeholder(R.drawable.conan_movie) // Ảnh tạm
                        .error(R.drawable.conan_movie) // Ảnh khi có lỗi
                        .into((ImageView) findViewById(R.id.movieImg), new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d("Picasso", "Tải ảnh thành công!");
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.e("Picasso", "Lỗi khi tải ảnh: " + e.getMessage());
                                e.printStackTrace();
                            }
                        });

                LinearLayout buttonTrailer = findViewById(R.id.btnTrailer);
                buttonTrailer.setOnClickListener(v -> playVideo(res.trailer));
            }

            @Override
            public void onFailure(Call<MovieService.MovieDto> call, Throwable t) {
                Log.e("MovieDetailActivity", "Failed to get movie details: " + t.getMessage());
                Toast.makeText(MovieDetailActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getShowtimesForMovie(int movieId, String date) {
        Log.d("MovieDetailActivity", "Fetching showtimes for movieId: " + movieId + " and date: " + date);

        MovieService apiService = ApiClient.getRetrofitInstance().create(MovieService.class);
        Call<List<TheaterShowtime>> call = apiService.getShowtimesForMovie(movieId, date);
        call.enqueue(new Callback<List<TheaterShowtime>>() {
            @Override
            public void onResponse(Call<List<TheaterShowtime>> call, Response<List<TheaterShowtime>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("MovieDetailActivity", "Successfully fetched showtimes. Count: " + response.body().size());
                    List<TheaterShowtime> theaterShowtimes = response.body();
                    TheaterAdapter theaterAdapter = new TheaterAdapter(MovieDetailActivity.this, theaterShowtimes, MovieDetailActivity.this);
                    showtimesRecyclerView.setAdapter(theaterAdapter);
                } else {
                    Log.e("MovieDetailActivity", "Failed to fetch showtimes. Code: " + response.code() + ", Message: " + response.message());
                    // Clear the RecyclerView if there are no showtimes for the selected date
                    showtimesRecyclerView.setAdapter(null);
                }
            }

            @Override
            public void onFailure(Call<List<TheaterShowtime>> call, Throwable t) {
                Log.e("MovieDetailActivity", "Failed to get showtimes: " + t.getMessage(), t);
                Toast.makeText(MovieDetailActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                 showtimesRecyclerView.setAdapter(null);
            }
        });
    }
}

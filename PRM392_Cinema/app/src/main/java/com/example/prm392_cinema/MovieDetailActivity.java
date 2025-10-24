package com.example.prm392_cinema;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_cinema.Adapters.DateAdapter;
import com.example.prm392_cinema.Models.Showtime;
import com.example.prm392_cinema.Services.ApiClient;
import com.example.prm392_cinema.Services.MovieService;
import com.example.prm392_cinema.DateUtils.DateGroup;
import com.example.prm392_cinema.Stores.AuthStore;
import com.example.prm392_cinema.Stores.HallScreenStore;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movie_detail);

        recyclerView = findViewById(R.id.datesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (getIntent() == null) return;

        int movieId = getIntent().getIntExtra("movieId", 0);
        HallScreenStore.movieId = movieId;
        getMovieDetail(movieId);
        getShowtimes(this, movieId);

        findViewById(R.id.btnSignOut).setOnClickListener(v -> handleSignOut());
        findViewById(R.id.backIcon).setOnClickListener(v -> handleBack());
        (findViewById(R.id.btnHistory)).setOnClickListener(v -> navigateHistory());
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

    private void showVideoPopup(String videoUrl) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_video);
        PlayerControlView playerView = dialog.findViewById(R.id.playerView);
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));
        player.setMediaItem(mediaItem);
        player.prepare();
        player.setPlayWhenReady(true);
        dialog.setOnDismissListener(dialogInterface -> player.release());
        dialog.show();
    }

    private void getMovieDetail(int movieId) {
        MovieService apiService = ApiClient.getRetrofitInstance().create(MovieService.class);

        Call<MovieService.MovieDto> call = apiService.getMovieDetail(movieId);
        call.enqueue(new Callback<MovieService.MovieDto>() {
            @Override
            public void onResponse(Call<MovieService.MovieDto> call, Response<MovieService.MovieDto> response) {
                if (!response.isSuccessful() || response.body() == null) return;

                MovieService.MovieDto res = response.body();

                ((TextView) findViewById(R.id.title)).setText(res.title);
                ((TextView) findViewById(R.id.description)).setText(res.description);
                ((TextView) findViewById(R.id.release)).setText("Phát hành: " + res.releaseDate);
                ((TextView) findViewById(R.id.duration)).setText("Thời lượng: " + res.duration + " phút");
                // You may want to add a TextView for director and cast in your layout
                // ((TextView) findViewById(R.id.director)).setText("Đạo diễn: " + res.director);
                // ((TextView) findViewById(R.id.cast)).setText("Diễn viên: " + res.cast);

                Picasso.get().load(res.posterUrl).into((ImageView) findViewById(R.id.movieImg));

                LinearLayout buttonTrailer = findViewById(R.id.btnTrailer);
                buttonTrailer.setOnClickListener(v -> showVideoPopup(res.linkTrailer));
            }

            @Override
            public void onFailure(Call<MovieService.MovieDto> call, Throwable t) {
                Log.e("MovieDetailActivity", "Failed to get movie details: " + t.getMessage());
            }
        });
    }

    private void getShowtimes(Context context, int movieId) {
        List<Showtime> showtimes = new ArrayList<>();

        MovieService apiService = ApiClient.getRetrofitInstance().create(MovieService.class);

        Call<MovieService.GetShowtimesResponse> call = apiService.getShowtimes(movieId);
        call.enqueue(new Callback<MovieService.GetShowtimesResponse>() {
            @Override
            public void onResponse(Call<MovieService.GetShowtimesResponse> call, Response<MovieService.GetShowtimesResponse> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().result == null) return;

                MovieService.GetShowtimesResponse res = response.body();

                for (MovieService.ShowtimeDto showtime : res.result.data) {
                    showtimes.add(new Showtime(showtime.getShowtimeId(), showtime.getMovieId(), showtime.getHallId(), showtime.getSeatPrice(), showtime.getShowDate(), showtime.hall.hallName));
                }

                List<DateGroup> dateGroups = DateGroup.groupShowtimesByDate(showtimes);
                DateAdapter dateAdapter = new DateAdapter(context, dateGroups);
                recyclerView.setAdapter(dateAdapter);
            }

            @Override
            public void onFailure(Call<MovieService.GetShowtimesResponse> call, Throwable t) {
                Log.e("MovieDetailActivity", "Failed to get showtimes: " + t.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
        }
    }
}

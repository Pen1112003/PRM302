package com.example.prm392_cinema;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

        findViewById(R.id.btnSignOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSignOut();
            }
        });

        findViewById(R.id.backIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBack();
            }
        });
        (findViewById(R.id.btnHistory)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateHistory();
            }
        });
    }

    private void navigateHistory()
    {
        Intent intent = new Intent(MovieDetailActivity.this, HistoryOrder.class);
        startActivity(intent);
    }

    private void handleSignOut() {
        AuthStore.userId = 0;
        Intent intent = new Intent(MovieDetailActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void handleBack() {
        finish(); // Đóng Activity hiện tại và quay lại Activity trước đó
    }

    private void showVideoPopup(String videoUrl) {
        // Tạo dialog hiển thị video
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_video);

        // Lấy PlayerView từ layout
        PlayerControlView playerView = dialog.findViewById(R.id.playerView);

        // Khởi tạo ExoPlayer
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        // Tạo MediaItem từ URL và phát video
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));
        player.setMediaItem(mediaItem);
        player.prepare();
        player.setPlayWhenReady(true);

        // Hiển thị dialog
        dialog.setOnDismissListener(dialogInterface -> player.release()); // Giải phóng tài nguyên khi dialog đóng
        dialog.show();
    }

    private void getMovieDetail(int movieId) {
        MovieService apiService = ApiClient.getRetrofitInstance().create(MovieService.class);

        // Call API with a query parameter
        Call<MovieService.MovieDto> call = apiService.getMovieDetail(movieId);  // Pass `id` as 1
        call.enqueue(new Callback<MovieService.MovieDto>() {
            @Override
            public void onResponse(Call<MovieService.MovieDto> call, Response<MovieService.MovieDto> response) {
                if (!response.isSuccessful()) return;

                MovieService.MovieDto res = response.body();

                if (res == null) return;

                ((TextView) findViewById(R.id.title)).setText(res.title);
                ((TextView) findViewById(R.id.description)).setText(res.description);
                ((TextView) findViewById(R.id.release)).setText("Phát hành: " + res.releaseDate);
                ((TextView) findViewById(R.id.duration)).setText("Thời lượng: " + res.duration + " phút");
                ((TextView) findViewById(R.id.rating)).setText(res.rating + "");
                ((TextView) findViewById(R.id.genre)).setText("Thể loại: " + res.genre);
                Picasso.get().load(res.posterUrl).into((ImageView) findViewById(R.id.movieImg));

//                LinearLayout buttonTrailer = findViewById(R.id.btnTrailer);
//                buttonTrailer.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        showVideoPopup(res.linkTrailer);
//                    }
//                });
            }

            @Override
            public void onFailure(Call<MovieService.MovieDto> call, Throwable t) {
                // Handle the error
                Log.d("callAPI", t.getMessage());
            }
        });
    }

    private void getShowtimes(Context context, int movieId) {
        List<Showtime> showtimes = new ArrayList<>();

        MovieService apiService = ApiClient.getRetrofitInstance().create(MovieService.class);

        // Call API with a query parameter
        Call<MovieService.GetShowtimesResponse> call = apiService.getShowtimes(movieId);  // Pass `id` as 1
        call.enqueue(new Callback<MovieService.GetShowtimesResponse>() {
            @Override
            public void onResponse(Call<MovieService.GetShowtimesResponse> call, Response<MovieService.GetShowtimesResponse> response) {
                if (!response.isSuccessful()) return;
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
                // Handle the error
                Log.d("callAPI", t.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release(); // Giải phóng tài nguyên khi Activity bị hủy
        }
    }
}
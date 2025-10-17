package com.example.prm392_cinema;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.prm392_cinema.Adapters.BannerAdapter;


import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prm392_cinema.Adapters.MovieAdapter;
import com.example.prm392_cinema.Models.Movie;
import com.example.prm392_cinema.Services.ApiClient;
import com.example.prm392_cinema.Services.MovieService;
import com.example.prm392_cinema.Stores.AuthStore;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private Spinner genreSpinner, languageSpinner;

    private ViewPager2 viewPager;
    private RecyclerView recyclerView;
    ArrayList<Movie> movies;
    List<Movie> bannerMovies;
    MovieAdapter movieAdapter;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;
    BannerAdapter bannerAdapter;
    Button filterButton;
    TextView noMovie;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        noMovie = findViewById(R.id.noMoviesTextView);

        bannerMovies = new ArrayList<>();
        viewPager = findViewById(R.id.viewPager);
        bannerAdapter = new BannerAdapter(this, bannerMovies);
        viewPager.setAdapter(bannerAdapter);

        genreSpinner = findViewById(R.id.genreSpinner);
        languageSpinner = findViewById(R.id.languageSpinner);
        filterButton = findViewById(R.id.filterButton);
        recyclerView = findViewById(R.id.recyclerView);

        setupGenreSpinner();
        setupLanguageSpinner();

        movies = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        movieAdapter = new MovieAdapter(this, movies, movie -> {
            // Chuyển sang MovieDetailActivity khi nhấn vào item
            Intent intent = new Intent(HomeActivity.this, MovieDetailActivity.class);
            intent.putExtra("movieId", movie.getMovieId());
            startActivity(intent);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(movieAdapter);

        getTopMovies();
        getMovies("", "");

        runnable = new Runnable() {
            @Override
            public void run() {
                int nextItem = (viewPager.getCurrentItem() + 1) % bannerMovies.size();
                viewPager.setCurrentItem(nextItem);
                handler.postDelayed(this, 3000);
            }
        };
        handler.postDelayed(runnable, 3000);

        filterButton = findViewById(R.id.filterButton);
        filterButton.setOnClickListener(v -> {
            String selectedGenre = genreSpinner.getSelectedItem().toString();
            String selectedLanguage = languageSpinner.getSelectedItem().toString();
            getMovies(selectedGenre, selectedLanguage);
        });


        (findViewById(R.id.btnSignOut)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSignOut();
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
        Intent intent = new Intent(HomeActivity.this, HistoryOrder.class);
        startActivity(intent);
    }

    private void handleSignOut() {
        AuthStore.userId = 0;
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable); // Xóa callback khi activity bị hủy
    }

    private void setupGenreSpinner() {
        // Các giá trị mẫu cho genre
        String[] genres = {"Tất cả", "Hành động",
                "Phiêu lưu",
                "Kịch",
                "Giả tưởng",
                "Kinh dị",
                "Bí ẩn",
                "Lãng mạn",
                "Khoa học viễn tưởng",
                "Giật gân",
                "Hoạt hình"};
        ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genres);
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genreSpinner.setAdapter(genreAdapter);
    }

    private void setupLanguageSpinner() {
        // Các giá trị mẫu cho language
        String[] languages = {"Tất cả", "Tiếng Anh", "Tiếng Tây Ban Nha", "Tiếng Pháp", "Tiếng Đức", "Tiếng Nhật", "Tiếng Hàn", "Tiếng Thái", "Tiếng Việt"};
        ArrayAdapter<String> languageAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, languages);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(languageAdapter);
    }

    private void getMovies(String genre, String language) {

        MovieService apiService = ApiClient.getRetrofitInstance().create(MovieService.class);

        Call<List<Movie>> call = apiService.getMovies(genre.equals("Tất cả") ? null : genre,
                language.equals("Tất cả") ? null : language);
        call.enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                Log.d("callAPI", "Done");
                if (response.isSuccessful() && response.body() != null) {
                    noMovie.setVisibility(View.GONE);

                    if (response.body().size() == 0) {
                        noMovie.setVisibility(View.VISIBLE);
                    }
                    movies.clear();
                    movies.addAll(response.body());
                    movieAdapter.notifyDataSetChanged();
                } else {
                    noMovie.setVisibility(View.VISIBLE);
                    Toast.makeText(HomeActivity.this, "Không tìm thấy phim phù hợp", Toast.LENGTH_SHORT).show();
                }
                Log.d("callAPI", "Done");

            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                // Handle the error
                Log.d("callAPI", t.getMessage());
            }
        });


    }

    private void getTopMovies() {

        MovieService apiService = ApiClient.getRetrofitInstance().create(MovieService.class);

        Call<List<Movie>> call = apiService.getMovies("", "");
        call.enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                Log.d("callAPI", "Done");
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movies = response.body();
                    if (movies.size() == 0) {
                        return;
                    }
                    Collections.sort(movies, (m1, m2) -> Float.compare(m2.getRating(), m1.getRating()));

                    List<Movie> topMovies = movies.subList(0, Math.min(3, movies.size()));
                    bannerMovies.clear();
                    bannerMovies.addAll(topMovies);
                    bannerAdapter.notifyDataSetChanged();

                    TabLayout tabLayout = findViewById(R.id.tabDots);
                    new TabLayoutMediator(tabLayout, viewPager,
                            (tab, position) -> {
//                    tab.setCustomView(R.layout.tab_custom_view);
                            }).attach();
                }
                Log.d("callAPI", "Done");

            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                // Handle the error
                Log.d("callAPI", t.getMessage());
            }
        });


    }
}

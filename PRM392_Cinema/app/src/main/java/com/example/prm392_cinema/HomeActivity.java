package com.example.prm392_cinema;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.prm392_cinema.Adapters.BannerAdapter;
import com.example.prm392_cinema.Adapters.MovieAdapter;
import com.example.prm392_cinema.Models.Genre;
import com.example.prm392_cinema.Models.Movie;
import com.example.prm392_cinema.Services.ApiClient;
import com.example.prm392_cinema.Services.MovieService;
import com.example.prm392_cinema.Stores.AuthStore;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private Spinner genreSpinner;
    private ViewPager2 viewPager;
    private RecyclerView recyclerView;
    private ArrayList<Movie> movies;
    private List<Movie> bannerMovies;
    private MovieAdapter movieAdapter;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;
    private BannerAdapter bannerAdapter;
    private Button filterButton;
    private TextView noMovie;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize Views
        noMovie = findViewById(R.id.noMoviesTextView);
        viewPager = findViewById(R.id.viewPager);
        genreSpinner = findViewById(R.id.genreSpinner);
        filterButton = findViewById(R.id.filterButton);
        recyclerView = findViewById(R.id.recyclerView);

        // Initialize lists and adapters
        bannerMovies = new ArrayList<>();
        bannerAdapter = new BannerAdapter(this, bannerMovies);
        viewPager.setAdapter(bannerAdapter);

        movies = new ArrayList<>();
        movieAdapter = new MovieAdapter(this, movies, movie -> {
            Intent intent = new Intent(HomeActivity.this, MovieDetailActivity.class);
            intent.putExtra("movieId", movie.getMovieId());
            startActivity(intent);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(movieAdapter);

        // Setup UI components
        fetchAndSetupGenres();

        // Fetch initial data from API
        getTopMovies();
        fetchAllMovies();

        // Setup button listeners
        filterButton.setOnClickListener(v -> handleFilterClick());
        (findViewById(R.id.btnSignOut)).setOnClickListener(v -> handleSignOut());
        (findViewById(R.id.btnHistory)).setOnClickListener(v -> navigateHistory());
    }

    private void handleFilterClick() {
        Integer genreId = null;
        Object selectedItem = genreSpinner.getSelectedItem();

        if (selectedItem instanceof Genre) {
            Genre selectedGenre = (Genre) selectedItem;
            if (selectedGenre.getGenreId() != 0) { // 0 is our placeholder for "Tất cả"
                genreId = selectedGenre.getGenreId();
            }
        } else {
            Log.w("HomeActivity", "Cannot filter by genre, spinner data is not ready or failed to load.");
            Toast.makeText(this, "Vui lòng chờ danh sách thể loại tải xong.", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Call API based on selection
        if (genreId == null) {
            fetchAllMovies();
        } else {
            // Pass "Now Showing" for status and null for other parameters not on the UI
            fetchFilteredMovies("Now Showing", genreId, null, null);
        }
    }

    private void fetchAndSetupGenres() {
        Toast.makeText(HomeActivity.this, "Fetching genres...", Toast.LENGTH_SHORT).show();
        MovieService apiService = ApiClient.getRetrofitInstance().create(MovieService.class);
        Call<List<Genre>> call = apiService.getAllGenres();

        call.enqueue(new Callback<List<Genre>>() {
            @Override
            public void onResponse(Call<List<Genre>> call, Response<List<Genre>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Genre> genres = new ArrayList<>(response.body());
                    Log.d("HomeActivity", "Genres fetched successfully. Count: " + genres.size());
                    Toast.makeText(HomeActivity.this, "Genres fetched: " + genres.size() + " items", Toast.LENGTH_SHORT).show();

                    for (Genre genre : genres) {
                        Log.d("HomeActivity", "Genre: " + genre.getName() + " (ID: " + genre.getGenreId() + ")");
                    }

                    Genre allGenres = new Genre();
                    allGenres.setGenreId(0);
                    allGenres.setName("Tất cả thể loại");
                    genres.add(0, allGenres);

                    ArrayAdapter<Genre> genreAdapter = new ArrayAdapter<>(HomeActivity.this, R.layout.spinner_item_white_text, genres);
                    genreAdapter.setDropDownViewResource(R.layout.spinner_item_white_text);
                    genreSpinner.setAdapter(genreAdapter);
                } else {
                    Log.e("HomeActivity", "Failed to fetch genres. Response code: " + response.code() + ", Message: " + response.message());
                    Toast.makeText(HomeActivity.this, "Failed to fetch genres: Code " + response.code(), Toast.LENGTH_LONG).show();
                    setupDefaultGenreSpinner();
                }
            }

            @Override
            public void onFailure(Call<List<Genre>> call, Throwable t) {
                Log.e("HomeActivity", "Failed to fetch genres due to network or API error: " + t.getMessage(), t);
                Toast.makeText(HomeActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                setupDefaultGenreSpinner();
            }
        });
    }

    private void setupDefaultGenreSpinner() {
        String[] genres = {"Tất cả thể loại"};
        ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(this, R.layout.spinner_item_white_text, genres);
        genreAdapter.setDropDownViewResource(R.layout.spinner_item_white_text);
        genreSpinner.setAdapter(genreAdapter);
    }

    private void startBannerAutoScroll() {
        handler.removeCallbacks(runnable);
        runnable = () -> {
            if (bannerMovies != null && !bannerMovies.isEmpty()) {
                int nextItem = (viewPager.getCurrentItem() + 1) % bannerMovies.size();
                viewPager.setCurrentItem(nextItem, true);
                handler.postDelayed(runnable, 3000);
            }
        };
        handler.postDelayed(runnable, 3000);
    }

    private void navigateHistory() {
        Intent intent = new Intent(HomeActivity.this, HistoryOrder.class);
        startActivity(intent);
    }

    private void handleSignOut() {
        AuthStore.clear();
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    private void updateMovieList(List<Movie> newMovies) {
        movies.clear();
        if (newMovies != null && !newMovies.isEmpty()) {
            movies.addAll(newMovies);
            noMovie.setVisibility(View.GONE);
        } else {
            noMovie.setVisibility(View.VISIBLE);
            Toast.makeText(HomeActivity.this, "Không tìm thấy phim phù hợp", Toast.LENGTH_SHORT).show();
        }
        movieAdapter.notifyDataSetChanged();
    }

    private void fetchAllMovies() {
        MovieService apiService = ApiClient.getRetrofitInstance().create(MovieService.class);
        Call<List<Movie>> call = apiService.getAllMovies();
        call.enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                updateMovieList(response.body());
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                Log.e("HomeActivity", "Failed to fetch all movies: " + t.getMessage());
                updateMovieList(null);
            }
        });
    }

    private void fetchFilteredMovies(String status, Integer genreId, Integer theaterId, String date) {
        MovieService apiService = ApiClient.getRetrofitInstance().create(MovieService.class);
        Call<List<Movie>> call = apiService.getFilteredMovies(status, genreId, theaterId, date);
        call.enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                updateMovieList(response.body());
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                Log.e("HomeActivity", "Failed to fetch filtered movies: " + t.getMessage());
                updateMovieList(null);
            }
        });
    }

    private void getTopMovies() {
        MovieService apiService = ApiClient.getRetrofitInstance().create(MovieService.class);
        Call<List<Movie>> call = apiService.getAllMovies();
        call.enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<Movie> allMovies = new ArrayList<>(response.body());
                    Collections.sort(allMovies, (m1, m2) -> Float.compare(m2.getRating(), m1.getRating()));

                    List<Movie> topMovies = allMovies.subList(0, Math.min(3, allMovies.size()));
                    bannerMovies.clear();
                    bannerMovies.addAll(topMovies);
                    bannerAdapter.notifyDataSetChanged();

                    TabLayout tabLayout = findViewById(R.id.tabDots);
                    new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {}).attach();

                    if (!bannerMovies.isEmpty()) {
                        startBannerAutoScroll();
                    }
                } else {
                    Log.w("HomeActivity", "Failed to get top movies or movie list is empty.");
                }
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                Log.e("HomeActivity", "Failed to get top movies: " + t.getMessage());
            }
        });
    }
}

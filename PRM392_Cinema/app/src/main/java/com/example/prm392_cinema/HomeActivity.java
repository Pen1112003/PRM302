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

        // Setup Adapters
        bannerAdapter = new BannerAdapter(this, new ArrayList<>());
        viewPager.setAdapter(bannerAdapter);

        movieAdapter = new MovieAdapter(this, new ArrayList<>(), movie -> {
            Intent intent = new Intent(HomeActivity.this, MovieDetailActivity.class);
            intent.putExtra("movieId", movie.getMovieId());
            startActivity(intent);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(movieAdapter);

        // Setup UI components and fetch data
        fetchAndSetupGenres();
        getTopMovies();      // Restored call
        fetchAllMovies();    // Restored call

        // Setup button listeners
        filterButton.setOnClickListener(v -> handleFilterClick());
        (findViewById(R.id.btnSignOut)).setOnClickListener(v -> handleSignOut());
        (findViewById(R.id.btnHistory)).setOnClickListener(v -> navigateHistory());
    }

    // Restored original filter logic
    private void handleFilterClick() {
        Integer genreId = null;
        Object selectedItem = genreSpinner.getSelectedItem();

        if (selectedItem instanceof Genre) {
            Genre selectedGenre = (Genre) selectedItem;
            if (selectedGenre.getGenreId() != 0) { // 0 is our placeholder for "Tất cả"
                genreId = selectedGenre.getGenreId();
            }
        } else {
            Toast.makeText(this, "Vui lòng chờ danh sách thể loại tải xong.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (genreId == null) {
            fetchAllMovies(); // Fetch all movies again
        } else {
            fetchFilteredMovies(genreId);
        }
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

    // Restored method for server-side filtering
    private void fetchFilteredMovies(Integer genreId) {
        MovieService apiService = ApiClient.getRetrofitInstance().create(MovieService.class);
        Call<List<Movie>> call = apiService.getFilteredMovies("Now Showing", genreId, null, null);
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

    private void updateMovieList(List<Movie> movies) {
        movieAdapter.updateMovies(movies);
        noMovie.setVisibility(movies == null || movies.isEmpty() ? View.VISIBLE : View.GONE);
        if (movies == null || movies.isEmpty()) {
            Toast.makeText(HomeActivity.this, "Không tìm thấy phim phù hợp", Toast.LENGTH_SHORT).show();
        }
    }

    // Restored original getTopMovies method
    private void getTopMovies() {
        MovieService apiService = ApiClient.getRetrofitInstance().create(MovieService.class);
        Call<List<Movie>> call = apiService.getAllMovies(); // This might need a specific endpoint like /TopMovies in the future
        call.enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<Movie> allMovies = new ArrayList<>(response.body());
                    // Sort by rating or any other criteria if needed
                    // Collections.sort(allMovies, (m1, m2) -> Float.compare(m2.getRating(), m1.getRating()));

                    List<Movie> topMovies = allMovies.subList(0, Math.min(3, allMovies.size()));
                    bannerAdapter.updateMovies(topMovies);

                    TabLayout tabLayout = findViewById(R.id.tabDots);
                    new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {}).attach();

                    if (topMovies.size() > 1) {
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

    // --- Other methods (genres, navigation, lifecycle) ---

    private void fetchAndSetupGenres() {
        MovieService apiService = ApiClient.getRetrofitInstance().create(MovieService.class);
        Call<List<Genre>> call = apiService.getAllGenres();
        call.enqueue(new Callback<List<Genre>>() {
            @Override
            public void onResponse(Call<List<Genre>> call, Response<List<Genre>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Genre> genres = new ArrayList<>(response.body());
                    Genre allGenres = new Genre();
                    allGenres.setGenreId(0);
                    allGenres.setName("Tất cả thể loại");
                    genres.add(0, allGenres);

                    ArrayAdapter<Genre> genreAdapter = new ArrayAdapter<>(HomeActivity.this, R.layout.spinner_item_white_text, genres);
                    genreAdapter.setDropDownViewResource(R.layout.spinner_item_white_text);
                    genreSpinner.setAdapter(genreAdapter);
                } else {
                    setupDefaultGenreSpinner();
                }
            }

            @Override
            public void onFailure(Call<List<Genre>> call, Throwable t) {
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
            int nextItem = (viewPager.getCurrentItem() + 1) % bannerAdapter.getItemCount();
            if (bannerAdapter.getItemCount() > 0) {
                 viewPager.setCurrentItem(nextItem, true);
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
        handler.removeCallbacks(runnable);
    }
}

package com.example.prm392_cinema.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prm392_cinema.Models.Movie;
import com.example.prm392_cinema.MovieDetailActivity;
import com.example.prm392_cinema.R;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private Context context;
    private List<Movie> movieList;

    public BannerAdapter(Context context, List<Movie> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.banner_item, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.bannerTitleTextView.setText(movie.getTitle());

        String posterUrl = movie.getPoster();
        Log.d("BannerAdapter", "Loading banner with Glide for " + movie.getTitle() + ": " + posterUrl);

        if (posterUrl != null && !posterUrl.isEmpty()) {
            Glide.with(context)
                    .load(posterUrl)
                    .placeholder(R.drawable.conan_movie)
                    .error(R.drawable.conan_movie)
                    .into(holder.bannerImageView);
        } else {
            Glide.with(context).clear(holder.bannerImageView);
            holder.bannerImageView.setImageResource(R.drawable.conan_movie);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MovieDetailActivity.class);
            intent.putExtra("movieId", movie.getMovieId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public void updateMovies(List<Movie> newMovies) {
        this.movieList.clear();
        if (newMovies != null) {
            this.movieList.addAll(newMovies);
        }
        notifyDataSetChanged();
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView bannerImageView;
        TextView bannerTitleTextView;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerImageView = itemView.findViewById(R.id.bannerImageView);
            bannerTitleTextView = itemView.findViewById(R.id.bannerTitleTextView);
        }
    }
}

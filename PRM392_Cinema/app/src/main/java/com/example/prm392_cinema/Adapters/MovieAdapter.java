package com.example.prm392_cinema.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_cinema.Models.Movie;
import com.example.prm392_cinema.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private Context context;
    private List<Movie> movieList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Movie movie);
    }

    public MovieAdapter(Context context, List<Movie> movieList, OnItemClickListener listener) {
        this.context = context;
        this.movieList = movieList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.titleTextView.setText(movie.getTitle());
        holder.durationTextView.setText("Thời lượng: " + movie.getDuration() + " phút");

        Log.d("MovieAdapter", "Loading poster for " + movie.getTitle() + ": " + movie.getPoster());

        if (movie.getPoster() != null && !movie.getPoster().isEmpty()) {
            Picasso.get()
                    .load(movie.getPoster())
                    .placeholder(R.drawable.conan_movie)
                    .error(R.drawable.conan_movie)
                    .into(holder.posterImageView);
        } else {
            holder.posterImageView.setImageResource(R.drawable.conan_movie);
        }

        // Set the click listener to the item view
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(movie);
            }
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

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, durationTextView, genreTextView;
        ImageView posterImageView;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            posterImageView = itemView.findViewById(R.id.posterImageView);
            durationTextView = itemView.findViewById(R.id.durationTextView);
            genreTextView = itemView.findViewById(R.id.genreTextView);
        }
    }
}

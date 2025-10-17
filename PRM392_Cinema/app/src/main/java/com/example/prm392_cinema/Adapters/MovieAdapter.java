package com.example.prm392_cinema.Adapters;
import android.content.Context;
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

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private Context context;
    private ArrayList<Movie> movieList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Movie movie);
    }

    public MovieAdapter(Context context, ArrayList<Movie> movieList, OnItemClickListener listener) {
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
        holder.ratingTextView.setText(String.valueOf(movie.getRating()));
        holder.genreTextView.setText(movie.getGenre());
        holder.posterImageView.setImageResource(R.drawable.conan_movie);
        Picasso.get().load(movie.getPosterUrl()).into(holder.posterImageView);

        // Sử dụng Glide để tải ảnh poster từ URL
//        Glide.with(context)
//                .load(movie.getPoster())
//                .placeholder(R.drawable.placeholder) // hình ảnh tạm trong khi tải poster
//                .into(holder.posterImageView);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(movie));
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, ratingTextView, genreTextView;
        ImageView posterImageView;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            ratingTextView = itemView.findViewById(R.id.ratingTextView);
            genreTextView = itemView.findViewById(R.id.genreTextView);
            posterImageView = itemView.findViewById(R.id.posterImageView);
        }
    }
}

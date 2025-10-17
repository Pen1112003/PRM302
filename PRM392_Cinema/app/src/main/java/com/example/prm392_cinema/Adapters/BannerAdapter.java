package com.example.prm392_cinema.Adapters;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_cinema.MainActivity;
import com.example.prm392_cinema.Models.Movie;
import com.example.prm392_cinema.MovieDetailActivity;
import com.example.prm392_cinema.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
    private List<Movie> movies;
    private Context context;

    public BannerAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.banner_item, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        Movie movie = movies.get(position);

        // Hiển thị thông tin từ Movie
        holder.titleTextView.setText(movie.getTitle());
        holder.ratingTextView.setText(String.valueOf(movie.getRating()));
        holder.genreTextView.setText(movie.getGenre());
        holder.imageView.setImageResource(R.drawable.conan_movie);
        holder.durationTextView.setText(String.valueOf(movie.getDuration()) + "m");
        Picasso.get().load(movie.getPosterUrl()).into(holder.imageView);
        // Nếu poster là một URL, sử dụng Glide để tải ảnh
//        Glide.with(context)
//                .load(movie.getPoster()) // Đảm bảo URL hợp lệ
//                .placeholder(R.drawable.placeholder) // Ảnh tạm trong khi tải poster
//                .into(holder.imageView);

        // Xử lý khi nhấn vào nút "Xem chi tiết"
        holder.button.setOnClickListener(v -> {
            Intent intent = new Intent(context, MovieDetailActivity.class);
            intent.putExtra("movieId", movie.getMovieId()); // Truyền đối tượng Movie sang Activity mới
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView, ratingTextView, genreTextView, durationTextView;
        Button button;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.bannerImage);
            button = itemView.findViewById(R.id.detailButton);
            titleTextView = itemView.findViewById(R.id.movieTitle);
            ratingTextView = itemView.findViewById(R.id.movieRating);
            genreTextView = itemView.findViewById(R.id.movieGenre);
            durationTextView = itemView.findViewById(R.id.movieDuration);
        }
    }
}

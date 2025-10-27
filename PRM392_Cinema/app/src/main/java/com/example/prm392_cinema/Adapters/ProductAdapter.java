package com.example.prm392_cinema.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.prm392_cinema.Models.Product;
import com.example.prm392_cinema.R;

import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private Context context;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.productName.setText(product.getProductName());
        holder.productPrice.setText(String.format(Locale.US, "%,.0f VNĐ", product.getPrice()));
        holder.productQuantity.setText(String.valueOf(product.getSelectedQuantity()));

        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.mipmap.ic_launcher) // Ảnh hiển thị trong khi chờ tải
                .error(R.mipmap.ic_launcher) // Ảnh hiển thị khi có lỗi
                .into(holder.productImage);

        holder.btnIncrease.setOnClickListener(v -> {
            int quantity = product.getSelectedQuantity();
            quantity++;
            product.setSelectedQuantity(quantity);
            notifyItemChanged(position);
        });

        holder.btnDecrease.setOnClickListener(v -> {
            int quantity = product.getSelectedQuantity();
            if (quantity > 0) {
                quantity--;
                product.setSelectedQuantity(quantity);
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public List<Product> getProductList() {
        return productList;
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice, productQuantity;
        Button btnIncrease, btnDecrease;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
        }
    }
}

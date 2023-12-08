package com.example.LostFound.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.LostFound.MessageActivity;
import com.example.LostFound.Models.LostProduct;
import com.example.LostFound.R;

import java.util.List;


public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductViewHolder> {

    private List<LostProduct> productList;
    private Context context;

    public ProductListAdapter(Context context, List<LostProduct> productList) {
        this.context = context;
        this.productList = productList;
    }

    public void setProductList(List<LostProduct> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        LostProduct lostProduct = productList.get(position);
        holder.bind(lostProduct);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView productNameTextView;
        private LostProduct lostProduct;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);

            // Set click listener on the itemView
            itemView.setOnClickListener(this);
        }

        public void bind(LostProduct lostProduct) {
            this.lostProduct = lostProduct;

            // Populate the TextView with product details (replace with your actual field names)
            productNameTextView.setText(lostProduct.getProductName());
        }

        @Override
        public void onClick(View view) {
            // Handle the click event, for example, start MessageActivity with the imageKey
            Intent intent = new Intent(context, MessageActivity.class);
            intent.putExtra("productId", lostProduct.getImageKey());
            context.startActivity(intent);
        }
    }
}

package com.example.LostFound.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.LostFound.Models.LostProduct;
import com.example.LostFound.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private List<LostProduct> productList;
    private static Context context;

    public ProductAdapter(Context context, List<LostProduct> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_product, parent, false);
        return new ViewHolder(view, this); // Pass the adapter instance to the ViewHolder
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LostProduct product = productList.get(position);

        // Bind the product details to the views
        holder.productNameTextView.setText(product.getProductName());
        holder.productDescriptionTextView.setText(product.getDescription());
        holder.productColorTextView.setText(String.valueOf(product.getColor()));

        // Load product image using Glide or any other image loading library
        Glide.with(context)
                .load(product.getImageUrl())
                .into(holder.productImageView);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView productImageView;
        TextView productNameTextView;
        TextView productDescriptionTextView;
        TextView productColorTextView;
        ImageView productMenuIcon;
        ProductAdapter productAdapter; // Declare the adapter instance

        public ViewHolder(@NonNull View itemView, ProductAdapter productAdapter) {
            super(itemView);
            this.productAdapter = productAdapter; // Initialize the adapter instance
            productImageView = itemView.findViewById(R.id.productImageView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productDescriptionTextView = itemView.findViewById(R.id.productDescriptionTextView);
            productColorTextView = itemView.findViewById(R.id.productColorTextView);

            productMenuIcon = itemView.findViewById(R.id.productMenuIcon);
            productMenuIcon.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.productMenuIcon) {
                showPopupMenu(v);
            }
        }

        private void showPopupMenu(View view) {
            PopupMenu popupMenu = new PopupMenu(context, view, Gravity.END);
            popupMenu.getMenuInflater().inflate(R.menu.product_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    switch (item.getItemId()) {
                        case R.id.menuCollected:
                            productAdapter.updateStatus(position, "collected", context);
                            break;
                        case R.id.menuPotentialOwnerFound:
                            productAdapter.updateStatus(position, "potentialOwnerFound", context);
                            break;
                        // Handle other menu items if needed
                    }
                }
                return true;
            });

            popupMenu.show();
        }
    }
    public void updateStatus(int position, String newStatus, Context context) {
        LostProduct product = productList.get(position);
        // Check if the new status is "collected"
        if ("collected".equals(newStatus)) {
            // Remove the product locally
            productList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());

            // Remove the product from the Firebase Realtime Database
            DatabaseReference lostProductRef = FirebaseDatabase.getInstance().getReference()
                    .child("LostProduct")
                    .child(product.getImageKey());

            lostProductRef.removeValue()
                    .addOnSuccessListener(aVoid -> {
                        // Product deleted successfully from the database
                        Toast.makeText(context, "Product marked as Collected.", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Handle the failure to delete the product from the database
                        Toast.makeText(context, "Failed to mark product as collected", Toast.LENGTH_SHORT).show();
                        // If deletion fails, you may want to add the product back to the local list
                        productList.add(position, product);
                        notifyItemInserted(position);
                    });
        } else {
            // If the status is not "collected," update the status in the database as before
            product.setStatus(newStatus);
            notifyItemChanged(position);

            DatabaseReference lostProductRef = FirebaseDatabase.getInstance().getReference()
                    .child("LostProduct")
                    .child(product.getImageKey());

            lostProductRef.child("status").setValue(newStatus)
                    .addOnSuccessListener(aVoid -> {
                        // Status updated successfully in the database
                        Toast.makeText(context, "Product status marked as potentialOwnerFound", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Handle the failure to update the status in the database
                        Toast.makeText(context, "Failed to mark product status as potentialOwnerFound ", Toast.LENGTH_SHORT).show();
                    });
        }
    }

}

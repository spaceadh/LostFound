package com.example.LostFound.AdminFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.LostFound.Models.LostProduct;
import com.example.LostFound.R;
import com.example.LostFound.adapters.ProductAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

// ...

public class AdminHomeFragment extends Fragment {
    private List<LostProduct> productList;

    public AdminHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        productList = new ArrayList<>();
        fetchProductList(); // Fetch products from Firebase
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        // Initialize the RecyclerView and set its adapter
        RecyclerView recyclerViewProducts = view.findViewById(R.id.recyclerViewProducts);
        ProductAdapter productAdapter = new ProductAdapter(getContext(), productList);
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewProducts.setAdapter(productAdapter);

        return view;
    }

    private void fetchProductList() {
        DatabaseReference lostProductRef = FirebaseDatabase.getInstance().getReference().child("LostProduct");

        lostProductRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear(); // Clear the existing list
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    LostProduct lostProduct = snapshot.getValue(LostProduct.class);
                    if (lostProduct != null) {
                        productList.add(lostProduct);
                    }
                }
                // Notify the adapter that the data set has changed
                if (getView() != null) {
                    RecyclerView recyclerViewProducts = getView().findViewById(R.id.recyclerViewProducts);
                    recyclerViewProducts.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}

package com.example.LostFound.ClientsFragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.LostFound.MessageActivity;
import com.example.LostFound.Models.LostProduct;
import com.example.LostFound.Models.User;
import com.example.LostFound.R;
import com.example.LostFound.adapters.ProductListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {

    private EditText productSearchEditText;
    private RecyclerView recyclerViewProductList;
    private RelativeLayout layoutPlaceholder;
    private TextView textViewGreeting,textViewUsername;
    private String currentUsername;
    private List<LostProduct> productList; // Replace with your actual product model
    //private List<LostProduct> lostProductList;
    private ProductListAdapter productListAdapter;
    // Firebase
    private DatabaseReference lostProductsRef;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        productSearchEditText = view.findViewById(R.id.productSearch);
        recyclerViewProductList = view.findViewById(R.id.recyclerViewProductList);
        layoutPlaceholder = view.findViewById(R.id.layout_placeholder);

        textViewGreeting = view.findViewById(R.id.textView_greeting);
        textViewUsername = view.findViewById(R.id.textView_username);

        // Retrieve the current username
        getCurrentUsername();

        productList = new ArrayList<>(); // Initialize your product list

        // Set up RecyclerView
        recyclerViewProductList.setLayoutManager(new LinearLayoutManager(getActivity()));
        productListAdapter = new ProductListAdapter(getActivity(), productList);
        //productListAdapter = new ProductListAdapter(productList, (ProductListAdapter.OnProductClickListener) this); // Pass 'this' as the click listener
        recyclerViewProductList.setAdapter(productListAdapter);

        // Initialize Firebase
        lostProductsRef = FirebaseDatabase.getInstance().getReference().child("LostProduct");

        // Set up the TextWatcher
        productSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Not used in this example
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Not used in this example
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Call the search function with the updated search term
                String searchTerm = editable.toString();
                searchForProduct(searchTerm);
            }
        });

        return view;
    }
    private void getCurrentUsername() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference().child("Users").child("Clients").child(currentUserId);
            usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            currentUsername = user.getUsername();
                            textViewUsername.setText(currentUsername);
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle the error
                }
            });
        }
    }
    private void searchForProduct(final String searchTerm) {
        // Create a query to search for the product
        Query searchQuery = lostProductsRef.orderByChild("productName").equalTo(searchTerm);

        searchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Initialize a list to store filtered products
                List<LostProduct> filteredList = new ArrayList<>();

                // Check if the product is found
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        LostProduct product = snapshot.getValue(LostProduct.class);
                        if (product != null) {
                            filteredList.add(product);
                        }
                    }
                    // Update RecyclerView with the filtered list
                    productListAdapter.setProductList(filteredList);


                } else {
                    // Product not found, display a message or handle accordingly
                    Toast.makeText(getActivity(), "Product not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that may occur
                Toast.makeText(getActivity(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

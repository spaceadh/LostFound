//package com.example.LostFound.ClientsFragments;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.LostFound.MessageActivity;
//import com.example.LostFound.Models.LostProduct;
//import com.example.LostFound.Models.User;
//import com.example.LostFound.R;
//import com.example.LostFound.adapters.ProductListAdapter;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;
//
//public class HomeFragment extends Fragment {
//
//    private EditText productSearchEditText;
//    private RecyclerView recyclerViewProductList;
//    private RelativeLayout layoutPlaceholder;
//    private TextView textViewGreeting,textViewUsername;
//    private String currentUsername;
//    private List<LostProduct> productList; // Replace with your actual product model
//    //private List<LostProduct> lostProductList;
//
//    private ProductListAdapter productListAdapter;
//
//    public HomeFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_home, container, false);
//
//        productSearchEditText = view.findViewById(R.id.productSearch);
//        recyclerViewProductList = view.findViewById(R.id.recyclerViewProductList);
//        layoutPlaceholder = view.findViewById(R.id.layout_placeholder);
//
//        textViewGreeting = view.findViewById(R.id.textView_greeting);
//        textViewUsername = view.findViewById(R.id.textView_username);
//
//        // Get the current time
//        Calendar calendar = Calendar.getInstance();
//        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
//
//        // Set the greeting based on the current time
//        String greeting = getGreeting(hourOfDay);
//        textViewGreeting.setText(greeting);
//
//        // Retrieve the current username
//        getCurrentUsername();
//
//        productList = new ArrayList<>(); // Initialize your product list
//
//        // Set up RecyclerView
//        recyclerViewProductList.setLayoutManager(new LinearLayoutManager(getActivity()));
//        productListAdapter = new ProductListAdapter(getActivity(), productList);
//        //productListAdapter = new ProductListAdapter(productList, (ProductListAdapter.OnProductClickListener) this); // Pass 'this' as the click listener
//        recyclerViewProductList.setAdapter(productListAdapter);
//
//        // Set up TextWatcher for the search bar
//        productSearchEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                // Filter the list based on the entered text
//                filterProductList(charSequence.toString());
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//            }
//        });
//
//        // Initially, hide the placeholder and show RecyclerView
//        updateVisibility();
//
//        return view;
//    }
//    private String getGreeting(int hourOfDay) {
//        String greeting;
//
//        if (hourOfDay >= 6 && hourOfDay < 12) {
//            greeting = "Good Morning \uD83C\uDF1E";
//        } else if (hourOfDay >= 12 && hourOfDay < 18) {
//            greeting = "Good Afternoon \uD83C\uDF25️";
//        } else {
//            greeting = "Good Evening \uD83C\uDF1A";
//        }
//        return greeting;
//    }
//    private void getCurrentUsername() {
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser != null) {
//            String currentUserId = currentUser.getUid();
//            DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference().child("Users").child("Clients").child(currentUserId);
//            usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.exists()) {
//                        User user = dataSnapshot.getValue(User.class);
//                        if (user != null) {
//                            currentUsername = user.getUsername();
//                            textViewUsername.setText(currentUsername);
//                        }
//                    }
//                }
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                    // Handle the error
//                }
//            });
//        }
//    }
//    private void filterProductList(String searchText) {
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//
//        if (currentUser != null) {
//            String currentUserId = currentUser.getUid();
//            DatabaseReference productsReference = FirebaseDatabase.getInstance().getReference()
//                    .child("LostProduct");
//
//            // Perform a query to search for products with names containing the search text
//            productsReference.orderByChild("productName")
//                    .startAt(searchText)
//                    .endAt(searchText + "\uf8ff")
//                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            List<LostProduct> filteredList = new ArrayList<>();
//                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                                LostProduct product = snapshot.getValue(LostProduct.class);
//                                if (product != null) {
//                                    filteredList.add(product);
//                                }
//                            }
//
//                            // Update RecyclerView with the filtered list
//                            productListAdapter.setProductList(filteredList);
//
//                            // Update visibility based on the filtered list
//                            updateVisibility();
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//                            // Handle the error
//                        }
//                    });
//        }
//    }
//
//
//    private void updateVisibility() {
//        if (productListAdapter.getItemCount() > 0) {
//            // If there are items in the RecyclerView, hide the placeholder and show RecyclerView
//            layoutPlaceholder.setVisibility(View.GONE);
//            recyclerViewProductList.setVisibility(View.VISIBLE);
//        } else {
//            // If the RecyclerView is empty, show the placeholder and hide RecyclerView
//            layoutPlaceholder.setVisibility(View.VISIBLE);
//            recyclerViewProductList.setVisibility(View.GONE);
//        }
//    }
//}

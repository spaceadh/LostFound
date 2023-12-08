package com.example.LostFound.ClientsFragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.example.LostFound.DashboardActivity;
import com.example.LostFound.R;

public class InstructionsFragment extends Fragment {
    private ImageView cancel;

    public InstructionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_instructions_page, container, false);

        // Initialize the cancel ImageView and set its click listener
        cancel = view.findViewById(R.id.imageView);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the DashboardActivity when the cancel ImageView is clicked
                startActivity(new Intent(getActivity(), DashboardActivity.class));
            }
        });

        return view;
    }
}

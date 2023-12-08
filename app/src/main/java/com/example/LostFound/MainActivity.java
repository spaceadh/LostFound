package com.example.LostFound;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.LostFound.R;
import com.example.LostFound.MainActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private static final long DIALOG_DISPLAY_TIME = 6000; // Time in milliseconds
    private AlertDialog dialog;
    private Handler handler;
    private boolean networkAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth != null) {
            currentUser = mAuth.getCurrentUser();
        }
        handler = new Handler();

        // This is used to hide the status bar and make the splash screen a full-screen activity
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        ImageView logoImageView = findViewById(R.id.logoImageView);

        // Specify the corner radius you want for rounded corners
        int cornerRadius = 20;

        // Load the image and apply a transformation for rounded corners
        Glide.with(this)
                .load(R.drawable.output2)
                .transform(new RoundedCorners(cornerRadius))
                .into(logoImageView);


        // Perform the slideshow animation on the background image
        ImageView backgroundImage = findViewById(R.id.logoImageView);
        backgroundImage.startAnimation(AnimationUtils.loadAnimation(this, R.anim.side_slide));

        // Perform the slideshow animation on the background image
        TextView PlantCareLoader = findViewById(R.id.PlantCareLoader);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                PlantCareLoader.setText("L");
            }
        }, 400);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                PlantCareLoader.append("o");
            }
        }, 550);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                PlantCareLoader.append("s");
            }
        }, 700);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                PlantCareLoader.append("t");
            }
        }, 850);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                PlantCareLoader.append(" ");
            }
        }, 1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                PlantCareLoader.append("N` ");
            }
        }, 1150);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                PlantCareLoader.append("F");
            }
        }, 1300);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                PlantCareLoader.append("o");
            }
        }, 1450);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                PlantCareLoader.append("u");
            }
        }, 1600);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                PlantCareLoader.append("n");
            }
        }, 1750);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                PlantCareLoader.append("d");
            }
        }, 1900);
        navigateToNextActivity();
    }

    private void navigateToNextActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user == null) {
                    Intent intent = new Intent(MainActivity.this,GuestActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(MainActivity.this, GuestActivity.class);
                    startActivity(intent);
                    finish();

                }
            }
        }, 3000);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
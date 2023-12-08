package com.example.LostFound;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class GuestActivity extends AppCompatActivity {

    private TextView welcomeGuestTextView;
    private ImageView menuIcon;
    private ImageView fluffyImageView;
    private PopupWindow menuPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);

        welcomeGuestTextView = findViewById(R.id.welcome_guest_text);
        menuIcon = findViewById(R.id.menu_icon);
        fluffyImageView = findViewById(R.id.fluffy_image);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu();
            }
        });
    }

    private void showPopupMenu() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View menuView = inflater.inflate(R.layout.menu_layout, null);

        menuPopupWindow = new PopupWindow(menuView, getResources().getDisplayMetrics().widthPixels * 3 / 4,
                getResources().getDisplayMetrics().heightPixels, true);


        RelativeLayout myaccount = menuView.findViewById(R.id.menu_my_accountLayout);
        myaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menuPopupWindow != null && menuPopupWindow.isShowing()) {
                    menuPopupWindow.dismiss();
                }

                startActivity(new Intent(GuestActivity.this, LoginNormal.class));
            }
        });

        menuPopupWindow.showAtLocation(welcomeGuestTextView, Gravity.END, 0, 0);
    }

    @Override
    public void onBackPressed() {
        if (menuPopupWindow != null && menuPopupWindow.isShowing()) {
            menuPopupWindow.dismiss();
        } else {
            super.onBackPressed();
        }
    }

}
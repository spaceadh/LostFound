package com.example.LostFound.AdminFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.LostFound.AdminFragment.AdminHomeFragment;
import com.example.LostFound.R;
import com.google.android.material.tabs.TabLayout;

public class HomepageSwitcheroo extends Fragment {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    private AdminHomeFragment adminHomeFragment;
    private Admindms myChatsFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_fragment, container, false);

        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);

        adminHomeFragment = new AdminHomeFragment();
        myChatsFragment = new Admindms();

        ChatPagerAdapter pagerAdapter = new ChatPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        // Set icons for the tabs
        tabLayout.getTabAt(0).setIcon(R.drawable.products);
        tabLayout.getTabAt(1).setIcon(R.drawable.chats);

        return view;
    }

    private class ChatPagerAdapter extends FragmentPagerAdapter {

        private static final int NUM_PAGES = 2;

        public ChatPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return adminHomeFragment;
                case 1:
                    return myChatsFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
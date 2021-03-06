package com.example.musicapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.example.musicapp.fragments.AllSongFragment;
import com.example.musicapp.fragments.FindFragment;
import com.example.musicapp.fragments.HomeFragment;
import com.example.musicapp.fragments.PlaylistFragment;
import com.example.musicapp.fragments.UserFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.musicapp.R;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadFragment(new HomeFragment());

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    loadFragment(new HomeFragment());
                    return true;
                case R.id.navigation_all_music:
                    loadFragment(new AllSongFragment());
                    return true;
                case R.id.navigation_playlist:
                    loadFragment(new PlaylistFragment());
                    return true;
                case R.id.navigation_search:
                    loadFragment(new FindFragment());
                    return true;
                case R.id.navigation_user:
                    loadFragment(new UserFragment());
                    return true;
            }
            return false;
        });
    }
    private void loadFragment(Fragment homeFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.home_container, homeFragment);
        transaction.addToBackStack(homeFragment.getClass().getSimpleName());
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commit();
    }

}
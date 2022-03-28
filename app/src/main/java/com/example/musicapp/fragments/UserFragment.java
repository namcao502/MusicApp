package com.example.musicapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.musicapp.R;
import com.example.musicapp.activities.UploadActivity;

public class UserFragment extends Fragment {

    Button buttonUp;


    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        buttonUp = view.findViewById(R.id.buttonUp);

        buttonUp.setOnClickListener(view1 -> startActivity(new Intent(getContext(), UploadActivity.class)));

        return view;
    }
}
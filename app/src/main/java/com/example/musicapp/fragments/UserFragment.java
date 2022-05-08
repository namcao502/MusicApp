package com.example.musicapp.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.musicapp.R;
import com.example.musicapp.activities.crud.ResetPasswordActivity;
import com.example.musicapp.activities.crud.ArtistCRUDActivity;
import com.example.musicapp.activities.crud.CountryCRUDActivity;
import com.example.musicapp.activities.crud.GenreCRUDActivity;
import com.example.musicapp.activities.LoginActivity;
import com.example.musicapp.activities.crud.SongCRUDActivity;
import com.example.musicapp.activities.crud.SongUploadActivity;
import com.example.musicapp.activities.crud.UserCRUDActivity;
import com.example.musicapp.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserFragment extends Fragment {

    Button buttonContribute, buttonLogout, buttonSongCRUD, buttonArtistCRUD, buttonCountryCRUD, buttonGenreCRUD, buttonUserCRUD, buttonChangePassword;
    TextView textViewEmail;
    FirebaseAuth auth;
    FirebaseFirestore db;
    List<UserModel> userModelList;

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        ViewBinding(view);

        LoadUserName();

        Listener();

        return view;
    }


    private void Listener() {

        buttonChangePassword.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), ResetPasswordActivity.class));
        });

        buttonUserCRUD.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), UserCRUDActivity.class));
        });

        buttonGenreCRUD.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), GenreCRUDActivity.class));
        });

        buttonCountryCRUD.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), CountryCRUDActivity.class));
        });

        buttonArtistCRUD.setOnClickListener(view -> startActivity(new Intent(getContext(), ArtistCRUDActivity.class)));

        buttonContribute.setOnClickListener(view -> startActivity(new Intent(getContext(), SongUploadActivity.class)));

        buttonLogout.setOnClickListener(view -> {
            auth.signOut();
            startActivity(new Intent(getContext(), LoginActivity.class));
        });

        buttonSongCRUD.setOnClickListener(view -> startActivity(new Intent(getContext(), SongCRUDActivity.class)));
    }

    @SuppressLint("SetTextI18n")
    private void LoadUserName() {
        db.collection("User").whereEqualTo("id", auth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot doc : task.getResult()){
                    UserModel userModel = doc.toObject(UserModel.class);
                    textViewEmail.setText("Xin chào, " + userModel.getName());
                }
            }
        });
    }

    private void ViewBinding(View view) {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        textViewEmail = view.findViewById(R.id.textViewEmail);

        buttonSongCRUD = view.findViewById(R.id.buttonSongCRUD);
        buttonLogout = view.findViewById(R.id.buttonLogout);
        buttonContribute = view.findViewById(R.id.buttonContribute);
        buttonArtistCRUD = view.findViewById(R.id.buttonArtistCRUD);
        buttonCountryCRUD = view.findViewById(R.id.buttonCountryCRUD);
        buttonGenreCRUD = view.findViewById(R.id.buttonGenreCRUD);
        buttonUserCRUD = view.findViewById(R.id.buttonUserCRUD);
        buttonChangePassword = view.findViewById(R.id.buttonChangePassword);

        userModelList = new ArrayList<>();

        SetVisibility(false);

        db.collection("User").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot doc : task.getResult()){
                    UserModel userModel = doc.toObject(UserModel.class);
                    if (userModel.getId().equals(auth.getCurrentUser().getUid()) && userModel.getRole().equals("admin")){
                        SetVisibility(true);
                    }
                }
            }
        });
    }

    private void SetVisibility(boolean x){
        if (x){
            buttonUserCRUD.setVisibility(View.VISIBLE);
            buttonSongCRUD.setVisibility(View.VISIBLE);
            buttonArtistCRUD.setVisibility(View.VISIBLE);
            buttonCountryCRUD.setVisibility(View.VISIBLE);
            buttonGenreCRUD.setVisibility(View.VISIBLE);
        }
        else {
            buttonUserCRUD.setVisibility(View.GONE);
            buttonSongCRUD.setVisibility(View.GONE);
            buttonArtistCRUD.setVisibility(View.GONE);
            buttonCountryCRUD.setVisibility(View.GONE);
            buttonGenreCRUD.setVisibility(View.GONE);
        }
    }

}
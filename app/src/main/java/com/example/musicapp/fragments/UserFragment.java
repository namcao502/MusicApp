package com.example.musicapp.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.musicapp.R;
import com.example.musicapp.activities.ArtistCRUDActivity;
import com.example.musicapp.activities.CountryCRUDActivity;
import com.example.musicapp.activities.GenreCRUDActivity;
import com.example.musicapp.activities.LoginActivity;
import com.example.musicapp.activities.SongCRUDActivity;
import com.example.musicapp.activities.SongUploadActivity;
import com.example.musicapp.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserFragment extends Fragment {

    Button buttonContribute, buttonLogout, buttonSongCRUD, buttonArtistCRUD, buttonCountryCRUD, buttonGenreCRUD;
    TextView textViewEmail;
    CircleImageView circleImageView;
    FirebaseAuth auth;
    FirebaseFirestore db;

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        ViewBinding(view);

        LoadUserNameAndImage();

        Listener();

        return view;
    }


    private void Listener() {

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

    private void LoadUserNameAndImage() {
        db.collection("User").whereEqualTo("id", auth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot doc : task.getResult()){
                    UserModel userModel = doc.toObject(UserModel.class);
                    textViewEmail.setText("Xin chào, " + userModel.getName());
                    Glide.with(UserFragment.this).load(userModel.getImg_url()).into(circleImageView);
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

        circleImageView = view.findViewById(R.id.circleImageViewUserFragment);
    }

}
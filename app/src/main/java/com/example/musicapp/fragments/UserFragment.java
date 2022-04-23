package com.example.musicapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.musicapp.R;
import com.example.musicapp.activities.LoginActivity;
import com.example.musicapp.activities.SongCRUDActivity;
import com.example.musicapp.activities.SongUploadActivity;
import com.example.musicapp.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class UserFragment extends Fragment {

    Button buttonContribute, buttonLogout, buttonCRUD;
    TextView textViewEmail;
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

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        textViewEmail = view.findViewById(R.id.textViewEmail);

        buttonCRUD = view.findViewById(R.id.buttonCRUD);
        buttonLogout = view.findViewById(R.id.buttonLogout);
        buttonContribute = view.findViewById(R.id.buttonContribute);

        db.collection("User").whereEqualTo("id", auth.getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot doc : task.getResult()){
                    UserModel userModel = doc.toObject(UserModel.class);
                    textViewEmail.setText("Xin chào, " + userModel.getName());
                }
            }
        });


        buttonContribute.setOnClickListener(view13 -> startActivity(new Intent(getContext(), SongUploadActivity.class)));

        buttonLogout.setOnClickListener(view12 -> {
            auth.signOut();
            startActivity(new Intent(getContext(), LoginActivity.class));
        });

        buttonCRUD.setOnClickListener(view1 -> startActivity(new Intent(getContext(), SongCRUDActivity.class)));

        return view;
    }
}
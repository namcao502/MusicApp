package com.example.musicapp.activities;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.musicapp.R;
import com.example.musicapp.Variables;
import com.example.musicapp.adapters.ListSongAdapter;
import com.example.musicapp.models.SongModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListSongActivity extends AppCompatActivity {

    RecyclerView recyclerViewListSong;
    List<SongModel> songModelList;
    ListSongAdapter listSongAdapter;
    List<String> songTitleList;
    FirebaseFirestore db;
    FirebaseAuth auth;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_song);

        getSupportActionBar().hide();

        recyclerViewListSong = findViewById(R.id.recyclerView_list_song);
        recyclerViewListSong.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        songModelList = new ArrayList<>();
        songTitleList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        listSongAdapter = new ListSongAdapter(getApplicationContext(), songModelList);
        recyclerViewListSong.setAdapter(listSongAdapter);

        //get data from intent
        Intent intent = getIntent();
        songTitleList = (List<String>) intent.getSerializableExtra(Variables.PLAYLIST_OBJECT);

        //get data from song collection
        for (String x : songTitleList){
            db.collection("Song")
                    .whereEqualTo("title", x).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot doc : task.getResult()){
                        SongModel songModel = doc.toObject(SongModel.class);
                        songModelList.add(songModel);
                        Log.i("TAG1", "list song activity: " + songModelList.toString());
                    }
                    listSongAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}
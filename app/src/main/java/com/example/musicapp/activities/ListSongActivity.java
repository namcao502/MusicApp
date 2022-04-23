package com.example.musicapp.activities;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.Variables;
import com.example.musicapp.adapters.ListSongAdapter;
import com.example.musicapp.adapters.ListSongDialogAdapter;
import com.example.musicapp.models.PlaylistModel;
import com.example.musicapp.models.SongModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListSongActivity extends AppCompatActivity {

    RecyclerView recyclerViewListSong;
    List<SongModel> songModelList;
    ListSongAdapter listSongAdapter;

    FirebaseFirestore db;
    FirebaseAuth auth;

    Toolbar toolbar;

    Intent intent;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_song_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_song);

        ViewBinding();

        LoadToolbar();

        if (GetDataFromIntent().equals("playlistIntent")){
            LoadAllSongFromPlaylist((List<String>) intent.getSerializableExtra("LIST_SONG_ID"));
        }

        if (GetDataFromIntent().equals("artistIntent")){
            String artistTitle = intent.getStringExtra(Variables.ARTIST_TITLE);
            LoadAllReceivedSongFromArtist(artistTitle);
            toolbar.setVisibility(View.GONE);
        }

        if (GetDataFromIntent().equals("genreIntent")){
            String genreTitle = intent.getStringExtra(Variables.GENRE_TITLE);
            LoadAllReceivedSongFromGenre(genreTitle);
            toolbar.setVisibility(View.GONE);
        }

        if (GetDataFromIntent().equals("countryIntent")){
            String countryTitle = intent.getStringExtra(Variables.COUNTRY_TITLE);
            LoadAllReceivedSongFromCountry(countryTitle);
            toolbar.setVisibility(View.GONE);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void LoadAllReceivedSongFromCountry(String countryTitle) {
        db.collection("Song").whereArrayContains("country", countryTitle).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot doc : task.getResult()){
                    SongModel songModel = doc.toObject(SongModel.class);
                    songModelList.add(songModel);
                }
                listSongAdapter.notifyDataSetChanged();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void LoadAllReceivedSongFromGenre(String genreTitle) {
        db.collection("Song").whereArrayContains("genre", genreTitle).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot doc : task.getResult()){
                    SongModel songModel = doc.toObject(SongModel.class);
                    songModelList.add(songModel);
                }
                listSongAdapter.notifyDataSetChanged();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void LoadAllReceivedSongFromArtist(String artistTitle) {
        db.collection("Song").whereArrayContains("artist", artistTitle).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot doc : task.getResult()){
                    SongModel songModel = doc.toObject(SongModel.class);
                    songModelList.add(songModel);
                }
                listSongAdapter.notifyDataSetChanged();
            }
        });
    }

    private String GetDataFromIntent() {
        //get data from intent
        intent = getIntent();
        return intent.getStringExtra(Variables.INTENT_TYPE);
    }

    private void ViewBinding() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        recyclerViewListSong = findViewById(R.id.recyclerView_list_song);
        toolbar = findViewById(R.id.list_song_toolbar);

        recyclerViewListSong.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        songModelList = new ArrayList<>();
        listSongAdapter = new ListSongAdapter(getApplicationContext(), songModelList, getIntent());
        recyclerViewListSong.setAdapter(listSongAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void LoadToolbar() {
        //toolbar

        toolbar.setTitle("Danh sách bài hát");
        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.playlist_menu_add) {
                Dialog dialog = new Dialog(ListSongActivity.this);
                dialog.setContentView(R.layout.activity_list_song_add_new_song_dialog);

                RecyclerView recyclerViewAddNewSongDialog = dialog.findViewById(R.id.recyclerView_list_song_dialog);
                Button buttonAddAll = dialog.findViewById(R.id.buttonAddNewListDialogAddAll);

                recyclerViewAddNewSongDialog.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                List<SongModel> songModelListTemp = new ArrayList<>();
                ListSongDialogAdapter listSongDialogAdapter = new ListSongDialogAdapter(getApplicationContext(), songModelListTemp, intent, dialog);
                recyclerViewAddNewSongDialog.setAdapter(listSongDialogAdapter);

                //load all song in db for picking
                db.collection("Song").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            SongModel songModel = doc.toObject(SongModel.class);
                            songModelListTemp.add(songModel);
                        }
                        listSongDialogAdapter.notifyDataSetChanged();
                    }
                });

                buttonAddAll.setOnClickListener(view -> {
                    //get playlist title, playlist id from intent
                    String playlistID = intent.getStringExtra(Variables.PLAYLIST_ID);

                    //get song_id that already in playlist
                    final List<String>[] songIdFirst = new List[]{new ArrayList<>()};

                    DocumentReference documentReference = db.collection("Playlist")
                            .document(auth.getCurrentUser().getUid()).collection("User").document(playlistID);
                    documentReference.get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.get("song_id") != null) {
                            PlaylistModel playlistModel = documentSnapshot.toObject(PlaylistModel.class);
                            songIdFirst[0] = playlistModel.getSong_id();
                        }
                    });

                    if (songModelListTemp != null){
                        for (SongModel x : songModelListTemp) {
                            songIdFirst[0].add(x.getId());
                        }
                    }

                    db.collection("Playlist").document(Objects.requireNonNull(auth.getUid()))
                            .collection("User").document(playlistID).update("song_id", songIdFirst[0]).addOnCompleteListener(task -> {
                        Toast.makeText(ListSongActivity.this, "Thêm tất cả bài hát thành công", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(ListSongActivity.this, "Thêm tất cả bài hát thất bại", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    });

                });

                dialog.show();
            }
            return true;
        });
    }
    @SuppressLint("NotifyDataSetChanged")
    private void LoadAllSongFromPlaylist(List<String> songIdList) {

        //get data from song collection
        if (songIdList != null){
            for (String x : songIdList){
                db.collection("Song")
                        .whereEqualTo("id", x).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        for (QueryDocumentSnapshot doc : task.getResult()){
                            SongModel songModel = doc.toObject(SongModel.class);
                            songModelList.add(songModel);
                        }
                        listSongAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
    }
}